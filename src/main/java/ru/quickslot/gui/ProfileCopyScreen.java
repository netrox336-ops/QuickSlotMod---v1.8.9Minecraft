package ru.quickslot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import ru.quickslot.config.ProfileType;
import ru.quickslot.config.QuickSlotConfig;

import java.io.IOException;

public final class ProfileCopyScreen extends GuiScreen {
    private final GuiScreen parent;
    private final QuickSlotConfig config;

    public ProfileCopyScreen(GuiScreen parent, QuickSlotConfig config) {
        this.parent = parent;
        this.config = config;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int x = width / 2 - 100;
        int y = height / 2 - 58;
        int row = 0;

        for (ProfileType profile : ProfileType.values()) {
            if (profile == config.getActiveProfile()) continue;
            buttonList.add(new GuiButton(100 + profile.ordinal(), x, y + row * 24, 200, 20, "Копировать в: " + profile.getDisplayName()));
            row++;
        }

        buttonList.add(new GuiButton(10, x, y + row * 24 + 10, 200, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 100 && button.id < 100 + ProfileType.values().length) {
            ProfileType target = ProfileType.values()[button.id - 100];
            if (target != config.getActiveProfile()) {
                config.copyActiveProfileTo(target);
                mc.displayGuiScreen(parent);
            }
            return;
        }
        if (button.id == 10) mc.displayGuiScreen(parent);
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
        drawCenteredString(fontRendererObj, "Копирование профиля", width / 2, height / 2 - 90, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Источник: " + config.getActiveProfile().getDisplayName(), width / 2, height / 2 - 76, 0xAAAAAA);
        drawCenteredString(fontRendererObj, "Профиль назначения будет перезаписан", width / 2, height / 2 - 64, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
