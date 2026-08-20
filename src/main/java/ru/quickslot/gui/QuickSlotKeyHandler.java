package ru.quickslot.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import ru.quickslot.config.ProfileType;
import ru.quickslot.config.QuickSlotConfig;

public final class QuickSlotKeyHandler {
    private final Minecraft minecraft = Minecraft.getMinecraft();
    private final QuickSlotConfig config;
    private final KeyBinding settingsKey;
    private final KeyBinding nextProfileKey;

    public QuickSlotKeyHandler(QuickSlotConfig config, KeyBinding settingsKey, KeyBinding nextProfileKey) {
        this.config = config;
        this.settingsKey = settingsKey;
        this.nextProfileKey = nextProfileKey;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (minecraft.thePlayer == null || minecraft.currentScreen != null) return;

        if (settingsKey.isPressed()) {
            minecraft.displayGuiScreen(new QuickSlotScreen(config));
            return;
        }

        if (nextProfileKey.isPressed()) {
            ProfileType[] profiles = ProfileType.values();
            ProfileType current = config.getActiveProfile();
            ProfileType next = profiles[(current.ordinal() + 1) % profiles.length];
            config.setActiveProfile(next);
            minecraft.thePlayer.addChatMessage(new ChatComponentText(
                    "§a[QuickSlot] §fПрофиль: §e" + next.getDisplayName()
            ));
        }
    }
}
