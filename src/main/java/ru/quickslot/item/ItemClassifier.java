package ru.quickslot.item;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public final class ItemClassifier {
    private ItemClassifier() {}

    public static boolean matches(ItemStack stack, ItemCategory category) {
        if (stack == null) return category == ItemCategory.EMPTY;
        Item item = stack.getItem();
        switch (category) {
            case SWORD: return item instanceof ItemSword;
            case BLOCKS:
                return item instanceof ItemBlock
                        && item != Item.getItemFromBlock(Blocks.tnt)
                        && item != Item.getItemFromBlock(Blocks.ladder);
            case GOLDEN_APPLE: return item == Items.golden_apple;
            case SHEARS: return item == Items.shears;
            case PICKAXE: return item instanceof ItemPickaxe;
            case AXE: return item instanceof ItemAxe;
            case BOW: return item == Items.bow;
            case ARROWS: return item == Items.arrow;
            case TNT: return item == Item.getItemFromBlock(Blocks.tnt);
            case FIREBALL: return item == Items.fire_charge;
            case ENDER_PEARL: return item == Items.ender_pearl;
            case LADDER: return item == Item.getItemFromBlock(Blocks.ladder);
            case WATER_BUCKET: return item == Items.water_bucket;
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

        if (category == ItemCategory.SWORD) {
            if (item == Items.diamond_sword) return 400;
            if (item == Items.iron_sword) return 300;
            if (item == Items.stone_sword) return 200;
            if (item == Items.wooden_sword) return 100;
        }

        if (category == ItemCategory.PICKAXE) {
            if (item == Items.diamond_pickaxe) return 500;
            if (item == Items.iron_pickaxe) return 400;
            if (item == Items.stone_pickaxe) return 300;
            if (item == Items.golden_pickaxe) return 200;
            if (item == Items.wooden_pickaxe) return 100;
        }

        if (category == ItemCategory.AXE) {
            if (item == Items.diamond_axe) return 500;
            if (item == Items.iron_axe) return 400;
            if (item == Items.stone_axe) return 300;
            if (item == Items.golden_axe) return 200;
            if (item == Items.wooden_axe) return 100;
        }

        return stack.stackSize;
    }
}
