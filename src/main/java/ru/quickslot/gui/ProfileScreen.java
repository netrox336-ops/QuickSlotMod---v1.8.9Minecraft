package ru.quickslot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import ru.quickslot.config.ProfileType;
import ru.quickslot.config.QuickSlotConfig;

import java.io.IOException;

public final class ProfileScreen extends GuiScreen {
    private final GuiScreen parent;
    private final QuickSlotConfig config;

    public ProfileScreen(GuiScreen parent, QuickSlotConfig config) {
        this.parent = parent;
        this.config = config;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int x = width / 2 - 100;
        int y = height / 2 - 82;

        ProfileType[] profiles = ProfileType.values();
        for (int i = 0; i < profiles.length; i++) {
            buttonList.add(new GuiButton(100 + i, x, y + i * 24, 200, 20, profileText(profiles[i])));
        }

        buttonList.add(new GuiButton(20, x, y + 82, 200, 20, "Копировать активный профиль"));
        buttonList.add(new GuiButton(21, x, y + 106, 200, 20, "Сбросить активный профиль"));
        buttonList.add(new GuiButton(10, x, y + 138, 200, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 100 && button.id < 100 + ProfileType.values().length) {
            config.setActiveProfile(ProfileType.values()[button.id - 100]);
            initGui();
            return;
        }

        if (button.id == 20) {
            mc.displayGuiScreen(new ProfileCopyScreen(this, config));
        } else if (button.id == 21) {
            mc.displayGuiScreen(new ProfileResetScreen(this, config));
        } else if (button.id == 10) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parent);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Профили хотбара", width / 2, height / 2 - 110, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Выбери активный профиль или управляй его настройками", width / 2, height / 2 - 96, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private String profileText(ProfileType profile) {
        String prefix = config.getActiveProfile() == profile ? "> " : "";
        return prefix + profile.getDisplayName();
    }
}
