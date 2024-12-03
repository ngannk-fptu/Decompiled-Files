/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import java.io.Serializable;

public class EstimationProbe
implements Serializable {
    private static final long serialVersionUID = 42L;
    private final boolean canBeConsumed;
    private final long remainingTokens;
    private final long nanosToWaitForRefill;

    public static EstimationProbe canBeConsumed(long remainingTokens) {
        return new EstimationProbe(true, remainingTokens, 0L);
    }

    public static EstimationProbe canNotBeConsumed(long remainingTokens, long nanosToWaitForRefill) {
        return new EstimationProbe(false, remainingTokens, nanosToWaitForRefill);
    }

    private EstimationProbe(boolean canBeConsumed, long remainingTokens, long nanosToWaitForRefill) {
        this.canBeConsumed = canBeConsumed;
        this.remainingTokens = Math.max(0L, remainingTokens);
        this.nanosToWaitForRefill = nanosToWaitForRefill;
    }

    public boolean canBeConsumed() {
        return this.canBeConsumed;
    }

    public long getRemainingTokens() {
        return this.remainingTokens;
    }

    public long getNanosToWaitForRefill() {
        return this.nanosToWaitForRefill;
    }

    public String toString() {
        return "ConsumptionResult{isConsumed=" + this.canBeConsumed + ", remainingTokens=" + this.remainingTokens + ", nanosToWaitForRefill=" + this.nanosToWaitForRefill + '}';
    }
}

