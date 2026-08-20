package ru.quickslot.config;

import java.util.Locale;

public enum ProfileType {
    NORMAL("Обычный"),
    RUSH("Rush"),
    BRIDGE("Bridge");

    private final String displayName;

    ProfileType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getConfigCategory() {
        return "Профиль." + displayName;
    }

    public static ProfileType fromConfig(String value, ProfileType fallback) {
        if (value == null) return fallback;
        try {
            return ProfileType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }
}
