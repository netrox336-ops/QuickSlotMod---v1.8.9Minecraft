package ru.quickslot.config;

import net.minecraftforge.common.config.Configuration;
import ru.quickslot.item.ItemCategory;

import java.io.File;

public final class QuickSlotConfig {
    private static final ItemCategory[] DEFAULT_HOTBAR = {
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

    private final Configuration configuration;
    private final ItemCategory[] hotbarRules = new ItemCategory[9];

    private boolean enabled;
    private boolean resourceHudEnabled;
    private boolean removeResourcesFromHotbar;
    private int hudX;
    private int hudY;
    private float hudScale;

    public QuickSlotConfig(File file) {
        this.configuration = new Configuration(file);
    }

    public void load() {
        enabled = configuration.getBoolean("Включен", "Основное", true, "Включает автоматическую работу QuickSlot.");
        removeResourcesFromHotbar = configuration.getBoolean("Убирать ресурсы из хотбара", "Основное", true, "Железо, золото, алмазы и изумруды будут переноситься в основной инвентарь.");

        resourceHudEnabled = configuration.getBoolean("Показывать HUD ресурсов", "HUD", true, "Показывает количество ресурсов во всём инвентаре.");
        hudX = configuration.getInt("X", "HUD", 8, 0, 10000, "Положение HUD по горизонтали.");
        hudY = configuration.getInt("Y", "HUD", 8, 0, 10000, "Положение HUD по вертикали.");
        hudScale = (float) configuration.get("HUD", "Масштаб", 1.0D, "Масштаб HUD.", 0.5D, 3.0D).getDouble();

        for (int i = 0; i < hotbarRules.length; i++) {
            String key = "Слот " + (i + 1);
            String value = configuration.getString(key, "Хотбар", DEFAULT_HOTBAR[i].name(), "Назначение слота: " + ItemCategory.availableValues());
            hotbarRules[i] = ItemCategory.fromConfig(value, DEFAULT_HOTBAR[i]);
        }

        if (configuration.hasChanged()) configuration.save();
    }

    public void save() {
        configuration.get("Основное", "Включен", true).set(enabled);
        configuration.get("Основное", "Убирать ресурсы из хотбара", true).set(removeResourcesFromHotbar);
        configuration.get("HUD", "Показывать HUD ресурсов", true).set(resourceHudEnabled);
        configuration.get("HUD", "X", 8).set(hudX);
        configuration.get("HUD", "Y", 8).set(hudY);
        configuration.get("HUD", "Масштаб", 1.0D).set((double) hudScale);

        for (int i = 0; i < hotbarRules.length; i++) {
            configuration.get("Хотбар", "Слот " + (i + 1), DEFAULT_HOTBAR[i].name()).set(hotbarRules[i].name());
        }
        configuration.save();
    }

    public boolean isEnabled() { return enabled; }
    public boolean isResourceHudEnabled() { return resourceHudEnabled; }
    public boolean isRemoveResourcesFromHotbar() { return removeResourcesFromHotbar; }
    public int getHudX() { return hudX; }
    public int getHudY() { return hudY; }
    public float getHudScale() { return hudScale; }
    public ItemCategory getRule(int hotbarIndex) { return hotbarRules[hotbarIndex]; }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        save();
    }

    public void setResourceHudEnabled(boolean resourceHudEnabled) {
        this.resourceHudEnabled = resourceHudEnabled;
        save();
    }

    public void setRemoveResourcesFromHotbar(boolean removeResourcesFromHotbar) {
        this.removeResourcesFromHotbar = removeResourcesFromHotbar;
        save();
    }

    public void setRule(int hotbarIndex, ItemCategory category) {
        if (hotbarIndex < 0 || hotbarIndex >= hotbarRules.length || category == null) return;
        hotbarRules[hotbarIndex] = category;
        save();
    }

    public void resetHotbar() {
        System.arraycopy(DEFAULT_HOTBAR, 0, hotbarRules, 0, hotbarRules.length);
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
}
