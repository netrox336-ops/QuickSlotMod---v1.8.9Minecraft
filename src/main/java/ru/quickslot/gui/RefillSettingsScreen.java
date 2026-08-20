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
        int y = Math.max(34, height / 2 - 92);

        buttonList.add(new GuiButton(1, x, y, 200, 20, modeText()));
        buttonList.add(new GuiButton(2, x, y + 28, 48, 20, "-8"));
        GuiButton value = new GuiButton(3, x + 52, y + 28, 96, 20, thresholdText());
        value.enabled = false;
        buttonList.add(value);
        buttonList.add(new GuiButton(4, x + 152, y + 28, 48, 20, "+8"));

        int gridY = y + 60;
        for (int i = 0; i < 9; i++) {
            int column = i % 3;
            int row = i / 3;
            buttonList.add(new GuiButton(10 + i, x + column * 68, gridY + row * 24, 64, 20, slotText(i)));
        }

        buttonList.add(new GuiButton(5, x, gridY + 80, 200, 20, "Назад"));
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
        } else if (button.id >= 10 && button.id <= 18) {
            int slot = button.id - 10;
            config.setRefillEnabled(slot, !config.isRefillEnabled(slot));
            button.displayString = slotText(slot);
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
        int y = Math.max(34, height / 2 - 92);
        drawCenteredString(fontRendererObj, "Автопополнение", width / 2, y - 28, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Профиль: " + config.getActiveProfile().getDisplayName(), width / 2, y - 14, 0xAAAAAA);
        drawCenteredString(fontRendererObj, "Ниже можно отключить refill отдельно для каждого слота", width / 2, y + 50, 0xAAAAAA);
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

    private String slotText(int slot) {
        return (slot + 1) + ": " + (config.isRefillEnabled(slot) ? "ВКЛ" : "ВЫКЛ");
    }
}
