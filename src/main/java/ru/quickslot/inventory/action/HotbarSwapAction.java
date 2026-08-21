package ru.quickslot.inventory.action;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.Container;

public final class HotbarSwapAction implements InventoryAction {
    private static final int HOTBAR_FIRST = 36;

    private final int windowId;
    private final int sourceSlot;
    private final int hotbarIndex;
    private final int targetSlot;
    private final StackSnapshot sourceBefore;
    private final StackSnapshot targetBefore;
    private final String key;

    public HotbarSwapAction(Container container, int sourceSlot, int hotbarIndex, String reason) {
        this.windowId = container.windowId;
        this.sourceSlot = sourceSlot;
        this.hotbarIndex = hotbarIndex;
        this.targetSlot = HOTBAR_FIRST + hotbarIndex;
        this.sourceBefore = StackSnapshot.capture(container.getSlot(sourceSlot).getStack());
        this.targetBefore = StackSnapshot.capture(container.getSlot(targetSlot).getStack());
        this.key = "swap:" + reason + ":" + windowId + ":" + sourceSlot + ":" + targetSlot + ":" + sourceBefore.fingerprint() + ":" + targetBefore.fingerprint();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public int getWindowId() {
        return windowId;
    }

    @Override
    public boolean isStillValid(Container container) {
        return container.windowId == windowId
                && sourceBefore.matchesExactly(container.getSlot(sourceSlot).getStack())
                && targetBefore.matchesExactly(container.getSlot(targetSlot).getStack());
    }

    @Override
    public void execute(Minecraft minecraft, EntityPlayerSP player, Container container) {
        minecraft.playerController.windowClick(windowId, sourceSlot, hotbarIndex, 2, player);
    }

    @Override
    public boolean isConfirmed(Container container) {
        if (container.windowId != windowId) return false;
        return sourceBefore.matchesExactly(container.getSlot(targetSlot).getStack())
                && targetBefore.matchesExactly(container.getSlot(sourceSlot).getStack());
    }
}
