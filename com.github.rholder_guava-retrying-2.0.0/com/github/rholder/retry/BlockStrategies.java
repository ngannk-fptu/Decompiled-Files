/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package com.github.rholder.retry;

import com.github.rholder.retry.BlockStrategy;
import javax.annotation.concurrent.Immutable;

public final class BlockStrategies {
    private static final BlockStrategy THREAD_SLEEP_STRATEGY = new ThreadSleepStrategy();

    private BlockStrategies() {
    }

    public static BlockStrategy threadSleepStrategy() {
        return THREAD_SLEEP_STRATEGY;
    }

    @Immutable
    private static class ThreadSleepStrategy
    implements BlockStrategy {
        private ThreadSleepStrategy() {
        }

        @Override
        public void block(long sleepTime) throws InterruptedException {
            Thread.sleep(sleepTime);
        }
    }
}

