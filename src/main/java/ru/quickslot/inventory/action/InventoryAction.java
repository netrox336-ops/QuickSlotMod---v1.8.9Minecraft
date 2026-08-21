package ru.quickslot.inventory.action;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.Container;

public interface InventoryAction {
    String getKey();

    int getWindowId();

    boolean isStillValid(Container container);

    void execute(Minecraft minecraft, EntityPlayerSP player, Container container);

    boolean isConfirmed(Container container);
}
