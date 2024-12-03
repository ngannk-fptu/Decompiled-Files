/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  javax.annotation.Nonnull
 */
package com.github.rholder.retry;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.AttemptTimeLimiter;
import com.github.rholder.retry.AttemptTimeLimiters;
import com.github.rholder.retry.BlockStrategies;
import com.github.rholder.retry.BlockStrategy;
import com.github.rholder.retry.RetryListener;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.StopStrategy;
import com.github.rholder.retry.WaitStrategies;
import com.github.rholder.retry.WaitStrategy;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class RetryerBuilder<V> {
    private AttemptTimeLimiter<V> attemptTimeLimiter;
    private StopStrategy stopStrategy;
    private WaitStrategy waitStrategy;
    private BlockStrategy blockStrategy;
    private Predicate<Attempt<V>> rejectionPredicate = Predicates.alwaysFalse();
    private List<RetryListener> listeners = new ArrayList<RetryListener>();

    private RetryerBuilder() {
    }

    public static <V> RetryerBuilder<V> newBuilder() {
        return new RetryerBuilder<V>();
    }

    public RetryerBuilder<V> withRetryListener(@Nonnull RetryListener listener) {
        Preconditions.checkNotNull((Object)listener, (Object)"listener may not be null");
        this.listeners.add(listener);
        return this;
    }

    public RetryerBuilder<V> withWaitStrategy(@Nonnull WaitStrategy waitStrategy) throws IllegalStateException {
        Preconditions.checkNotNull((Object)waitStrategy, (Object)"waitStrategy may not be null");
        Preconditions.checkState((this.waitStrategy == null ? 1 : 0) != 0, (String)"a wait strategy has already been set %s", (Object[])new Object[]{this.waitStrategy});
        this.waitStrategy = waitStrategy;
        return this;
    }

    public RetryerBuilder<V> withStopStrategy(@Nonnull StopStrategy stopStrategy) throws IllegalStateException {
        Preconditions.checkNotNull((Object)stopStrategy, (Object)"stopStrategy may not be null");
        Preconditions.checkState((this.stopStrategy == null ? 1 : 0) != 0, (String)"a stop strategy has already been set %s", (Object[])new Object[]{this.stopStrategy});
        this.stopStrategy = stopStrategy;
        return this;
    }

    public RetryerBuilder<V> withBlockStrategy(@Nonnull BlockStrategy blockStrategy) throws IllegalStateException {
        Preconditions.checkNotNull((Object)blockStrategy, (Object)"blockStrategy may not be null");
        Preconditions.checkState((this.blockStrategy == null ? 1 : 0) != 0, (String)"a block strategy has already been set %s", (Object[])new Object[]{this.blockStrategy});
        this.blockStrategy = blockStrategy;
        return this;
    }

    public RetryerBuilder<V> withAttemptTimeLimiter(@Nonnull AttemptTimeLimiter<V> attemptTimeLimiter) {
        Preconditions.checkNotNull(attemptTimeLimiter);
        this.attemptTimeLimiter = attemptTimeLimiter;
        return this;
    }

    public RetryerBuilder<V> retryIfException() {
        this.rejectionPredicate = Predicates.or(this.rejectionPredicate, new ExceptionClassPredicate(Exception.class));
        return this;
    }

    public RetryerBuilder<V> retryIfRuntimeException() {
        this.rejectionPredicate = Predicates.or(this.rejectionPredicate, new ExceptionClassPredicate(RuntimeException.class));
        return this;
    }

    public RetryerBuilder<V> retryIfExceptionOfType(@Nonnull Class<? extends Throwable> exceptionClass) {
        Preconditions.checkNotNull(exceptionClass, (Object)"exceptionClass may not be null");
        this.rejectionPredicate = Predicates.or(this.rejectionPredicate, new ExceptionClassPredicate(exceptionClass));
        return this;
    }

    public RetryerBuilder<V> retryIfException(@Nonnull Predicate<Throwable> exceptionPredicate) {
        Preconditions.checkNotNull(exceptionPredicate, (Object)"exceptionPredicate may not be null");
        this.rejectionPredicate = Predicates.or(this.rejectionPredicate, new ExceptionPredicate(exceptionPredicate));
        return this;
    }

    public RetryerBuilder<V> retryIfResult(@Nonnull Predicate<V> resultPredicate) {
        Preconditions.checkNotNull(resultPredicate, (Object)"resultPredicate may not be null");
        this.rejectionPredicate = Predicates.or(this.rejectionPredicate, new ResultPredicate<V>(resultPredicate));
        return this;
    }

    public Retryer<V> build() {
        AttemptTimeLimiter theAttemptTimeLimiter = this.attemptTimeLimiter == null ? AttemptTimeLimiters.noTimeLimit() : this.attemptTimeLimiter;
        StopStrategy theStopStrategy = this.stopStrategy == null ? StopStrategies.neverStop() : this.stopStrategy;
        WaitStrategy theWaitStrategy = this.waitStrategy == null ? WaitStrategies.noWait() : this.waitStrategy;
        BlockStrategy theBlockStrategy = this.blockStrategy == null ? BlockStrategies.threadSleepStrategy() : this.blockStrategy;
        return new Retryer(theAttemptTimeLimiter, theStopStrategy, theWaitStrategy, theBlockStrategy, this.rejectionPredicate, this.listeners);
    }

    private static final class ExceptionPredicate<V>
    implements Predicate<Attempt<V>> {
        private Predicate<Throwable> delegate;

        public ExceptionPredicate(Predicate<Throwable> delegate) {
            this.delegate = delegate;
        }

        public boolean apply(Attempt<V> attempt) {
            if (!attempt.hasException()) {
                return false;
            }
            return this.delegate.apply((Object)attempt.getExceptionCause());
        }
    }

    private static final class ResultPredicate<V>
    implements Predicate<Attempt<V>> {
        private Predicate<V> delegate;

        public ResultPredicate(Predicate<V> delegate) {
            this.delegate = delegate;
        }

        public boolean apply(Attempt<V> attempt) {
            if (!attempt.hasResult()) {
                return false;
            }
            V result = attempt.getResult();
            return this.delegate.apply(result);
        }
    }

    private static final class ExceptionClassPredicate<V>
    implements Predicate<Attempt<V>> {
        private Class<? extends Throwable> exceptionClass;

        public ExceptionClassPredicate(Class<? extends Throwable> exceptionClass) {
            this.exceptionClass = exceptionClass;
        }

        public boolean apply(Attempt<V> attempt) {
            if (!attempt.hasException()) {
                return false;
            }
            return this.exceptionClass.isAssignableFrom(attempt.getExceptionCause().getClass());
        }
    }
}

