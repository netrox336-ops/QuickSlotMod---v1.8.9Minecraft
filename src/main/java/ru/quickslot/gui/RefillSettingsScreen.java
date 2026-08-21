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
    private int selectedSlot;

    public RefillSettingsScreen(GuiScreen parent, QuickSlotConfig config) {
        this.parent = parent;
        this.config = config;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int x = width / 2 - 100;
        int y = Math.max(34, height / 2 - 108);

        for (int i = 0; i < 9; i++) {
            int column = i % 3;
            int row = i / 3;
            buttonList.add(new GuiButton(10 + i, x + column * 68, y + row * 24, 64, 20, slotText(i)));
        }

        int controlsY = y + 80;
        buttonList.add(new GuiButton(1, x, controlsY, 200, 20, enabledText()));
        buttonList.add(new GuiButton(2, x, controlsY + 24, 200, 20, modeText()));
        buttonList.add(new GuiButton(3, x, controlsY + 48, 48, 20, "-8"));
        GuiButton value = new GuiButton(4, x + 52, controlsY + 48, 96, 20, thresholdText());
        value.enabled = false;
        buttonList.add(value);
        buttonList.add(new GuiButton(5, x + 152, controlsY + 48, 48, 20, "+8"));
        buttonList.add(new GuiButton(6, x, controlsY + 80, 200, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 10 && button.id <= 18) {
            selectedSlot = button.id - 10;
            initGui();
            return;
        }

        switch (button.id) {
            case 1:
                config.setRefillEnabled(selectedSlot, !config.isRefillEnabled(selectedSlot));
                initGui();
                break;
            case 2:
                config.setRefillMode(selectedSlot, config.getRefillMode(selectedSlot).next());
                initGui();
                break;
            case 3:
                config.setRefillThreshold(selectedSlot, config.getRefillThreshold(selectedSlot) - 8);
                initGui();
                break;
            case 5:
                config.setRefillThreshold(selectedSlot, config.getRefillThreshold(selectedSlot) + 8);
                initGui();
                break;
            case 6:
                mc.displayGuiScreen(parent);
                break;
            default:
                break;
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
        int y = Math.max(34, height / 2 - 108);
        drawCenteredString(fontRendererObj, "Автопополнение", width / 2, y - 28, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Профиль: " + config.getActiveProfile().getDisplayName(), width / 2, y - 14, 0xAAAAAA);
        drawCenteredString(
                fontRendererObj,
                "Слот " + (selectedSlot + 1) + ": " + config.getRule(selectedSlot).getDisplayName(),
                width / 2,
                y + 70,
                0xAAAAAA
        );
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private String enabledText() {
        return "Auto Refill: " + (config.isRefillEnabled(selectedSlot) ? "ВКЛ" : "ВЫКЛ");
    }

    private String modeText() {
        RefillMode mode = config.getRefillMode(selectedSlot);
        return "Режим: " + mode.getDisplayName();
    }

    private String thresholdText() {
        return "Порог: " + config.getRefillThreshold(selectedSlot);
    }

    private String slotText(int slot) {
        String marker = slot == selectedSlot ? "[" + (slot + 1) + "]" : Integer.toString(slot + 1);
        return marker + " " + (config.isRefillEnabled(slot) ? "ВКЛ" : "ВЫКЛ");
    }
}
