package ru.quickslot.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import ru.quickslot.config.QuickSlotConfig;

public final class QuickSlotKeyHandler {
    private final Minecraft minecraft = Minecraft.getMinecraft();
    private final QuickSlotConfig config;
    private final KeyBinding settingsKey;

    public QuickSlotKeyHandler(QuickSlotConfig config, KeyBinding settingsKey) {
        this.config = config;
        this.settingsKey = settingsKey;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (settingsKey.isPressed() && minecraft.thePlayer != null && minecraft.currentScreen == null) {
            minecraft.displayGuiScreen(new QuickSlotScreen(config));
        }
    }
}
