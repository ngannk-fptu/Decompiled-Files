/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.Immutable
 */
package com.github.rholder.retry;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.StopStrategy;
import com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

public final class StopStrategies {
    private static final StopStrategy NEVER_STOP = new NeverStopStrategy();

    private StopStrategies() {
    }

    public static StopStrategy neverStop() {
        return NEVER_STOP;
    }

    public static StopStrategy stopAfterAttempt(int attemptNumber) {
        return new StopAfterAttemptStrategy(attemptNumber);
    }

    @Deprecated
    public static StopStrategy stopAfterDelay(long delayInMillis) {
        return StopStrategies.stopAfterDelay(delayInMillis, TimeUnit.MILLISECONDS);
    }

    public static StopStrategy stopAfterDelay(long duration, @Nonnull TimeUnit timeUnit) {
        Preconditions.checkNotNull((Object)((Object)timeUnit), (Object)"The time unit may not be null");
        return new StopAfterDelayStrategy(timeUnit.toMillis(duration));
    }

    @Immutable
    private static final class StopAfterDelayStrategy
    implements StopStrategy {
        private final long maxDelay;

        public StopAfterDelayStrategy(long maxDelay) {
            Preconditions.checkArgument((maxDelay >= 0L ? 1 : 0) != 0, (String)"maxDelay must be >= 0 but is %d", (Object[])new Object[]{maxDelay});
            this.maxDelay = maxDelay;
        }

        @Override
        public boolean shouldStop(Attempt failedAttempt) {
            return failedAttempt.getDelaySinceFirstAttempt() >= this.maxDelay;
        }
    }

    @Immutable
    private static final class StopAfterAttemptStrategy
    implements StopStrategy {
        private final int maxAttemptNumber;

        public StopAfterAttemptStrategy(int maxAttemptNumber) {
            Preconditions.checkArgument((maxAttemptNumber >= 1 ? 1 : 0) != 0, (String)"maxAttemptNumber must be >= 1 but is %d", (Object[])new Object[]{maxAttemptNumber});
            this.maxAttemptNumber = maxAttemptNumber;
        }

        @Override
        public boolean shouldStop(Attempt failedAttempt) {
            return failedAttempt.getAttemptNumber() >= (long)this.maxAttemptNumber;
        }
    }

    @Immutable
    private static final class NeverStopStrategy
    implements StopStrategy {
        private NeverStopStrategy() {
        }

        @Override
        public boolean shouldStop(Attempt failedAttempt) {
            return false;
        }
    }
}

