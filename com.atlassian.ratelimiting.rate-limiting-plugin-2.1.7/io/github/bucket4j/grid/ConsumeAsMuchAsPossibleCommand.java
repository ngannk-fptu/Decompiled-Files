/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.GridCommand;

public class ConsumeAsMuchAsPossibleCommand
implements GridCommand<Long> {
    private static final long serialVersionUID = 1L;
    private long limit;
    private boolean bucketStateModified;

    public ConsumeAsMuchAsPossibleCommand(long limit) {
        this.limit = limit;
    }

    @Override
    public Long execute(GridBucketState state, long currentTimeNanos) {
        state.refillAllBandwidth(currentTimeNanos);
        long availableToConsume = state.getAvailableTokens();
        long toConsume = Math.min(this.limit, availableToConsume);
        if (toConsume <= 0L) {
            return 0L;
        }
        state.consume(toConsume);
        this.bucketStateModified = true;
        return toConsume;
    }

    @Override
    public boolean isBucketStateModified() {
        return this.bucketStateModified;
    }
}

