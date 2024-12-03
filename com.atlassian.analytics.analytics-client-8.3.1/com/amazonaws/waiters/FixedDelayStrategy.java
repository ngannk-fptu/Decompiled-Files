/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.waiters;

import com.amazonaws.waiters.PollingStrategy;
import com.amazonaws.waiters.PollingStrategyContext;

public class FixedDelayStrategy
implements PollingStrategy.DelayStrategy {
    private final int defaultDelayInSeconds;

    public FixedDelayStrategy(int defaultDelayInSeconds) {
        this.defaultDelayInSeconds = defaultDelayInSeconds;
    }

    @Override
    public void delayBeforeNextRetry(PollingStrategyContext pollingStrategyContext) throws InterruptedException {
        Thread.sleep(this.defaultDelayInSeconds * 1000);
    }
}

