package ru.quickslot;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ru.quickslot.config.QuickSlotConfig;
import ru.quickslot.hud.ResourceHud;
import ru.quickslot.inventory.InventoryManager;

@Mod(
        modid = QuickSlot.MOD_ID,
        name = QuickSlot.MOD_NAME,
        version = QuickSlot.VERSION,
        acceptedMinecraftVersions = "[1.8.9]",
        clientSideOnly = true
)
public final class QuickSlot {
    public static final String MOD_ID = "quickslot";
    public static final String MOD_NAME = "QuickSlot";
    public static final String VERSION = "0.1.0";

    @Mod.Instance(MOD_ID)
    public static QuickSlot instance;

    private QuickSlotConfig config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new QuickSlotConfig(event.getSuggestedConfigurationFile());
        config.load();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new InventoryManager(config));
        MinecraftForge.EVENT_BUS.register(new ResourceHud(config));
    }
}
