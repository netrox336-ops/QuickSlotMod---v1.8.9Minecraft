package ru.quickslot.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.quickslot.config.ProfileType;
import ru.quickslot.config.QuickSlotConfig;
import ru.quickslot.config.RefillMode;
import ru.quickslot.inventory.action.HotbarSwapAction;
import ru.quickslot.inventory.action.InventoryAction;
import ru.quickslot.inventory.action.MergeStacksAction;
import ru.quickslot.inventory.action.ShiftClickAction;
import ru.quickslot.inventory.action.StackSnapshot;
import ru.quickslot.item.BlockType;
import ru.quickslot.item.ItemCategory;
import ru.quickslot.item.ItemClassifier;

import java.util.Arrays;

public final class InventoryManager {
    private static final int MAIN_FIRST = 9;
    private static final int MAIN_LAST = 35;
    private static final int HOTBAR_FIRST = 36;
    private static final int HOTBAR_LAST = 44;
    private static final int MANUAL_GRACE_TICKS = 10;

    private final Minecraft minecraft = Minecraft.getMinecraft();
    private final QuickSlotConfig config;
    private final InventoryActionQueue actionQueue = new InventoryActionQueue();
    private final ItemStack[] lastPreferredStacks = new ItemStack[9];

    private int manualGraceTicks;
    private boolean wasContainerOpen;
    private ProfileType rememberedProfile;
    private EntityPlayerSP rememberedPlayer;
    private int rememberedWindowId = -1;

    public InventoryManager(QuickSlotConfig config) {
        this.config = config;
        this.rememberedProfile = config.getActiveProfile();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        EntityPlayerSP player = minecraft.thePlayer;
        if (player == null || minecraft.theWorld == null) {
            resetRuntime(null);
            return;
        }

        if (rememberedPlayer != player) {
            resetRuntime(player);
        }

        if (player.isDead) {
            actionQueue.clear();
            Arrays.fill(lastPreferredStacks, null);
            rememberedWindowId = -1;
            return;
        }

        if (rememberedProfile != config.getActiveProfile()) {
            rememberedProfile = config.getActiveProfile();
            Arrays.fill(lastPreferredStacks, null);
            actionQueue.clear();
        }

        boolean containerOpen = minecraft.currentScreen instanceof GuiContainer;
        if (containerOpen) {
            wasContainerOpen = true;
            actionQueue.clear();
            return;
        }

        if (wasContainerOpen) {
            wasContainerOpen = false;
            actionQueue.clear();
            manualGraceTicks = config.isManualGraceEnabled() ? MANUAL_GRACE_TICKS : 0;
        }

        if (manualGraceTicks > 0) {
            manualGraceTicks--;
            return;
        }

        if (player.openContainer != player.inventoryContainer) {
            actionQueue.clear();
            rememberedWindowId = -1;
            return;
        }

        Container container = player.inventoryContainer;
        if (rememberedWindowId != container.windowId) {
            actionQueue.clear();
            rememberedWindowId = container.windowId;
        }

        actionQueue.tick(minecraft, player, container);
        if (!actionQueue.canPlan()) return;

        if (!config.isEnabled()
                && !config.isRemoveResourcesFromHotbar()
                && !config.isStackConsolidationEnabled()) return;

        rememberPreferredStacks(container);

        InventoryAction next = null;
        if (config.isRemoveResourcesFromHotbar()) {
            next = planMoveOneResourceOutOfHotbar(player, container);
        }
        if (next == null && config.isEnabled()) {
            next = planOrganizeOneSlot(player, container);
        }
        if (next == null && config.isStackConsolidationEnabled()) {
            next = planConsolidateOneStack(container);
        }

        if (next != null && actionQueue.enqueue(next)) {
            actionQueue.tick(minecraft, player, container);
        }
    }

    private void resetRuntime(EntityPlayerSP player) {
        actionQueue.clear();
        Arrays.fill(lastPreferredStacks, null);
        manualGraceTicks = 0;
        wasContainerOpen = false;
        rememberedWindowId = -1;
        rememberedPlayer = player;
        rememberedProfile = config.getActiveProfile();
    }

    private void rememberPreferredStacks(Container container) {
        for (int hotbarIndex = 0; hotbarIndex < 9; hotbarIndex++) {
            ItemCategory rule = config.getRule(hotbarIndex);
            ItemStack current = container.getSlot(HOTBAR_FIRST + hotbarIndex).getStack();
            if (current == null || !ItemClassifier.matches(current, rule)) continue;

            ItemStack remembered = current.copy();
            remembered.stackSize = 1;
            lastPreferredStacks[hotbarIndex] = remembered;
        }
    }

    private InventoryAction planMoveOneResourceOutOfHotbar(EntityPlayerSP player, Container container) {
        int protectedSlotNumber = protectedHotbarSlot(player);
        for (int slotNumber = HOTBAR_FIRST; slotNumber <= HOTBAR_LAST; slotNumber++) {
            if (slotNumber == protectedSlotNumber) continue;
            Slot slot = container.getSlot(slotNumber);
            if (slot.getHasStack() && ItemClassifier.isResource(slot.getStack()) && canMoveToMain(container, slot.getStack())) {
                return new ShiftClickAction(container, slotNumber, "resource");
            }
        }
        return null;
    }

    private InventoryAction planOrganizeOneSlot(EntityPlayerSP player, Container container) {
        int protectedSlotNumber = protectedHotbarSlot(player);

        for (int hotbarIndex = 0; hotbarIndex < 9; hotbarIndex++) {
            ItemCategory rule = config.getRule(hotbarIndex);
            if (rule == ItemCategory.IGNORE) continue;

            int targetSlotNumber = HOTBAR_FIRST + hotbarIndex;
            if (targetSlotNumber == protectedSlotNumber) continue;

            Slot target = container.getSlot(targetSlotNumber);
            ItemStack current = target.getStack();

            if (rule == ItemCategory.EMPTY) {
                if (current != null && hasFreeMainInventorySpace(container)) {
                    return new ShiftClickAction(container, targetSlotNumber, "empty-slot");
                }
                continue;
            }

            if (ItemClassifier.matches(current, rule)) {
                if (rule == ItemCategory.BLOCKS && !config.isPreferSameBlock()) {
                    int preferredSource = findBestSource(container, rule, targetSlotNumber, hotbarIndex, protectedSlotNumber);
                    if (preferredSource >= 0) {
                        ItemStack preferredBlock = container.getSlot(preferredSource).getStack();
                        if (blockRank(preferredBlock) < blockRank(current)) {
                            return new HotbarSwapAction(container, preferredSource, hotbarIndex, "block-priority");
                        }
                    }
                }

                if (isUpgradeable(rule) && config.isAutoUpgrade(rule)) {
                    int betterSource = findBestSource(container, rule, targetSlotNumber, hotbarIndex, protectedSlotNumber);
                    if (betterSource >= 0) {
                        ItemStack better = container.getSlot(betterSource).getStack();
                        if (ItemClassifier.priority(better, rule) > ItemClassifier.priority(current, rule)) {
                            return new HotbarSwapAction(container, betterSource, hotbarIndex, "upgrade");
                        }
                    }
                } else if (!isUpgradeable(rule) && config.isRefillEnabled(hotbarIndex) && shouldRefill(current)) {
                    int mergeSource = findMergeSource(container, current);
                    if (mergeSource >= 0) {
                        return new MergeStacksAction(container, mergeSource, targetSlotNumber, "refill");
                    }
                }
                continue;
            }

            if (current == null && !config.isRefillEnabled(hotbarIndex)) continue;

            int bestSource = findBestSource(container, rule, targetSlotNumber, hotbarIndex, protectedSlotNumber);
            if (bestSource < 0) continue;

            return new HotbarSwapAction(container, bestSource, hotbarIndex, "organize");
        }
        return null;
    }

    private boolean shouldRefill(ItemStack current) {
        if (current == null || current.getMaxStackSize() <= 1 || current.stackSize >= current.getMaxStackSize()) return false;

        RefillMode mode = config.getRefillMode();
        if (mode == RefillMode.ALWAYS_MAX) return true;
        if (mode == RefillMode.BELOW_THRESHOLD) {
            return current.stackSize < Math.min(config.getRefillThreshold(), current.getMaxStackSize());
        }
        return false;
    }

    private InventoryAction planConsolidateOneStack(Container container) {
        int bestTarget = -1;
        int bestSource = -1;
        int bestTargetSize = -1;

        for (int targetSlot = MAIN_FIRST; targetSlot <= MAIN_LAST; targetSlot++) {
            ItemStack target = container.getSlot(targetSlot).getStack();
            if (target == null || target.getMaxStackSize() <= 1 || target.stackSize >= target.getMaxStackSize()) continue;

            for (int sourceSlot = MAIN_FIRST; sourceSlot <= MAIN_LAST; sourceSlot++) {
                if (sourceSlot == targetSlot) continue;
                ItemStack source = container.getSlot(sourceSlot).getStack();
                if (source == null || !sameStackKind(source, target)) continue;

                if (target.stackSize > bestTargetSize) {
                    bestTargetSize = target.stackSize;
                    bestTarget = targetSlot;
                    bestSource = sourceSlot;
                }
            }
        }

        if (bestTarget < 0 || bestSource < 0) return null;
        return new MergeStacksAction(container, bestSource, bestTarget, "consolidate");
    }

    private boolean isUpgradeable(ItemCategory category) {
        return category == ItemCategory.SWORD
                || category == ItemCategory.PICKAXE
                || category == ItemCategory.AXE;
    }

    private int findBestSource(Container container, ItemCategory category, int targetSlotNumber, int targetHotbarIndex, int protectedSlotNumber) {
        int bestSlot = -1;
        int bestPriority = Integer.MIN_VALUE;
        ItemStack preferred = category == ItemCategory.BLOCKS ? lastPreferredStacks[targetHotbarIndex] : null;

        for (int slotNumber = MAIN_FIRST; slotNumber <= HOTBAR_LAST; slotNumber++) {
            if (slotNumber == targetSlotNumber || slotNumber == protectedSlotNumber) continue;
            ItemStack stack = container.getSlot(slotNumber).getStack();
            if (!ItemClassifier.matches(stack, category)) continue;

            if (slotNumber >= HOTBAR_FIRST) {
                int sourceHotbarIndex = slotNumber - HOTBAR_FIRST;
                ItemCategory sourceRule = config.getRule(sourceHotbarIndex);
                if (sourceRule == category) continue;
            }

            int priority = sourcePriority(stack, category, preferred);
            if (priority > bestPriority) {
                bestPriority = priority;
                bestSlot = slotNumber;
            }
        }
        return bestSlot;
    }

    private int sourcePriority(ItemStack stack, ItemCategory category, ItemStack preferred) {
        if (category != ItemCategory.BLOCKS) return ItemClassifier.priority(stack, category);

        int rank = blockRank(stack);
        int priority = (BlockType.values().length - rank) * 10000 + stack.stackSize;
        if (config.isPreferSameBlock() && preferred != null && sameStackKind(stack, preferred)) {
            priority += 1000000;
        }
        return priority;
    }

    private int blockRank(ItemStack stack) {
        return config.getBlockPriorityRank(BlockType.fromStack(stack));
    }

    private int findMergeSource(Container container, ItemStack target) {
        int bestSlot = -1;
        int bestSize = -1;
        for (int slotNumber = MAIN_FIRST; slotNumber <= MAIN_LAST; slotNumber++) {
            ItemStack stack = container.getSlot(slotNumber).getStack();
            if (stack == null || !sameStackKind(stack, target)) continue;
            if (stack.stackSize > bestSize) {
                bestSize = stack.stackSize;
                bestSlot = slotNumber;
            }
        }
        return bestSlot;
    }

    private boolean sameStackKind(ItemStack first, ItemStack second) {
        return StackSnapshot.sameKind(first, second);
    }

    private int protectedHotbarSlot(EntityPlayerSP player) {
        if (!config.isProtectSelectedSlot()) return -1;
        return HOTBAR_FIRST + player.inventory.currentItem;
    }

    private boolean canMoveToMain(Container container, ItemStack resource) {
        for (int slotNumber = MAIN_FIRST; slotNumber <= MAIN_LAST; slotNumber++) {
            ItemStack stack = container.getSlot(slotNumber).getStack();
            if (stack == null) return true;
            if (sameStackKind(stack, resource) && stack.stackSize < stack.getMaxStackSize()) return true;
        }
        return false;
    }

    private boolean hasFreeMainInventorySpace(Container container) {
        for (int slotNumber = MAIN_FIRST; slotNumber <= MAIN_LAST; slotNumber++) {
            if (!container.getSlot(slotNumber).getHasStack()) return true;
        }
        return false;
    }
}
