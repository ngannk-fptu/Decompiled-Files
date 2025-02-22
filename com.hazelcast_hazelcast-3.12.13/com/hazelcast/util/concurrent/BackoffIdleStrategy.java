/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.concurrent;

import com.hazelcast.util.Preconditions;
import com.hazelcast.util.concurrent.IdleStrategy;
import java.util.concurrent.locks.LockSupport;

public class BackoffIdleStrategy
implements IdleStrategy {
    private static final int ARG_COUNT = 5;
    private static final int ARG_MAX_SPINS = 1;
    private static final int ARG_MAX_YIELDS = 2;
    private static final int ARG_MIN_PARK_PERIOD = 3;
    private static final int ARG_MAX_PARK_PERIOD = 4;
    final long yieldThreshold;
    final long parkThreshold;
    final long minParkPeriodNs;
    final long maxParkPeriodNs;
    private final int maxShift;

    public BackoffIdleStrategy(long maxSpins, long maxYields, long minParkPeriodNs, long maxParkPeriodNs) {
        Preconditions.checkNotNegative(maxSpins, "maxSpins must be positive or zero");
        Preconditions.checkNotNegative(maxYields, "maxYields must be positive or zero");
        Preconditions.checkNotNegative(minParkPeriodNs, "minParkPeriodNs must be positive or zero");
        Preconditions.checkNotNegative(maxParkPeriodNs - minParkPeriodNs, "maxParkPeriodNs must be greater than or equal to minParkPeriodNs");
        this.yieldThreshold = maxSpins;
        this.parkThreshold = maxSpins + maxYields;
        this.minParkPeriodNs = minParkPeriodNs;
        this.maxParkPeriodNs = maxParkPeriodNs;
        this.maxShift = Long.numberOfLeadingZeros(minParkPeriodNs) - Long.numberOfLeadingZeros(maxParkPeriodNs);
    }

    @Override
    public boolean idle(long n) {
        if (n < this.yieldThreshold) {
            return false;
        }
        if (n < this.parkThreshold) {
            Thread.yield();
            return false;
        }
        long parkTime = this.parkTime(n);
        LockSupport.parkNanos(parkTime);
        return parkTime == this.maxParkPeriodNs;
    }

    long parkTime(long n) {
        long proposedShift = n - this.parkThreshold;
        long allowedShift = Math.min((long)this.maxShift, proposedShift);
        return proposedShift > (long)this.maxShift ? this.maxParkPeriodNs : (proposedShift < (long)this.maxShift ? this.minParkPeriodNs << (int)allowedShift : Math.min(this.minParkPeriodNs << (int)allowedShift, this.maxParkPeriodNs));
    }

    public static BackoffIdleStrategy createBackoffIdleStrategy(String config) {
        String[] args = config.split(",");
        if (args.length != 5) {
            throw new IllegalArgumentException(String.format("Invalid backoff configuration '%s', 4 arguments expected", config));
        }
        long maxSpins = Long.parseLong(args[1]);
        long maxYields = Long.parseLong(args[2]);
        long minParkPeriodNs = Long.parseLong(args[3]);
        long maxParkNanos = Long.parseLong(args[4]);
        return new BackoffIdleStrategy(maxSpins, maxYields, minParkPeriodNs, maxParkNanos);
    }
}

