package ru.quickslot.inventory;

public final class InventoryBackoffPolicy {
    static final int BASE_FAILURE_COOLDOWN_TICKS = 8;
    static final int MAX_FAILURE_COOLDOWN_TICKS = 64;
    static final int SUCCESSES_TO_RECOVER = 3;

    private int consecutiveFailures;
    private int successfulActions;

    public int onFailure() {
        consecutiveFailures = Math.min(consecutiveFailures + 1, 8);
        successfulActions = 0;
        return getFailureCooldownTicks();
    }

    public void onSuccess() {
        if (consecutiveFailures <= 0) return;
        successfulActions++;
        if (successfulActions >= SUCCESSES_TO_RECOVER) {
            consecutiveFailures--;
            successfulActions = 0;
        }
    }

    public int getFailureCooldownTicks() {
        if (consecutiveFailures <= 0) return 0;
        int shift = Math.min(consecutiveFailures - 1, 3);
        return Math.min(MAX_FAILURE_COOLDOWN_TICKS, BASE_FAILURE_COOLDOWN_TICKS << shift);
    }

    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public void reset() {
        consecutiveFailures = 0;
        successfulActions = 0;
    }
}
