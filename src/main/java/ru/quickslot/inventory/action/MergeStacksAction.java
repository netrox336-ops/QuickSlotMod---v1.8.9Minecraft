package ru.quickslot.inventory.action;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public final class MergeStacksAction implements InventoryAction {
    private final int windowId;
    private final int sourceSlot;
    private final int targetSlot;
    private final StackSnapshot sourceBefore;
    private final StackSnapshot targetBefore;
    private final StackSnapshot sourceExpected;
    private final StackSnapshot targetExpected;
    private final String key;

    public MergeStacksAction(Container container, int sourceSlot, int targetSlot, String reason) {
        this.windowId = container.windowId;
        this.sourceSlot = sourceSlot;
        this.targetSlot = targetSlot;

        ItemStack source = container.getSlot(sourceSlot).getStack();
        ItemStack target = container.getSlot(targetSlot).getStack();
        this.sourceBefore = StackSnapshot.capture(source);
        this.targetBefore = StackSnapshot.capture(target);

        ItemStack expectedSource = source == null ? null : source.copy();
        ItemStack expectedTarget = target == null ? null : target.copy();
        if (expectedSource != null && expectedTarget != null && StackSnapshot.sameKind(expectedSource, expectedTarget)) {
            int free = Math.max(0, expectedTarget.getMaxStackSize() - expectedTarget.stackSize);
            int moved = Math.min(free, expectedSource.stackSize);
            expectedTarget.stackSize += moved;
            expectedSource.stackSize -= moved;
            if (expectedSource.stackSize <= 0) expectedSource = null;
        }

        this.sourceExpected = StackSnapshot.capture(expectedSource);
        this.targetExpected = StackSnapshot.capture(expectedTarget);
        this.key = "merge:" + reason + ":" + windowId + ":" + sourceSlot + ":" + targetSlot + ":" + sourceBefore.fingerprint() + ":" + targetBefore.fingerprint();
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
        minecraft.playerController.windowClick(windowId, sourceSlot, 0, 0, player);
        minecraft.playerController.windowClick(windowId, targetSlot, 0, 0, player);
        minecraft.playerController.windowClick(windowId, sourceSlot, 0, 0, player);
    }

    @Override
    public boolean isConfirmed(Container container) {
        if (container.windowId != windowId) return false;
        return sourceExpected.matchesExactly(container.getSlot(sourceSlot).getStack())
                && targetExpected.matchesExactly(container.getSlot(targetSlot).getStack());
    }
}
