package ru.quickslot.inventory.action;

import net.minecraft.item.ItemStack;

public final class StackSnapshot {
    private final ItemStack stack;

    private StackSnapshot(ItemStack stack) {
        this.stack = stack == null ? null : stack.copy();
    }

    public static StackSnapshot capture(ItemStack stack) {
        return new StackSnapshot(stack);
    }

    public static StackSnapshot empty() {
        return new StackSnapshot(null);
    }

    public boolean matchesExactly(ItemStack current) {
        if (stack == null) return current == null;
        if (current == null) return false;
        return sameKind(stack, current) && stack.stackSize == current.stackSize;
    }

    public ItemStack copyStack() {
        return stack == null ? null : stack.copy();
    }

    public String fingerprint() {
        if (stack == null) return "empty";
        String tag = stack.hasTagCompound() ? stack.getTagCompound().toString() : "";
        return stack.getItem().getUnlocalizedName() + ":" + stack.getItemDamage() + ":" + stack.stackSize + ":" + tag;
    }

    public static boolean sameKind(ItemStack first, ItemStack second) {
        if (first == null || second == null) return false;
        if (first.getItem() != second.getItem()) return false;
        if (first.getItemDamage() != second.getItemDamage()) return false;
        if (first.hasTagCompound() != second.hasTagCompound()) return false;
        return !first.hasTagCompound() || first.getTagCompound().equals(second.getTagCompound());
    }
}
