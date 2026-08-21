package ru.quickslot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import ru.quickslot.config.QuickSlotConfig;

import java.io.IOException;

public final class SmartSelectionScreen extends GuiScreen {
    private final GuiScreen parent;
    private final QuickSlotConfig config;

    public SmartSelectionScreen(GuiScreen parent, QuickSlotConfig config) {
        this.parent = parent;
        this.config = config;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int x = width / 2 - 100;
        int y = height / 2 - 72;

        buttonList.add(new GuiButton(1, x, y, 200, 20, sameBlockText()));
        buttonList.add(new GuiButton(2, x, y + 28, 200, 20, "Приоритет блоков"));
        buttonList.add(new GuiButton(3, x, y + 62, 200, 20, swordText()));
        buttonList.add(new GuiButton(4, x, y + 86, 200, 20, pickaxeText()));
        buttonList.add(new GuiButton(5, x, y + 110, 200, 20, axeText()));
        buttonList.add(new GuiButton(6, x, y + 144, 200, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                config.setPreferSameBlock(!config.isPreferSameBlock());
                button.displayString = sameBlockText();
                break;
            case 2:
                mc.displayGuiScreen(new BlockPriorityScreen(this, config));
                break;
            case 3:
                config.setAutoUpgradeSword(!config.isAutoUpgradeSword());
                button.displayString = swordText();
                break;
            case 4:
                config.setAutoUpgradePickaxe(!config.isAutoUpgradePickaxe());
                button.displayString = pickaxeText();
                break;
            case 5:
                config.setAutoUpgradeAxe(!config.isAutoUpgradeAxe());
                button.displayString = axeText();
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
        drawCenteredString(fontRendererObj, "Умный выбор", width / 2, height / 2 - 106, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Профиль: " + config.getActiveProfile().getDisplayName(), width / 2, height / 2 - 92, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private String sameBlockText() {
        return "Сохранять текущий блок: " + onOff(config.isPreferSameBlock());
    }

    private String swordText() {
        return "Улучшать меч: " + onOff(config.isAutoUpgradeSword());
    }

    private String pickaxeText() {
        return "Улучшать кирку: " + onOff(config.isAutoUpgradePickaxe());
    }

    private String axeText() {
        return "Улучшать топор: " + onOff(config.isAutoUpgradeAxe());
    }

    private String onOff(boolean value) {
        return value ? "ВКЛ" : "ВЫКЛ";
    }
}
