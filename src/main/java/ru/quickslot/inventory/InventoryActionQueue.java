package ru.quickslot.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.Container;
import ru.quickslot.inventory.action.InventoryAction;

import java.util.ArrayDeque;
import java.util.Deque;

public final class InventoryActionQueue {
    private static final int CONFIRM_STABLE_TICKS = 3;
    private static final int ACTION_TIMEOUT_TICKS = 30;
    private static final int SUCCESS_COOLDOWN_TICKS = 2;
    private static final int FAILURE_COOLDOWN_TICKS = 8;
    private static final int MAX_PENDING = 8;

    private final Deque<InventoryAction> pending = new ArrayDeque<InventoryAction>();
    private InventoryAction active;
    private int activeTicks;
    private int stableTicks;
    private int cooldownTicks;

    public boolean enqueue(InventoryAction action) {
        if (action == null) return false;
        if (active != null && active.getKey().equals(action.getKey())) return false;
        for (InventoryAction queued : pending) {
            if (queued.getKey().equals(action.getKey())) return false;
        }
        if (pending.size() >= MAX_PENDING) return false;
        pending.addLast(action);
        return true;
    }

    public void tick(Minecraft minecraft, EntityPlayerSP player, Container container) {
        if (minecraft == null || player == null || container == null) {
            clear();
            return;
        }

        if (active != null) {
            if (active.getWindowId() != container.windowId) {
                failCurrent();
                return;
            }

            activeTicks++;
            if (active.isConfirmed(container)) {
                stableTicks++;
                if (stableTicks >= CONFIRM_STABLE_TICKS) {
                    active = null;
                    activeTicks = 0;
                    stableTicks = 0;
                    cooldownTicks = SUCCESS_COOLDOWN_TICKS;
                }
            } else {
                stableTicks = 0;
            }

            if (active != null && activeTicks >= ACTION_TIMEOUT_TICKS) {
                failCurrent();
            }
            return;
        }

        if (cooldownTicks > 0) {
            cooldownTicks--;
            return;
        }

        while (!pending.isEmpty()) {
            InventoryAction next = pending.removeFirst();
            if (next.getWindowId() != container.windowId || !next.isStillValid(container)) continue;

            try {
                active = next;
                activeTicks = 0;
                stableTicks = 0;
                next.execute(minecraft, player, container);
            } catch (RuntimeException ignored) {
                failCurrent();
            }
            return;
        }
    }

    public boolean canPlan() {
        return active == null && pending.isEmpty() && cooldownTicks == 0;
    }

    public boolean isBusy() {
        return active != null || !pending.isEmpty() || cooldownTicks > 0;
    }

    public int size() {
        return pending.size() + (active == null ? 0 : 1);
    }

    public void clear() {
        pending.clear();
        active = null;
        activeTicks = 0;
        stableTicks = 0;
        cooldownTicks = 0;
    }

    private void failCurrent() {
        pending.clear();
        active = null;
        activeTicks = 0;
        stableTicks = 0;
        cooldownTicks = FAILURE_COOLDOWN_TICKS;
    }
}
