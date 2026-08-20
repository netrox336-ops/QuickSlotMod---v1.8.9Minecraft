package ru.quickslot.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.quickslot.config.QuickSlotConfig;
import ru.quickslot.item.ItemCategory;
import ru.quickslot.item.ItemClassifier;

public final class InventoryManager {
    private static final int MAIN_FIRST = 9;
    private static final int MAIN_LAST = 35;
    private static final int HOTBAR_FIRST = 36;
    private static final int HOTBAR_LAST = 44;
    private static final int ACTION_COOLDOWN_TICKS = 2;

    private final Minecraft minecraft = Minecraft.getMinecraft();
    private final QuickSlotConfig config;
    private int cooldown;

    public InventoryManager(QuickSlotConfig config) {
        this.config = config;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !config.isEnabled()) return;
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        EntityPlayerSP player = minecraft.thePlayer;
        if (player == null || minecraft.theWorld == null || player.openContainer != player.inventoryContainer) return;
        Container container = player.inventoryContainer;

        if (config.isRemoveResourcesFromHotbar() && moveOneResourceOutOfHotbar(player, container)) {
            cooldown = ACTION_COOLDOWN_TICKS;
            return;
        }
        if (organizeOneSlot(player, container)) cooldown = ACTION_COOLDOWN_TICKS;
    }

    private boolean moveOneResourceOutOfHotbar(EntityPlayerSP player, Container container) {
        if (!hasFreeMainInventorySpace(container)) return false;
        for (int slotNumber = HOTBAR_FIRST; slotNumber <= HOTBAR_LAST; slotNumber++) {
            Slot slot = container.getSlot(slotNumber);
            if (slot.getHasStack() && ItemClassifier.isResource(slot.getStack())) {
                minecraft.playerController.windowClick(container.windowId, slotNumber, 0, 1, player);
                return true;
            }
        }
        return false;
    }

    private boolean organizeOneSlot(EntityPlayerSP player, Container container) {
        for (int hotbarIndex = 0; hotbarIndex < 9; hotbarIndex++) {
            ItemCategory rule = config.getRule(hotbarIndex);
            if (rule == ItemCategory.IGNORE) continue;

            int targetSlotNumber = HOTBAR_FIRST + hotbarIndex;
            Slot target = container.getSlot(targetSlotNumber);
            ItemStack current = target.getStack();

            if (rule == ItemCategory.EMPTY) {
                if (current != null && hasFreeMainInventorySpace(container)) {
                    minecraft.playerController.windowClick(container.windowId, targetSlotNumber, 0, 1, player);
                    return true;
                }
                continue;
            }

            int bestSource = findBestSource(container, rule, targetSlotNumber);
            if (bestSource < 0) continue;

            ItemStack bestStack = container.getSlot(bestSource).getStack();
            if (ItemClassifier.matches(current, rule)
                    && ItemClassifier.priority(current, rule) >= ItemClassifier.priority(bestStack, rule)) continue;

            minecraft.playerController.windowClick(container.windowId, bestSource, hotbarIndex, 2, player);
            return true;
        }
        return false;
    }

    private int findBestSource(Container container, ItemCategory category, int targetSlotNumber) {
        int bestSlot = -1;
        int bestPriority = Integer.MIN_VALUE;
        for (int slotNumber = MAIN_FIRST; slotNumber <= HOTBAR_LAST; slotNumber++) {
            if (slotNumber == targetSlotNumber) continue;
            ItemStack stack = container.getSlot(slotNumber).getStack();
            if (!ItemClassifier.matches(stack, category)) continue;
            int priority = ItemClassifier.priority(stack, category);
            if (priority > bestPriority) {
                bestPriority = priority;
                bestSlot = slotNumber;
            }
        }
        return bestSlot;
    }

    private boolean hasFreeMainInventorySpace(Container container) {
        for (int slotNumber = MAIN_FIRST; slotNumber <= MAIN_LAST; slotNumber++) {
            if (!container.getSlot(slotNumber).getHasStack()) return true;
        }
        return false;
    }
}
