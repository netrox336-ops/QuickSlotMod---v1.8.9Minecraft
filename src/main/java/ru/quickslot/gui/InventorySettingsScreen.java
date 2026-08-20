package ru.quickslot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import ru.quickslot.config.QuickSlotConfig;

import java.io.IOException;

public final class InventorySettingsScreen extends GuiScreen {
    private final GuiScreen parent;
    private final QuickSlotConfig config;

    public InventorySettingsScreen(GuiScreen parent, QuickSlotConfig config) {
        this.parent = parent;
        this.config = config;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int x = width / 2 - 100;
        int y = height / 2 - 44;

        buttonList.add(new GuiButton(1, x, y, 200, 20, consolidationText()));
        buttonList.add(new GuiButton(2, x, y + 28, 200, 20, graceText()));
        buttonList.add(new GuiButton(3, x, y + 64, 200, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            config.setStackConsolidationEnabled(!config.isStackConsolidationEnabled());
            button.displayString = consolidationText();
        } else if (button.id == 2) {
            config.setManualGraceEnabled(!config.isManualGraceEnabled());
            button.displayString = graceText();
        } else if (button.id == 3) {
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
        drawCenteredString(fontRendererObj, "Инвентарь", width / 2, height / 2 - 78, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Дополнительное управление предметами", width / 2, height / 2 - 64, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private String consolidationText() {
        return "Объединять стаки: " + onOff(config.isStackConsolidationEnabled());
    }

    private String graceText() {
        return "Пауза после инвентаря: " + onOff(config.isManualGraceEnabled());
    }

    private String onOff(boolean value) {
        return value ? "ВКЛ" : "ВЫКЛ";
    }
}
