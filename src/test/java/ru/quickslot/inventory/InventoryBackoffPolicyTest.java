package ru.quickslot.inventory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class InventoryBackoffPolicyTest {
    @Test
    public void failureCooldownGrowsAndStopsAtMaximum() {
        InventoryBackoffPolicy policy = new InventoryBackoffPolicy();

        assertEquals(8, policy.onFailure());
        assertEquals(16, policy.onFailure());
        assertEquals(32, policy.onFailure());
        assertEquals(64, policy.onFailure());
        assertEquals(64, policy.onFailure());
    }

    @Test
    public void threeSuccessfulActionsReduceFailureLevel() {
        InventoryBackoffPolicy policy = new InventoryBackoffPolicy();
        policy.onFailure();
        policy.onFailure();

        policy.onSuccess();
        policy.onSuccess();
        assertEquals(2, policy.getConsecutiveFailures());

        policy.onSuccess();
        assertEquals(1, policy.getConsecutiveFailures());
        assertEquals(8, policy.getFailureCooldownTicks());
    }

    @Test
    public void resetClearsFailureHistory() {
        InventoryBackoffPolicy policy = new InventoryBackoffPolicy();
        policy.onFailure();
        policy.onFailure();
        policy.reset();

        assertEquals(0, policy.getConsecutiveFailures());
        assertEquals(0, policy.getFailureCooldownTicks());
        assertEquals(8, policy.onFailure());
    }
}
