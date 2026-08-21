package ru.quickslot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import ru.quickslot.config.QuickSlotConfig;
import ru.quickslot.item.BlockType;

import java.io.IOException;

public final class BlockPriorityScreen extends GuiScreen {
    private final GuiScreen parent;
    private final QuickSlotConfig config;

    public BlockPriorityScreen(GuiScreen parent, QuickSlotConfig config) {
        this.parent = parent;
        this.config = config;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int x = width / 2 - 100;
        int y = Math.max(30, height / 2 - 94);

        for (int i = 0; i < BlockType.values().length; i++) {
            int rowY = y + i * 22;
            GuiButton label = new GuiButton(100 + i, x, rowY, 112, 20, priorityText(i));
            label.enabled = false;
            buttonList.add(label);

            GuiButton up = new GuiButton(200 + i, x + 116, rowY, 40, 20, "↑");
            up.enabled = i > 0;
            buttonList.add(up);

            GuiButton down = new GuiButton(300 + i, x + 160, rowY, 40, 20, "↓");
            down.enabled = i < BlockType.values().length - 1;
            buttonList.add(down);
        }

        int bottomY = y + BlockType.values().length * 22 + 6;
        buttonList.add(new GuiButton(1, x, bottomY, 98, 20, "Сбросить"));
        buttonList.add(new GuiButton(2, x + 102, bottomY, 98, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 200 && button.id < 200 + BlockType.values().length) {
            config.moveBlockPriority(button.id - 200, -1);
            initGui();
        } else if (button.id >= 300 && button.id < 300 + BlockType.values().length) {
            config.moveBlockPriority(button.id - 300, 1);
            initGui();
        } else if (button.id == 1) {
            config.resetBlockPriority();
            initGui();
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
        int y = Math.max(30, height / 2 - 94);
        drawCenteredString(fontRendererObj, "Приоритет блоков", width / 2, y - 26, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Выше в списке — раньше используется", width / 2, y - 12, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private String priorityText(int index) {
        return (index + 1) + ". " + config.getBlockPriority(index).getDisplayName();
    }
}
