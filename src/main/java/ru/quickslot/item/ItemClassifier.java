package ru.quickslot.item;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public final class ItemClassifier {
    private ItemClassifier() {}

    public static boolean matches(ItemStack stack, ItemCategory category) {
        if (stack == null) return category == ItemCategory.EMPTY;
        Item item = stack.getItem();
        switch (category) {
            case SWORD: return item instanceof ItemSword;
            case BLOCKS: return item instanceof ItemBlock;
            case GOLDEN_APPLE: return item == Items.golden_apple;
            case SHEARS: return item == Items.shears;
            case PICKAXE: return item instanceof ItemPickaxe;
            case AXE: return item instanceof ItemAxe;
            case EMPTY: return false;
            case IGNORE: return true;
            default: return false;
        }
    }

    public static boolean isResource(ItemStack stack) {
        if (stack == null) return false;
        Item item = stack.getItem();
        return item == Items.iron_ingot || item == Items.gold_ingot || item == Items.diamond || item == Items.emerald;
    }

    public static int priority(ItemStack stack, ItemCategory category) {
        if (stack == null) return Integer.MIN_VALUE;
        Item item = stack.getItem();
        if (category == ItemCategory.SWORD && item instanceof ItemSword) {
            return (int) (((ItemSword) item).getDamageVsEntity() * 100.0F);
        }
        if ((category == ItemCategory.PICKAXE || category == ItemCategory.AXE) && item instanceof ItemTool) {
            return (int) (((ItemTool) item).getToolMaterial().getEfficiencyOnProperMaterial() * 100.0F);
        }
        return stack.stackSize;
    }
}
