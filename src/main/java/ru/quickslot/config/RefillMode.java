package ru.quickslot.config;

import java.util.Locale;

public enum RefillMode {
    EMPTY_ONLY("Когда пусто"),
    BELOW_THRESHOLD("Ниже порога"),
    ALWAYS_MAX("Держать максимум");

    private final String displayName;

    RefillMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public RefillMode next() {
        RefillMode[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

    public static RefillMode fromConfig(String value, RefillMode fallback) {
        if (value == null) return fallback;
        try {
            return RefillMode.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }
}
