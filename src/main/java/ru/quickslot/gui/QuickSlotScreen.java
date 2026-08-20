package ru.quickslot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import ru.quickslot.config.QuickSlotConfig;

import java.io.IOException;

public final class QuickSlotScreen extends GuiScreen {
    private final QuickSlotConfig config;

    public QuickSlotScreen(QuickSlotConfig config) {
        this.config = config;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int x = width / 2 - 100;
        int y = height / 2 - 70;

        buttonList.add(new GuiButton(1, x, y, 200, 20, organizerText()));
        buttonList.add(new GuiButton(2, x, y + 24, 200, 20, resourcesText()));
        buttonList.add(new GuiButton(3, x, y + 48, 200, 20, hudText()));
        buttonList.add(new GuiButton(4, x, y + 78, 98, 20, "Хотбар"));
        buttonList.add(new GuiButton(5, x + 102, y + 78, 98, 20, "HUD"));
        buttonList.add(new GuiButton(6, x, y + 108, 200, 20, "Готово"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                config.setEnabled(!config.isEnabled());
                button.displayString = organizerText();
                break;
            case 2:
                config.setRemoveResourcesFromHotbar(!config.isRemoveResourcesFromHotbar());
                button.displayString = resourcesText();
                break;
            case 3:
                config.setResourceHudEnabled(!config.isResourceHudEnabled());
                button.displayString = hudText();
                break;
            case 4:
                mc.displayGuiScreen(new HotbarEditorScreen(this, config));
                break;
            case 5:
                mc.displayGuiScreen(new HudEditorScreen(this, config));
                break;
            case 6:
                mc.displayGuiScreen(null);
                break;
            default:
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "QuickSlot", width / 2, height / 2 - 104, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Настройки мода", width / 2, height / 2 - 90, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private String organizerText() {
        return "Автосортировка: " + onOff(config.isEnabled());
    }

    private String resourcesText() {
        return "Убирать ресурсы: " + onOff(config.isRemoveResourcesFromHotbar());
    }

    private String hudText() {
        return "HUD ресурсов: " + onOff(config.isResourceHudEnabled());
    }

    private String onOff(boolean value) {
        return value ? "ВКЛ" : "ВЫКЛ";
    }
}
