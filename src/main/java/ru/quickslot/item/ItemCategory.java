package ru.quickslot.item;

import java.util.Locale;

public enum ItemCategory {
    SWORD("Меч"),
    BLOCKS("Блоки"),
    GOLDEN_APPLE("Золотые яблоки"),
    SHEARS("Ножницы"),
    PICKAXE("Кирка"),
    AXE("Топор"),
    BOW("Лук"),
    ARROWS("Стрелы"),
    TNT("TNT"),
    FIREBALL("Fireball"),
    ENDER_PEARL("Эндер-жемчуг"),
    LADDER("Лестницы"),
    WATER_BUCKET("Вода"),
    EMPTY("Пусто"),
    IGNORE("Не трогать");

    private final String displayName;

    ItemCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemCategory next() {
        ItemCategory[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

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
