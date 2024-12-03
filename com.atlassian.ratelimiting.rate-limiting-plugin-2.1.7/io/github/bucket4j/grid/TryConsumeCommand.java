/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.GridCommand;

public class TryConsumeCommand
implements GridCommand<Boolean> {
    private static final long serialVersionUID = 1L;
    private long tokensToConsume;
    private boolean bucketStateModified;

    public TryConsumeCommand(long tokensToConsume) {
        this.tokensToConsume = tokensToConsume;
    }

    @Override
    public Boolean execute(GridBucketState state, long currentTimeNanos) {
        state.refillAllBandwidth(currentTimeNanos);
        long availableToConsume = state.getAvailableTokens();
        if (this.tokensToConsume <= availableToConsume) {
            state.consume(this.tokensToConsume);
            this.bucketStateModified = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean isBucketStateModified() {
        return this.bucketStateModified;
    }
}

