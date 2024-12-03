/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.Beta
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.Immutable
 */
package com.github.rholder.retry;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.AttemptTimeLimiter;
import com.github.rholder.retry.AttemptTimeLimiters;
import com.github.rholder.retry.BlockStrategies;
import com.github.rholder.retry.BlockStrategy;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.RetryListener;
import com.github.rholder.retry.StopStrategy;
import com.github.rholder.retry.WaitStrategy;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

public final class Retryer<V> {
    private final StopStrategy stopStrategy;
    private final WaitStrategy waitStrategy;
    private final BlockStrategy blockStrategy;
    private final AttemptTimeLimiter<V> attemptTimeLimiter;
    private final Predicate<Attempt<V>> rejectionPredicate;
    private final Collection<RetryListener> listeners;

    public Retryer(@Nonnull StopStrategy stopStrategy, @Nonnull WaitStrategy waitStrategy, @Nonnull Predicate<Attempt<V>> rejectionPredicate) {
        this(AttemptTimeLimiters.noTimeLimit(), stopStrategy, waitStrategy, BlockStrategies.threadSleepStrategy(), rejectionPredicate);
    }

    public Retryer(@Nonnull AttemptTimeLimiter<V> attemptTimeLimiter, @Nonnull StopStrategy stopStrategy, @Nonnull WaitStrategy waitStrategy, @Nonnull Predicate<Attempt<V>> rejectionPredicate) {
        this(attemptTimeLimiter, stopStrategy, waitStrategy, BlockStrategies.threadSleepStrategy(), rejectionPredicate);
    }

    public Retryer(@Nonnull AttemptTimeLimiter<V> attemptTimeLimiter, @Nonnull StopStrategy stopStrategy, @Nonnull WaitStrategy waitStrategy, @Nonnull BlockStrategy blockStrategy, @Nonnull Predicate<Attempt<V>> rejectionPredicate) {
        this(attemptTimeLimiter, stopStrategy, waitStrategy, blockStrategy, rejectionPredicate, new ArrayList<RetryListener>());
    }

    @Beta
    public Retryer(@Nonnull AttemptTimeLimiter<V> attemptTimeLimiter, @Nonnull StopStrategy stopStrategy, @Nonnull WaitStrategy waitStrategy, @Nonnull BlockStrategy blockStrategy, @Nonnull Predicate<Attempt<V>> rejectionPredicate, @Nonnull Collection<RetryListener> listeners) {
        Preconditions.checkNotNull(attemptTimeLimiter, (Object)"timeLimiter may not be null");
        Preconditions.checkNotNull((Object)stopStrategy, (Object)"stopStrategy may not be null");
        Preconditions.checkNotNull((Object)waitStrategy, (Object)"waitStrategy may not be null");
        Preconditions.checkNotNull((Object)blockStrategy, (Object)"blockStrategy may not be null");
        Preconditions.checkNotNull(rejectionPredicate, (Object)"rejectionPredicate may not be null");
        Preconditions.checkNotNull(listeners, (Object)"listeners may not null");
        this.attemptTimeLimiter = attemptTimeLimiter;
        this.stopStrategy = stopStrategy;
        this.waitStrategy = waitStrategy;
        this.blockStrategy = blockStrategy;
        this.rejectionPredicate = rejectionPredicate;
        this.listeners = listeners;
    }

    public V call(Callable<V> callable) throws ExecutionException, RetryException {
        long startTime = System.nanoTime();
        int attemptNumber = 1;
        while (true) {
            Attempt<Object> attempt;
            try {
                V result = this.attemptTimeLimiter.call(callable);
                attempt = new ResultAttempt<V>(result, attemptNumber, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
            }
            catch (Throwable t) {
                attempt = new ExceptionAttempt(t, attemptNumber, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
            }
            for (RetryListener listener : this.listeners) {
                listener.onRetry(attempt);
            }
            if (!this.rejectionPredicate.apply(attempt)) {
                return (V)attempt.get();
            }
            if (this.stopStrategy.shouldStop(attempt)) {
                throw new RetryException(attemptNumber, attempt);
            }
            long sleepTime = this.waitStrategy.computeSleepTime(attempt);
            try {
                this.blockStrategy.block(sleepTime);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RetryException(attemptNumber, attempt);
            }
            ++attemptNumber;
        }
    }

    public RetryerCallable<V> wrap(Callable<V> callable) {
        return new RetryerCallable(this, callable);
    }

    public static class RetryerCallable<X>
    implements Callable<X> {
        private Retryer<X> retryer;
        private Callable<X> callable;

        private RetryerCallable(Retryer<X> retryer, Callable<X> callable) {
            this.retryer = retryer;
            this.callable = callable;
        }

        @Override
        public X call() throws ExecutionException, RetryException {
            return this.retryer.call(this.callable);
        }
    }

    @Immutable
    static final class ExceptionAttempt<R>
    implements Attempt<R> {
        private final ExecutionException e;
        private final long attemptNumber;
        private final long delaySinceFirstAttempt;

        public ExceptionAttempt(Throwable cause, long attemptNumber, long delaySinceFirstAttempt) {
            this.e = new ExecutionException(cause);
            this.attemptNumber = attemptNumber;
            this.delaySinceFirstAttempt = delaySinceFirstAttempt;
        }

        @Override
        public R get() throws ExecutionException {
            throw this.e;
        }

        @Override
        public boolean hasResult() {
            return false;
        }

        @Override
        public boolean hasException() {
            return true;
        }

        @Override
        public R getResult() throws IllegalStateException {
            throw new IllegalStateException("The attempt resulted in an exception, not in a result");
        }

        @Override
        public Throwable getExceptionCause() throws IllegalStateException {
            return this.e.getCause();
        }

        @Override
        public long getAttemptNumber() {
            return this.attemptNumber;
        }

        @Override
        public long getDelaySinceFirstAttempt() {
            return this.delaySinceFirstAttempt;
        }
    }

    @Immutable
    static final class ResultAttempt<R>
    implements Attempt<R> {
        private final R result;
        private final long attemptNumber;
        private final long delaySinceFirstAttempt;

        public ResultAttempt(R result, long attemptNumber, long delaySinceFirstAttempt) {
            this.result = result;
            this.attemptNumber = attemptNumber;
            this.delaySinceFirstAttempt = delaySinceFirstAttempt;
        }

        @Override
        public R get() throws ExecutionException {
            return this.result;
        }

        @Override
        public boolean hasResult() {
            return true;
        }

        @Override
        public boolean hasException() {
            return false;
        }

        @Override
        public R getResult() throws IllegalStateException {
            return this.result;
        }

        @Override
        public Throwable getExceptionCause() throws IllegalStateException {
            throw new IllegalStateException("The attempt resulted in a result, not in an exception");
        }

        @Override
        public long getAttemptNumber() {
            return this.attemptNumber;
        }

        @Override
        public long getDelaySinceFirstAttempt() {
            return this.delaySinceFirstAttempt;
        }
    }
}

