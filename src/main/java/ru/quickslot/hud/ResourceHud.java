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
import ru.quickslot.inventory.ResourceCounter;

public final class ResourceHud {
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
                || minecraft.gameSettings.showDebugInfo) return;

        GlStateManager.pushMatrix();
        float scale = config.getHudScale();
        GlStateManager.scale(scale, scale, 1.0F);
        int x = config.getHudX();
        int y = config.getHudY();
        drawResource(Items.iron_ingot, ResourceCounter.iron(minecraft.thePlayer.inventory), x, y);
        drawResource(Items.gold_ingot, ResourceCounter.gold(minecraft.thePlayer.inventory), x, y + 18);
        drawResource(Items.diamond, ResourceCounter.diamond(minecraft.thePlayer.inventory), x, y + 36);
        drawResource(Items.emerald, ResourceCounter.emerald(minecraft.thePlayer.inventory), x, y + 54);
        GlStateManager.popMatrix();
    }

    private void drawResource(Item item, int count, int x, int y) {
        RenderItem renderItem = minecraft.getRenderItem();
        FontRenderer font = minecraft.fontRendererObj;
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemAndEffectIntoGUI(new ItemStack(item), x, y);
        RenderHelper.disableStandardItemLighting();
        font.drawStringWithShadow(Integer.toString(count), x + 19, y + 4, 0xFFFFFF);
    }
}
