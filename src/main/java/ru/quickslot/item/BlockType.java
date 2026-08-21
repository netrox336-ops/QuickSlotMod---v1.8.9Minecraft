package ru.quickslot.item;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Locale;

public enum BlockType {
    WOOL("Шерсть"),
    PLANKS("Доски"),
    END_STONE("Эндерняк"),
    CLAY("Глина"),
    GLASS("Стекло"),
    OBSIDIAN("Обсидиан"),
    OTHER("Другое");

    private final String displayName;

    BlockType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BlockType fromStack(ItemStack stack) {
        if (stack == null) return OTHER;
        Item item = stack.getItem();
        if (item == Item.getItemFromBlock(Blocks.wool)) return WOOL;
        if (item == Item.getItemFromBlock(Blocks.planks)) return PLANKS;
        if (item == Item.getItemFromBlock(Blocks.end_stone)) return END_STONE;
        if (item == Item.getItemFromBlock(Blocks.stained_hardened_clay)
                || item == Item.getItemFromBlock(Blocks.hardened_clay)) return CLAY;
        if (item == Item.getItemFromBlock(Blocks.glass)
                || item == Item.getItemFromBlock(Blocks.stained_glass)) return GLASS;
        if (item == Item.getItemFromBlock(Blocks.obsidian)) return OBSIDIAN;
        return OTHER;
    }

    public static BlockType fromConfig(String value, BlockType fallback) {
        if (value == null) return fallback;
        try {
            return BlockType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }
}
