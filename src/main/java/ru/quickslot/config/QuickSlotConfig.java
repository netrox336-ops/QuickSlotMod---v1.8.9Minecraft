package ru.quickslot.config;

import net.minecraftforge.common.config.Configuration;
import ru.quickslot.item.ItemCategory;

import java.io.File;

public final class QuickSlotConfig {
    private static final ItemCategory[] NORMAL_HOTBAR = {
            ItemCategory.SWORD,
            ItemCategory.BLOCKS,
            ItemCategory.EMPTY,
            ItemCategory.GOLDEN_APPLE,
            ItemCategory.SHEARS,
            ItemCategory.PICKAXE,
            ItemCategory.AXE,
            ItemCategory.IGNORE,
            ItemCategory.IGNORE
    };

    private static final ItemCategory[] RUSH_HOTBAR = {
            ItemCategory.SWORD,
            ItemCategory.BLOCKS,
            ItemCategory.TNT,
            ItemCategory.GOLDEN_APPLE,
            ItemCategory.SHEARS,
            ItemCategory.PICKAXE,
            ItemCategory.AXE,
            ItemCategory.FIREBALL,
            ItemCategory.ENDER_PEARL
    };

    private static final ItemCategory[] BRIDGE_HOTBAR = {
            ItemCategory.SWORD,
            ItemCategory.BLOCKS,
            ItemCategory.BLOCKS,
            ItemCategory.GOLDEN_APPLE,
            ItemCategory.SHEARS,
            ItemCategory.PICKAXE,
            ItemCategory.AXE,
            ItemCategory.FIREBALL,
            ItemCategory.ENDER_PEARL
    };

    private final Configuration configuration;
    private final ItemCategory[][] profileRules = new ItemCategory[ProfileType.values().length][9];

    private boolean enabled;
    private boolean resourceHudEnabled;
    private boolean statusHudEnabled;
    private boolean removeResourcesFromHotbar;
    private boolean protectSelectedSlot;
    private int hudX;
    private int hudY;
    private float hudScale;
    private ProfileType activeProfile;
    private RefillMode refillMode;
    private int refillThreshold;

    public QuickSlotConfig(File file) {
        this.configuration = new Configuration(file);
    }

    public void load() {
        enabled = configuration.getBoolean("Включен", "Основное", true, "Включает автоматическую работу QuickSlot.");
        removeResourcesFromHotbar = configuration.getBoolean("Убирать ресурсы из хотбара", "Основное", true, "Железо, золото, алмазы и изумруды будут переноситься в основной инвентарь.");
        protectSelectedSlot = configuration.getBoolean("Защищать выбранный слот", "Основное", true, "QuickSlot не будет переставлять предмет в выбранном игроком слоте хотбара.");

        resourceHudEnabled = configuration.getBoolean("Показывать HUD ресурсов", "HUD", true, "Показывает количество ресурсов во всём инвентаре.");
        statusHudEnabled = configuration.getBoolean("Показывать состояние QuickSlot", "HUD", true, "Показывает активный профиль и состояние автосортировки.");
        hudX = configuration.getInt("X", "HUD", 8, 0, 10000, "Положение HUD по горизонтали.");
        hudY = configuration.getInt("Y", "HUD", 8, 0, 10000, "Положение HUD по вертикали.");
        hudScale = (float) configuration.get("HUD", "Масштаб", 1.0D, "Масштаб HUD.", 0.5D, 3.0D).getDouble();

        activeProfile = ProfileType.fromConfig(
                configuration.getString("Активный профиль", "Профили", ProfileType.NORMAL.name(), "Текущий профиль хотбара."),
                ProfileType.NORMAL
        );
        refillMode = RefillMode.fromConfig(
                configuration.getString("Режим", "Пополнение", RefillMode.EMPTY_ONLY.name(), "Режим автоматического пополнения слотов."),
                RefillMode.EMPTY_ONLY
        );
        refillThreshold = configuration.getInt("Порог", "Пополнение", 16, 1, 64, "Когда количество предметов ниже этого значения, слот будет пополнен.");

        loadProfiles();
        if (configuration.hasChanged()) configuration.save();
    }

    private void loadProfiles() {
        for (int i = 0; i < 9; i++) {
            ItemCategory fallback = NORMAL_HOTBAR[i];
            String legacy = configuration.getString("Слот " + (i + 1), "Хотбар", fallback.name(), "Старая раскладка QuickSlot.");
            ItemCategory legacyRule = ItemCategory.fromConfig(legacy, fallback);
            String value = configuration.getString("Слот " + (i + 1), ProfileType.NORMAL.getConfigCategory(), legacyRule.name(), "Назначение слота.");
            profileRules[ProfileType.NORMAL.ordinal()][i] = ItemCategory.fromConfig(value, legacyRule);
        }

        loadProfile(ProfileType.RUSH, RUSH_HOTBAR);
        loadProfile(ProfileType.BRIDGE, BRIDGE_HOTBAR);
    }

    private void loadProfile(ProfileType profile, ItemCategory[] defaults) {
        for (int i = 0; i < 9; i++) {
            String value = configuration.getString("Слот " + (i + 1), profile.getConfigCategory(), defaults[i].name(), "Назначение слота.");
            profileRules[profile.ordinal()][i] = ItemCategory.fromConfig(value, defaults[i]);
        }
    }

    public void save() {
        configuration.get("Основное", "Включен", true).set(enabled);
        configuration.get("Основное", "Убирать ресурсы из хотбара", true).set(removeResourcesFromHotbar);
        configuration.get("Основное", "Защищать выбранный слот", true).set(protectSelectedSlot);
        configuration.get("HUD", "Показывать HUD ресурсов", true).set(resourceHudEnabled);
        configuration.get("HUD", "Показывать состояние QuickSlot", true).set(statusHudEnabled);
        configuration.get("HUD", "X", 8).set(hudX);
        configuration.get("HUD", "Y", 8).set(hudY);
        configuration.get("HUD", "Масштаб", 1.0D).set((double) hudScale);
        configuration.get("Профили", "Активный профиль", ProfileType.NORMAL.name()).set(activeProfile.name());
        configuration.get("Пополнение", "Режим", RefillMode.EMPTY_ONLY.name()).set(refillMode.name());
        configuration.get("Пополнение", "Порог", 16).set(refillThreshold);

        for (ProfileType profile : ProfileType.values()) {
            ItemCategory[] defaults = defaultsFor(profile);
            for (int i = 0; i < 9; i++) {
                configuration.get(profile.getConfigCategory(), "Слот " + (i + 1), defaults[i].name())
                        .set(profileRules[profile.ordinal()][i].name());
            }
        }

        for (int i = 0; i < 9; i++) {
            configuration.get("Хотбар", "Слот " + (i + 1), NORMAL_HOTBAR[i].name())
                    .set(profileRules[ProfileType.NORMAL.ordinal()][i].name());
        }
        configuration.save();
    }

    public boolean isEnabled() { return enabled; }
    public boolean isResourceHudEnabled() { return resourceHudEnabled; }
    public boolean isStatusHudEnabled() { return statusHudEnabled; }
    public boolean isRemoveResourcesFromHotbar() { return removeResourcesFromHotbar; }
    public boolean isProtectSelectedSlot() { return protectSelectedSlot; }
    public int getHudX() { return hudX; }
    public int getHudY() { return hudY; }
    public float getHudScale() { return hudScale; }
    public ProfileType getActiveProfile() { return activeProfile; }
    public RefillMode getRefillMode() { return refillMode; }
    public int getRefillThreshold() { return refillThreshold; }

    public ItemCategory getRule(int hotbarIndex) {
        return getRule(activeProfile, hotbarIndex);
    }

    public ItemCategory getRule(ProfileType profile, int hotbarIndex) {
        if (profile == null || hotbarIndex < 0 || hotbarIndex >= 9) return ItemCategory.IGNORE;
        return profileRules[profile.ordinal()][hotbarIndex];
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        save();
    }

    public void setResourceHudEnabled(boolean resourceHudEnabled) {
        this.resourceHudEnabled = resourceHudEnabled;
        save();
    }

    public void setStatusHudEnabled(boolean statusHudEnabled) {
        this.statusHudEnabled = statusHudEnabled;
        save();
    }

    public void setRemoveResourcesFromHotbar(boolean removeResourcesFromHotbar) {
        this.removeResourcesFromHotbar = removeResourcesFromHotbar;
        save();
    }

    public void setProtectSelectedSlot(boolean protectSelectedSlot) {
        this.protectSelectedSlot = protectSelectedSlot;
        save();
    }

    public void setActiveProfile(ProfileType profile) {
        if (profile == null) return;
        activeProfile = profile;
        save();
    }

    public void setRefillMode(RefillMode mode) {
        if (mode == null) return;
        refillMode = mode;
        save();
    }

    public void setRefillThreshold(int threshold) {
        refillThreshold = Math.max(1, Math.min(64, threshold));
        save();
    }

    public void setRule(int hotbarIndex, ItemCategory category) {
        if (hotbarIndex < 0 || hotbarIndex >= 9 || category == null) return;
        profileRules[activeProfile.ordinal()][hotbarIndex] = category;
        save();
    }

    public void resetHotbar() {
        ItemCategory[] defaults = defaultsFor(activeProfile);
        System.arraycopy(defaults, 0, profileRules[activeProfile.ordinal()], 0, 9);
        save();
    }

    public void setHudPosition(int x, int y) {
        hudX = Math.max(0, x);
        hudY = Math.max(0, y);
    }

    public void setHudScale(float scale) {
        hudScale = Math.max(0.5F, Math.min(3.0F, scale));
    }

    public void resetHud() {
        hudX = 8;
        hudY = 8;
        hudScale = 1.0F;
        save();
    }

    private ItemCategory[] defaultsFor(ProfileType profile) {
        switch (profile) {
            case RUSH: return RUSH_HOTBAR;
            case BRIDGE: return BRIDGE_HOTBAR;
            case NORMAL:
            default: return NORMAL_HOTBAR;
        }
    }
}
