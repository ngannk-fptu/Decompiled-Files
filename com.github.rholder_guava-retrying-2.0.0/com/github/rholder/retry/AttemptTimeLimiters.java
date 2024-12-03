/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.util.concurrent.SimpleTimeLimiter
 *  com.google.common.util.concurrent.TimeLimiter
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.Immutable
 */
package com.github.rholder.retry;

import com.github.rholder.retry.AttemptTimeLimiter;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

public class AttemptTimeLimiters {
    private AttemptTimeLimiters() {
    }

    public static <V> AttemptTimeLimiter<V> noTimeLimit() {
        return new NoAttemptTimeLimit();
    }

    public static <V> AttemptTimeLimiter<V> fixedTimeLimit(long duration, @Nonnull TimeUnit timeUnit) {
        Preconditions.checkNotNull((Object)((Object)timeUnit));
        return new FixedAttemptTimeLimit(duration, timeUnit);
    }

    public static <V> AttemptTimeLimiter<V> fixedTimeLimit(long duration, @Nonnull TimeUnit timeUnit, @Nonnull ExecutorService executorService) {
        Preconditions.checkNotNull((Object)((Object)timeUnit));
        return new FixedAttemptTimeLimit(duration, timeUnit, executorService);
    }

    @Immutable
    private static final class FixedAttemptTimeLimit<V>
    implements AttemptTimeLimiter<V> {
        private final TimeLimiter timeLimiter;
        private final long duration;
        private final TimeUnit timeUnit;

        public FixedAttemptTimeLimit(long duration, @Nonnull TimeUnit timeUnit) {
            this((TimeLimiter)new SimpleTimeLimiter(), duration, timeUnit);
        }

        public FixedAttemptTimeLimit(long duration, @Nonnull TimeUnit timeUnit, @Nonnull ExecutorService executorService) {
            this((TimeLimiter)new SimpleTimeLimiter(executorService), duration, timeUnit);
        }

        private FixedAttemptTimeLimit(@Nonnull TimeLimiter timeLimiter, long duration, @Nonnull TimeUnit timeUnit) {
            Preconditions.checkNotNull((Object)timeLimiter);
            Preconditions.checkNotNull((Object)((Object)timeUnit));
            this.timeLimiter = timeLimiter;
            this.duration = duration;
            this.timeUnit = timeUnit;
        }

        @Override
        public V call(Callable<V> callable) throws Exception {
            return (V)this.timeLimiter.callWithTimeout(callable, this.duration, this.timeUnit, true);
        }
    }

    @Immutable
    private static final class NoAttemptTimeLimit<V>
    implements AttemptTimeLimiter<V> {
        private NoAttemptTimeLimit() {
        }

        @Override
        public V call(Callable<V> callable) throws Exception {
            return callable.call();
        }
    }
}

