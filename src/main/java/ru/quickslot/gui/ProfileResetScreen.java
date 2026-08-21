package ru.quickslot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import ru.quickslot.config.QuickSlotConfig;

import java.io.IOException;

public final class ProfileResetScreen extends GuiScreen {
    private final GuiScreen parent;
    private final QuickSlotConfig config;

    public ProfileResetScreen(GuiScreen parent, QuickSlotConfig config) {
        this.parent = parent;
        this.config = config;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int x = width / 2 - 100;
        int y = height / 2 + 10;
        buttonList.add(new GuiButton(1, x, y, 98, 20, "Сбросить"));
        buttonList.add(new GuiButton(2, x + 102, y, 98, 20, "Отмена"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            config.resetActiveProfile();
            mc.displayGuiScreen(parent);
        } else if (button.id == 2) {
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
        drawCenteredString(fontRendererObj, "Сбросить профиль «" + config.getActiveProfile().getDisplayName() + "»?", width / 2, height / 2 - 32, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Будут сброшены хотбар, refill и приоритеты блоков", width / 2, height / 2 - 16, 0xAAAAAA);
        drawCenteredString(fontRendererObj, "Глобальные настройки останутся без изменений", width / 2, height / 2 - 4, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
