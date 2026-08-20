package ru.quickslot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import ru.quickslot.config.QuickSlotConfig;
import ru.quickslot.config.RefillMode;

import java.io.IOException;

public final class RefillSettingsScreen extends GuiScreen {
    private final GuiScreen parent;
    private final QuickSlotConfig config;

    public RefillSettingsScreen(GuiScreen parent, QuickSlotConfig config) {
        this.parent = parent;
        this.config = config;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int x = width / 2 - 100;
        int y = height / 2 - 48;

        buttonList.add(new GuiButton(1, x, y, 200, 20, modeText()));
        buttonList.add(new GuiButton(2, x, y + 28, 48, 20, "-8"));
        GuiButton value = new GuiButton(3, x + 52, y + 28, 96, 20, thresholdText());
        value.enabled = false;
        buttonList.add(value);
        buttonList.add(new GuiButton(4, x + 152, y + 28, 48, 20, "+8"));
        buttonList.add(new GuiButton(5, x, y + 64, 200, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            config.setRefillMode(config.getRefillMode().next());
            initGui();
        } else if (button.id == 2) {
            config.setRefillThreshold(config.getRefillThreshold() - 8);
            initGui();
        } else if (button.id == 4) {
            config.setRefillThreshold(config.getRefillThreshold() + 8);
            initGui();
        } else if (button.id == 5) {
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
        drawCenteredString(fontRendererObj, "Автопополнение", width / 2, height / 2 - 82, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Порог используется только в режиме «Ниже порога»", width / 2, height / 2 - 68, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private String modeText() {
        RefillMode mode = config.getRefillMode();
        return "Режим: " + mode.getDisplayName();
    }

    private String thresholdText() {
        return "Порог: " + config.getRefillThreshold();
    }
}
