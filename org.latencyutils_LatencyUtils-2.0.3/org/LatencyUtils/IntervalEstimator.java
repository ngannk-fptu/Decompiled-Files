/*
 * Decompiled with CFR 0.152.
 */
package org.LatencyUtils;

public abstract class IntervalEstimator {
    public abstract void recordInterval(long var1);

    public abstract long getEstimatedInterval(long var1);
}

