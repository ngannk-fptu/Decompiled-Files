/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import java.io.Serializable;

public class ConsumptionProbe
implements Serializable {
    private static final long serialVersionUID = 42L;
    private final boolean consumed;
    private final long remainingTokens;
    private final long nanosToWaitForRefill;

    public static ConsumptionProbe consumed(long remainingTokens) {
        return new ConsumptionProbe(true, remainingTokens, 0L);
    }

    public static ConsumptionProbe rejected(long remainingTokens, long nanosToWaitForRefill) {
        return new ConsumptionProbe(false, remainingTokens, nanosToWaitForRefill);
    }

    private ConsumptionProbe(boolean consumed, long remainingTokens, long nanosToWaitForRefill) {
        this.consumed = consumed;
        this.remainingTokens = Math.max(0L, remainingTokens);
        this.nanosToWaitForRefill = nanosToWaitForRefill;
    }

    public boolean isConsumed() {
        return this.consumed;
    }

    public long getRemainingTokens() {
        return this.remainingTokens;
    }

    public long getNanosToWaitForRefill() {
        return this.nanosToWaitForRefill;
    }

    public String toString() {
        return "ConsumptionResult{consumed=" + this.consumed + ", remainingTokens=" + this.remainingTokens + ", nanosToWaitForRefill=" + this.nanosToWaitForRefill + '}';
    }
}

