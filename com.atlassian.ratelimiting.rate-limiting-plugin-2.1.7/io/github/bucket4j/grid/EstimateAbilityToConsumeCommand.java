/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import io.github.bucket4j.EstimationProbe;
import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.GridCommand;

public class EstimateAbilityToConsumeCommand
implements GridCommand<EstimationProbe> {
    private static final long serialVersionUID = 1L;
    private long tokensToConsume;

    public EstimateAbilityToConsumeCommand(long tokensToEstimate) {
        this.tokensToConsume = tokensToEstimate;
    }

    @Override
    public EstimationProbe execute(GridBucketState state, long currentTimeNanos) {
        state.refillAllBandwidth(currentTimeNanos);
        long availableToConsume = state.getAvailableTokens();
        if (this.tokensToConsume <= availableToConsume) {
            return EstimationProbe.canBeConsumed(availableToConsume);
        }
        long nanosToWaitForRefill = state.calculateDelayNanosAfterWillBePossibleToConsume(this.tokensToConsume, currentTimeNanos);
        return EstimationProbe.canNotBeConsumed(availableToConsume, nanosToWaitForRefill);
    }

    @Override
    public boolean isBucketStateModified() {
        return false;
    }
}

