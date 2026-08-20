package ru.quickslot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import ru.quickslot.config.QuickSlotConfig;
import ru.quickslot.item.ItemCategory;

import java.io.IOException;

public final class HotbarEditorScreen extends GuiScreen {
    private final GuiScreen parent;
    private final QuickSlotConfig config;

    public HotbarEditorScreen(GuiScreen parent, QuickSlotConfig config) {
        this.parent = parent;
        this.config = config;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int buttonWidth = Math.min(140, Math.max(86, (width - 52) / 3));
        int gap = 6;
        int totalWidth = buttonWidth * 3 + gap * 2;
        int startX = (width - totalWidth) / 2;
        int startY = Math.max(62, height / 2 - 58);

        for (int i = 0; i < 9; i++) {
            int column = i % 3;
            int row = i / 3;
            buttonList.add(new GuiButton(100 + i, startX + column * (buttonWidth + gap), startY + row * 26,
                    buttonWidth, 20, slotText(i)));
        }

        int bottomY = Math.min(height - 28, startY + 88);
        buttonList.add(new GuiButton(10, width / 2 - 102, bottomY, 100, 20, "Сбросить"));
        buttonList.add(new GuiButton(11, width / 2 + 2, bottomY, 100, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 100 && button.id < 109) {
            int index = button.id - 100;
            ItemCategory next = config.getRule(index).next();
            config.setRule(index, next);
            button.displayString = slotText(index);
            return;
        }

        if (button.id == 10) {
            config.resetHotbar();
            initGui();
        } else if (button.id == 11) {
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
        drawCenteredString(fontRendererObj, "Раскладка хотбара", width / 2, 22, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Профиль: " + config.getActiveProfile().getDisplayName(), width / 2, 36, 0xAAAAAA);
        drawCenteredString(fontRendererObj, "Нажимай на слот, чтобы менять его назначение", width / 2, 50, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private String slotText(int index) {
        return (index + 1) + ": " + config.getRule(index).getDisplayName();
    }
}
