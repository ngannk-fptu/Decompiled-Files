/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.GridCommand;

public class ReserveAndCalculateTimeToSleepCommand
implements GridCommand<Long> {
    private static final long serialVersionUID = 1L;
    private long tokensToConsume;
    private long waitIfBusyNanosLimit;
    private boolean bucketStateModified;

    public ReserveAndCalculateTimeToSleepCommand(long tokensToConsume, long waitIfBusyNanosLimit) {
        this.tokensToConsume = tokensToConsume;
        this.waitIfBusyNanosLimit = waitIfBusyNanosLimit;
    }

    @Override
    public Long execute(GridBucketState state, long currentTimeNanos) {
        state.refillAllBandwidth(currentTimeNanos);
        long nanosToCloseDeficit = state.calculateDelayNanosAfterWillBePossibleToConsume(this.tokensToConsume, currentTimeNanos);
        if (nanosToCloseDeficit == Long.MAX_VALUE || nanosToCloseDeficit > this.waitIfBusyNanosLimit) {
            return Long.MAX_VALUE;
        }
        state.consume(this.tokensToConsume);
        this.bucketStateModified = true;
        return nanosToCloseDeficit;
    }

    @Override
    public boolean isBucketStateModified() {
        return this.bucketStateModified;
    }
}

