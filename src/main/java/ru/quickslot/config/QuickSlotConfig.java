package ru.quickslot.config;

import net.minecraftforge.common.config.Configuration;
import ru.quickslot.item.ItemCategory;

import java.io.File;

public final class QuickSlotConfig {
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

        ItemCategory[] defaults = {
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

        for (int i = 0; i < hotbarRules.length; i++) {
            String key = "Слот " + (i + 1);
            String value = configuration.getString(key, "Хотбар", defaults[i].name(), "Назначение слота: " + ItemCategory.availableValues());
            hotbarRules[i] = ItemCategory.fromConfig(value, defaults[i]);
        }

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public boolean isEnabled() { return enabled; }
    public boolean isResourceHudEnabled() { return resourceHudEnabled; }
    public boolean isRemoveResourcesFromHotbar() { return removeResourcesFromHotbar; }
    public int getHudX() { return hudX; }
    public int getHudY() { return hudY; }
    public float getHudScale() { return hudScale; }
    public ItemCategory getRule(int hotbarIndex) { return hotbarRules[hotbarIndex]; }
}
