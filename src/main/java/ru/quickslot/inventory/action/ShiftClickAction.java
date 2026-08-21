package ru.quickslot.inventory.action;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.Container;

public final class ShiftClickAction implements InventoryAction {
    private final int windowId;
    private final int sourceSlot;
    private final StackSnapshot sourceBefore;
    private final String key;

    public ShiftClickAction(Container container, int sourceSlot, String reason) {
        this.windowId = container.windowId;
        this.sourceSlot = sourceSlot;
        this.sourceBefore = StackSnapshot.capture(container.getSlot(sourceSlot).getStack());
        this.key = "shift:" + reason + ":" + windowId + ":" + sourceSlot + ":" + sourceBefore.fingerprint();
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
                && sourceBefore.matchesExactly(container.getSlot(sourceSlot).getStack());
    }

    @Override
    public void execute(Minecraft minecraft, EntityPlayerSP player, Container container) {
        minecraft.playerController.windowClick(windowId, sourceSlot, 0, 1, player);
    }

    @Override
    public boolean isConfirmed(Container container) {
        return container.windowId == windowId
                && !sourceBefore.matchesExactly(container.getSlot(sourceSlot).getStack());
    }
}
