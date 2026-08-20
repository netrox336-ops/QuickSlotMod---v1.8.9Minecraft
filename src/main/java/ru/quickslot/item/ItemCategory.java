package ru.quickslot.item;

import java.util.Locale;

public enum ItemCategory {
    SWORD,
    BLOCKS,
    GOLDEN_APPLE,
    SHEARS,
    PICKAXE,
    AXE,
    EMPTY,
    IGNORE;

    public static ItemCategory fromConfig(String value, ItemCategory fallback) {
        if (value == null) return fallback;
        try {
            return ItemCategory.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    public static String availableValues() {
        StringBuilder builder = new StringBuilder();
        for (ItemCategory value : values()) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(value.name());
        }
        return builder.toString();
    }
}
