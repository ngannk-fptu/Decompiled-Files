/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.waiters;

import com.amazonaws.waiters.PollingStrategyContext;

public class PollingStrategy {
    private final RetryStrategy retryStrategy;
    private final DelayStrategy delayStrategy;

    public PollingStrategy(RetryStrategy retryStrategy, DelayStrategy delayStrategy) {
        this.retryStrategy = retryStrategy;
        this.delayStrategy = delayStrategy;
    }

    RetryStrategy getRetryStrategy() {
        return this.retryStrategy;
    }

    DelayStrategy getDelayStrategy() {
        return this.delayStrategy;
    }

    public static interface DelayStrategy {
        public void delayBeforeNextRetry(PollingStrategyContext var1) throws InterruptedException;
    }

    public static interface RetryStrategy {
        public boolean shouldRetry(PollingStrategyContext var1);
    }
}

