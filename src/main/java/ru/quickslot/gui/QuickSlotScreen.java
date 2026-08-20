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
        int y = Math.max(42, height / 2 - 92);

        buttonList.add(new GuiButton(1, x, y, 200, 20, organizerText()));
        buttonList.add(new GuiButton(2, x, y + 24, 200, 20, resourcesText()));
        buttonList.add(new GuiButton(9, x, y + 48, 200, 20, protectText()));
        buttonList.add(new GuiButton(3, x, y + 78, 98, 20, resourceHudText()));
        buttonList.add(new GuiButton(10, x + 102, y + 78, 98, 20, statusHudText()));
        buttonList.add(new GuiButton(4, x, y + 102, 98, 20, "Хотбар"));
        buttonList.add(new GuiButton(5, x + 102, y + 102, 98, 20, "HUD"));
        buttonList.add(new GuiButton(7, x, y + 126, 98, 20, "Профили"));
        buttonList.add(new GuiButton(8, x + 102, y + 126, 98, 20, "Пополнение"));
        buttonList.add(new GuiButton(6, x, y + 156, 200, 20, "Готово"));
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
                button.displayString = resourceHudText();
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
            case 7:
                mc.displayGuiScreen(new ProfileScreen(this, config));
                break;
            case 8:
                mc.displayGuiScreen(new RefillSettingsScreen(this, config));
                break;
            case 9:
                config.setProtectSelectedSlot(!config.isProtectSelectedSlot());
                button.displayString = protectText();
                break;
            case 10:
                config.setStatusHudEnabled(!config.isStatusHudEnabled());
                button.displayString = statusHudText();
                break;
            default:
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int y = Math.max(42, height / 2 - 92);
        drawCenteredString(fontRendererObj, "QuickSlot", width / 2, y - 30, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Профиль: " + config.getActiveProfile().getDisplayName(), width / 2, y - 16, 0xAAAAAA);
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

    private String protectText() {
        return "Защита выбранного слота: " + onOff(config.isProtectSelectedSlot());
    }

    private String resourceHudText() {
        return "Ресурсы: " + onOff(config.isResourceHudEnabled());
    }

    private String statusHudText() {
        return "Статус: " + onOff(config.isStatusHudEnabled());
    }

    private String onOff(boolean value) {
        return value ? "ВКЛ" : "ВЫКЛ";
    }
}
