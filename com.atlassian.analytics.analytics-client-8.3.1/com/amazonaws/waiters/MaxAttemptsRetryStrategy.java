/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.waiters;

import com.amazonaws.waiters.PollingStrategy;
import com.amazonaws.waiters.PollingStrategyContext;

public class MaxAttemptsRetryStrategy
implements PollingStrategy.RetryStrategy {
    private final int defaultMaxAttempts;

    public MaxAttemptsRetryStrategy(int defaultMaxAttempts) {
        this.defaultMaxAttempts = defaultMaxAttempts;
    }

    @Override
    public boolean shouldRetry(PollingStrategyContext pollingStrategyContext) {
        return pollingStrategyContext.getRetriesAttempted() < this.defaultMaxAttempts;
    }
}

