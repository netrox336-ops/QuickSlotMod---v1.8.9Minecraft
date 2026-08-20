package ru.quickslot.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.quickslot.config.QuickSlotConfig;
import ru.quickslot.gui.HudEditorScreen;
import ru.quickslot.inventory.ResourceCounter;

public final class ResourceHud {
    public static final int BASE_WIDTH = 58;
    public static final int BASE_HEIGHT = 70;

    private final Minecraft minecraft = Minecraft.getMinecraft();
    private final QuickSlotConfig config;

    public ResourceHud(QuickSlotConfig config) {
        this.config = config;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL
                || !config.isResourceHudEnabled()
                || minecraft.thePlayer == null
                || minecraft.gameSettings.showDebugInfo
                || minecraft.currentScreen instanceof HudEditorScreen) return;

        render(minecraft, config.getHudX(), config.getHudY(), config.getHudScale());
    }

    public static void render(Minecraft minecraft, int x, int y, float scale) {
        if (minecraft.thePlayer == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0F);
        GlStateManager.scale(scale, scale, 1.0F);
        drawResource(minecraft, Items.iron_ingot, ResourceCounter.iron(minecraft.thePlayer.inventory), 0, 0);
        drawResource(minecraft, Items.gold_ingot, ResourceCounter.gold(minecraft.thePlayer.inventory), 0, 18);
        drawResource(minecraft, Items.diamond, ResourceCounter.diamond(minecraft.thePlayer.inventory), 0, 36);
        drawResource(minecraft, Items.emerald, ResourceCounter.emerald(minecraft.thePlayer.inventory), 0, 54);
        GlStateManager.popMatrix();
    }

    private static void drawResource(Minecraft minecraft, Item item, int count, int x, int y) {
        RenderItem renderItem = minecraft.getRenderItem();
        FontRenderer font = minecraft.fontRendererObj;
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemAndEffectIntoGUI(new ItemStack(item), x, y);
        RenderHelper.disableStandardItemLighting();
        font.drawStringWithShadow(Integer.toString(count), x + 19, y + 4, 0xFFFFFF);
    }
}
