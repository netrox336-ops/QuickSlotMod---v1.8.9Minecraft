package ru.quickslot.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.quickslot.config.QuickSlotConfig;
import ru.quickslot.config.RefillMode;
import ru.quickslot.item.ItemCategory;
import ru.quickslot.item.ItemClassifier;

public final class InventoryManager {
    private static final int MAIN_FIRST = 9;
    private static final int MAIN_LAST = 35;
    private static final int HOTBAR_FIRST = 36;
    private static final int HOTBAR_LAST = 44;
    private static final int ACTION_COOLDOWN_TICKS = 2;
    private static final int MANUAL_GRACE_TICKS = 10;

    private final Minecraft minecraft = Minecraft.getMinecraft();
    private final QuickSlotConfig config;
    private final ItemStack[] lastPreferredStacks = new ItemStack[9];
    private int cooldown;
    private int manualGraceTicks;
    private boolean wasContainerOpen;

    public InventoryManager(QuickSlotConfig config) {
        this.config = config;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!config.isEnabled()
                && !config.isRemoveResourcesFromHotbar()
                && !config.isStackConsolidationEnabled()) return;

        EntityPlayerSP player = minecraft.thePlayer;
        if (player == null || minecraft.theWorld == null) return;

        boolean containerOpen = minecraft.currentScreen instanceof GuiContainer;
        if (containerOpen) {
            wasContainerOpen = true;
            return;
        }

        if (wasContainerOpen) {
            wasContainerOpen = false;
            manualGraceTicks = config.isManualGraceEnabled() ? MANUAL_GRACE_TICKS : 0;
        }

        if (manualGraceTicks > 0) {
            manualGraceTicks--;
            return;
        }

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        if (player.openContainer != player.inventoryContainer) return;

        Container container = player.inventoryContainer;
        rememberPreferredStacks(container);

        if (config.isRemoveResourcesFromHotbar() && moveOneResourceOutOfHotbar(player, container)) {
            cooldown = ACTION_COOLDOWN_TICKS;
            return;
        }
        if (config.isEnabled() && organizeOneSlot(player, container)) {
            cooldown = ACTION_COOLDOWN_TICKS;
            return;
        }
        if (config.isStackConsolidationEnabled() && consolidateOneStack(player, container)) {
            cooldown = ACTION_COOLDOWN_TICKS;
        }
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

    private boolean moveOneResourceOutOfHotbar(EntityPlayerSP player, Container container) {
        int protectedSlotNumber = protectedHotbarSlot(player);
        for (int slotNumber = HOTBAR_FIRST; slotNumber <= HOTBAR_LAST; slotNumber++) {
            if (slotNumber == protectedSlotNumber) continue;
            Slot slot = container.getSlot(slotNumber);
            if (slot.getHasStack() && ItemClassifier.isResource(slot.getStack()) && canMoveToMain(container, slot.getStack())) {
                minecraft.playerController.windowClick(container.windowId, slotNumber, 0, 1, player);
                return true;
            }
        }
        return false;
    }

    private boolean organizeOneSlot(EntityPlayerSP player, Container container) {
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
                    minecraft.playerController.windowClick(container.windowId, targetSlotNumber, 0, 1, player);
                    return true;
                }
                continue;
            }

            if (ItemClassifier.matches(current, rule)) {
                if (isUpgradeable(rule)) {
                    int betterSource = findBestSource(container, rule, targetSlotNumber, hotbarIndex, protectedSlotNumber);
                    if (betterSource >= 0) {
                        ItemStack better = container.getSlot(betterSource).getStack();
                        if (ItemClassifier.priority(better, rule) > ItemClassifier.priority(current, rule)) {
                            minecraft.playerController.windowClick(container.windowId, betterSource, hotbarIndex, 2, player);
                            return true;
                        }
                    }
                } else if (config.isRefillEnabled(hotbarIndex) && shouldRefill(current)) {
                    int mergeSource = findMergeSource(container, current);
                    if (mergeSource >= 0) {
                        mergeStacks(player, container, mergeSource, targetSlotNumber);
                        return true;
                    }
                }
                continue;
            }

            if (current == null && !config.isRefillEnabled(hotbarIndex)) continue;

            int bestSource = findBestSource(container, rule, targetSlotNumber, hotbarIndex, protectedSlotNumber);
            if (bestSource < 0) continue;

            minecraft.playerController.windowClick(container.windowId, bestSource, hotbarIndex, 2, player);
            return true;
        }
        return false;
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

    private boolean consolidateOneStack(EntityPlayerSP player, Container container) {
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

        if (bestTarget < 0 || bestSource < 0) return false;
        mergeStacks(player, container, bestSource, bestTarget);
        return true;
    }

    private void mergeStacks(EntityPlayerSP player, Container container, int sourceSlot, int targetSlot) {
        minecraft.playerController.windowClick(container.windowId, sourceSlot, 0, 0, player);
        minecraft.playerController.windowClick(container.windowId, targetSlot, 0, 0, player);
        minecraft.playerController.windowClick(container.windowId, sourceSlot, 0, 0, player);
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

            int priority = ItemClassifier.priority(stack, category);
            if (preferred != null && sameStackKind(stack, preferred)) priority += 100000;

            if (priority > bestPriority) {
                bestPriority = priority;
                bestSlot = slotNumber;
            }
        }
        return bestSlot;
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
        if (first == null || second == null) return false;
        if (first.getItem() != second.getItem() || first.getItemDamage() != second.getItemDamage()) return false;
        if (first.hasTagCompound() != second.hasTagCompound()) return false;
        return !first.hasTagCompound() || first.getTagCompound().equals(second.getTagCompound());
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
