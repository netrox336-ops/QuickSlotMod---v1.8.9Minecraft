package ru.quickslot.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import ru.quickslot.config.QuickSlotConfig;
import ru.quickslot.hud.ResourceHud;

import java.io.IOException;

public final class HudEditorScreen extends GuiScreen {
    private final GuiScreen parent;
    private final QuickSlotConfig config;
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    public HudEditorScreen(GuiScreen parent, QuickSlotConfig config) {
        this.parent = parent;
        this.config = config;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int y = height - 28;
        buttonList.add(new GuiButton(1, width / 2 - 154, y, 48, 20, "-"));
        buttonList.add(new GuiButton(2, width / 2 - 102, y, 48, 20, "+"));
        buttonList.add(new GuiButton(3, width / 2 - 50, y, 100, 20, "Сбросить"));
        buttonList.add(new GuiButton(4, width / 2 + 54, y, 100, 20, "Назад"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            config.setHudScale(config.getHudScale() - 0.1F);
            config.save();
        } else if (button.id == 2) {
            config.setHudScale(config.getHudScale() + 0.1F);
            config.save();
        } else if (button.id == 3) {
            config.resetHud();
        } else if (button.id == 4) {
            config.save();
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Настройка HUD", width / 2, 18, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Перетащи счётчик мышкой. Кнопки снизу меняют масштаб.", width / 2, 32, 0xAAAAAA);

        int previewWidth = Math.max(1, (int) (ResourceHud.BASE_WIDTH * config.getHudScale()));
        int previewHeight = Math.max(1, (int) (ResourceHud.BASE_HEIGHT * config.getHudScale()));
        int x = config.getHudX();
        int y = config.getHudY();
        drawRect(x - 3, y - 3, x + previewWidth + 3, y + previewHeight + 3, 0x45000000);
        ResourceHud.render(mc, x, y, config.getHudScale());

        drawCenteredString(fontRendererObj, String.format("Масштаб: %.1fx", config.getHudScale()), width / 2, height - 42, 0xDDDDDD);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton != 0) return;

        int previewWidth = (int) (ResourceHud.BASE_WIDTH * config.getHudScale());
        int previewHeight = (int) (ResourceHud.BASE_HEIGHT * config.getHudScale());
        int x = config.getHudX();
        int y = config.getHudY();
        if (mouseX >= x && mouseX <= x + previewWidth && mouseY >= y && mouseY <= y + previewHeight) {
            dragging = true;
            dragOffsetX = mouseX - x;
            dragOffsetY = mouseY - y;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (!dragging || clickedMouseButton != 0) return;
        int previewWidth = (int) (ResourceHud.BASE_WIDTH * config.getHudScale());
        int previewHeight = (int) (ResourceHud.BASE_HEIGHT * config.getHudScale());
        int maxX = Math.max(0, width - previewWidth);
        int maxY = Math.max(0, height - previewHeight);
        int x = Math.max(0, Math.min(maxX, mouseX - dragOffsetX));
        int y = Math.max(0, Math.min(maxY, mouseY - dragOffsetY));
        config.setHudPosition(x, y);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (dragging) {
            dragging = false;
            config.save();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            config.save();
            mc.displayGuiScreen(parent);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        config.save();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
