package ru.quickslot.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class ResourceCounter {
    private ResourceCounter() {}

    public static int count(InventoryPlayer inventory, Item item) {
        int total = 0;
        for (ItemStack stack : inventory.mainInventory) {
            if (stack != null && stack.getItem() == item) total += stack.stackSize;
        }
        return total;
    }

    public static int iron(InventoryPlayer inventory) { return count(inventory, Items.iron_ingot); }
    public static int gold(InventoryPlayer inventory) { return count(inventory, Items.gold_ingot); }
    public static int diamond(InventoryPlayer inventory) { return count(inventory, Items.diamond); }
    public static int emerald(InventoryPlayer inventory) { return count(inventory, Items.emerald); }
}
