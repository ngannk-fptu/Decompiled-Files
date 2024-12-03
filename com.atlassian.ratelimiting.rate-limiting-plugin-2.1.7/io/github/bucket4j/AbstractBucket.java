/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import io.github.bucket4j.AsyncBucket;
import io.github.bucket4j.AsyncScheduledBucket;
import io.github.bucket4j.BlockingBucket;
import io.github.bucket4j.BlockingStrategy;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.BucketExceptions;
import io.github.bucket4j.BucketListener;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.EstimationProbe;
import io.github.bucket4j.UninterruptibleBlockingStrategy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractBucket
implements Bucket,
BlockingBucket {
    private static long INFINITY_DURATION = Long.MAX_VALUE;
    private static long UNLIMITED_AMOUNT = Long.MAX_VALUE;
    private final AsyncScheduledBucketImpl asyncView;
    private final BucketListener listener;

    protected abstract long consumeAsMuchAsPossibleImpl(long var1);

    protected abstract boolean tryConsumeImpl(long var1);

    protected abstract ConsumptionProbe tryConsumeAndReturnRemainingTokensImpl(long var1);

    protected abstract EstimationProbe estimateAbilityToConsumeImpl(long var1);

    protected abstract long reserveAndCalculateTimeToSleepImpl(long var1, long var3);

    protected abstract void addTokensImpl(long var1);

    protected abstract void replaceConfigurationImpl(BucketConfiguration var1);

    protected abstract CompletableFuture<Long> tryConsumeAsMuchAsPossibleAsyncImpl(long var1);

    protected abstract CompletableFuture<Boolean> tryConsumeAsyncImpl(long var1);

    protected abstract CompletableFuture<ConsumptionProbe> tryConsumeAndReturnRemainingTokensAsyncImpl(long var1);

    protected abstract CompletableFuture<EstimationProbe> estimateAbilityToConsumeAsyncImpl(long var1);

    protected abstract CompletableFuture<Long> reserveAndCalculateTimeToSleepAsyncImpl(long var1, long var3);

    protected abstract CompletableFuture<Void> addTokensAsyncImpl(long var1);

    protected abstract CompletableFuture<Void> replaceConfigurationAsyncImpl(BucketConfiguration var1);

    public AbstractBucket(final BucketListener listener) {
        if (listener == null) {
            throw BucketExceptions.nullListener();
        }
        this.listener = listener;
        this.asyncView = new AsyncScheduledBucketImpl(){

            @Override
            public CompletableFuture<Boolean> tryConsume(long tokensToConsume) {
                AbstractBucket.checkTokensToConsume(tokensToConsume);
                return AbstractBucket.this.tryConsumeAsyncImpl(tokensToConsume).thenApply(consumed -> {
                    if (consumed.booleanValue()) {
                        listener.onConsumed(tokensToConsume);
                    } else {
                        listener.onRejected(tokensToConsume);
                    }
                    return consumed;
                });
            }

            @Override
            public CompletableFuture<ConsumptionProbe> tryConsumeAndReturnRemaining(long tokensToConsume) {
                AbstractBucket.checkTokensToConsume(tokensToConsume);
                return AbstractBucket.this.tryConsumeAndReturnRemainingTokensAsyncImpl(tokensToConsume).thenApply(probe -> {
                    if (probe.isConsumed()) {
                        listener.onConsumed(tokensToConsume);
                    } else {
                        listener.onRejected(tokensToConsume);
                    }
                    return probe;
                });
            }

            @Override
            public CompletableFuture<EstimationProbe> estimateAbilityToConsume(long numTokens) {
                AbstractBucket.checkTokensToConsume(numTokens);
                return AbstractBucket.this.estimateAbilityToConsumeAsyncImpl(numTokens);
            }

            @Override
            public CompletableFuture<Long> tryConsumeAsMuchAsPossible() {
                return AbstractBucket.this.tryConsumeAsMuchAsPossibleAsyncImpl(UNLIMITED_AMOUNT).thenApply(consumedTokens -> {
                    if (consumedTokens > 0L) {
                        listener.onConsumed((long)consumedTokens);
                    }
                    return consumedTokens;
                });
            }

            @Override
            public CompletableFuture<Long> tryConsumeAsMuchAsPossible(long limit) {
                AbstractBucket.checkTokensToConsume(limit);
                return AbstractBucket.this.tryConsumeAsMuchAsPossibleAsyncImpl(limit).thenApply(consumedTokens -> {
                    if (consumedTokens > 0L) {
                        listener.onConsumed((long)consumedTokens);
                    }
                    return consumedTokens;
                });
            }

            @Override
            public CompletableFuture<Boolean> tryConsume(long tokensToConsume, long maxWaitTimeNanos, ScheduledExecutorService scheduler) {
                AbstractBucket.checkMaxWaitTime(maxWaitTimeNanos);
                AbstractBucket.checkTokensToConsume(tokensToConsume);
                AbstractBucket.checkScheduler(scheduler);
                CompletableFuture<Boolean> resultFuture = new CompletableFuture<Boolean>();
                CompletableFuture<Long> reservationFuture = AbstractBucket.this.reserveAndCalculateTimeToSleepAsyncImpl(tokensToConsume, maxWaitTimeNanos);
                reservationFuture.whenComplete((nanosToSleep, exception) -> {
                    if (exception != null) {
                        resultFuture.completeExceptionally((Throwable)exception);
                        return;
                    }
                    if (nanosToSleep == INFINITY_DURATION) {
                        resultFuture.complete(false);
                        listener.onRejected(tokensToConsume);
                        return;
                    }
                    if (nanosToSleep == 0L) {
                        resultFuture.complete(true);
                        listener.onConsumed(tokensToConsume);
                        return;
                    }
                    try {
                        listener.onConsumed(tokensToConsume);
                        listener.onDelayed((long)nanosToSleep);
                        Runnable delayedCompletion = () -> resultFuture.complete(true);
                        scheduler.schedule(delayedCompletion, (long)nanosToSleep, TimeUnit.NANOSECONDS);
                    }
                    catch (Throwable t) {
                        resultFuture.completeExceptionally(t);
                    }
                });
                return resultFuture;
            }

            @Override
            public CompletableFuture<Void> consume(long tokensToConsume, ScheduledExecutorService scheduler) {
                AbstractBucket.checkTokensToConsume(tokensToConsume);
                AbstractBucket.checkScheduler(scheduler);
                CompletableFuture<Void> resultFuture = new CompletableFuture<Void>();
                CompletableFuture<Long> reservationFuture = AbstractBucket.this.reserveAndCalculateTimeToSleepAsyncImpl(tokensToConsume, INFINITY_DURATION);
                reservationFuture.whenComplete((nanosToSleep, exception) -> {
                    if (exception != null) {
                        resultFuture.completeExceptionally((Throwable)exception);
                        return;
                    }
                    if (nanosToSleep == INFINITY_DURATION) {
                        String msg = "Existed hardware is unable to service the reservation of so many tokens";
                        resultFuture.completeExceptionally(new IllegalStateException(msg));
                        return;
                    }
                    if (nanosToSleep == 0L) {
                        resultFuture.complete(null);
                        listener.onConsumed(tokensToConsume);
                        return;
                    }
                    try {
                        listener.onConsumed(tokensToConsume);
                        listener.onDelayed((long)nanosToSleep);
                        Runnable delayedCompletion = () -> resultFuture.complete(null);
                        scheduler.schedule(delayedCompletion, (long)nanosToSleep, TimeUnit.NANOSECONDS);
                    }
                    catch (Throwable t) {
                        resultFuture.completeExceptionally(t);
                    }
                });
                return resultFuture;
            }

            @Override
            public CompletableFuture<Void> replaceConfiguration(BucketConfiguration newConfiguration) {
                AbstractBucket.checkConfiguration(newConfiguration);
                return AbstractBucket.this.replaceConfigurationAsyncImpl(newConfiguration);
            }

            @Override
            public CompletableFuture<Void> addTokens(long tokensToAdd) {
                AbstractBucket.checkTokensToAdd(tokensToAdd);
                return AbstractBucket.this.addTokensAsyncImpl(tokensToAdd);
            }
        };
    }

    @Override
    public AsyncBucket asAsync() {
        if (!this.isAsyncModeSupported()) {
            throw new UnsupportedOperationException();
        }
        return this.asyncView;
    }

    @Override
    public AsyncScheduledBucket asAsyncScheduler() {
        if (!this.isAsyncModeSupported()) {
            throw new UnsupportedOperationException();
        }
        return this.asyncView;
    }

    @Override
    public BlockingBucket asScheduler() {
        return this;
    }

    @Override
    public boolean tryConsume(long tokensToConsume) {
        AbstractBucket.checkTokensToConsume(tokensToConsume);
        if (this.tryConsumeImpl(tokensToConsume)) {
            this.listener.onConsumed(tokensToConsume);
            return true;
        }
        this.listener.onRejected(tokensToConsume);
        return false;
    }

    @Override
    public boolean tryConsume(long tokensToConsume, long maxWaitTimeNanos, BlockingStrategy blockingStrategy) throws InterruptedException {
        AbstractBucket.checkTokensToConsume(tokensToConsume);
        AbstractBucket.checkMaxWaitTime(maxWaitTimeNanos);
        long nanosToSleep = this.reserveAndCalculateTimeToSleepImpl(tokensToConsume, maxWaitTimeNanos);
        if (nanosToSleep == INFINITY_DURATION) {
            this.listener.onRejected(tokensToConsume);
            return false;
        }
        this.listener.onConsumed(tokensToConsume);
        if (nanosToSleep > 0L) {
            try {
                blockingStrategy.park(nanosToSleep);
            }
            catch (InterruptedException e) {
                this.listener.onInterrupted(e);
                throw e;
            }
            this.listener.onParked(nanosToSleep);
        }
        return true;
    }

    @Override
    public boolean tryConsumeUninterruptibly(long tokensToConsume, long maxWaitTimeNanos, UninterruptibleBlockingStrategy blockingStrategy) {
        AbstractBucket.checkTokensToConsume(tokensToConsume);
        AbstractBucket.checkMaxWaitTime(maxWaitTimeNanos);
        long nanosToSleep = this.reserveAndCalculateTimeToSleepImpl(tokensToConsume, maxWaitTimeNanos);
        if (nanosToSleep == INFINITY_DURATION) {
            this.listener.onRejected(tokensToConsume);
            return false;
        }
        this.listener.onConsumed(tokensToConsume);
        if (nanosToSleep > 0L) {
            blockingStrategy.parkUninterruptibly(nanosToSleep);
            this.listener.onParked(nanosToSleep);
        }
        return true;
    }

    @Override
    public void consume(long tokensToConsume, BlockingStrategy blockingStrategy) throws InterruptedException {
        AbstractBucket.checkTokensToConsume(tokensToConsume);
        long nanosToSleep = this.reserveAndCalculateTimeToSleepImpl(tokensToConsume, INFINITY_DURATION);
        if (nanosToSleep == INFINITY_DURATION) {
            throw new IllegalStateException("Existed hardware is unable to service the reservation of so many tokens");
        }
        this.listener.onConsumed(tokensToConsume);
        if (nanosToSleep > 0L) {
            try {
                blockingStrategy.park(nanosToSleep);
            }
            catch (InterruptedException e) {
                this.listener.onInterrupted(e);
                throw e;
            }
            this.listener.onParked(nanosToSleep);
        }
    }

    @Override
    public void consumeUninterruptibly(long tokensToConsume, UninterruptibleBlockingStrategy blockingStrategy) {
        AbstractBucket.checkTokensToConsume(tokensToConsume);
        long nanosToSleep = this.reserveAndCalculateTimeToSleepImpl(tokensToConsume, INFINITY_DURATION);
        if (nanosToSleep == INFINITY_DURATION) {
            throw new IllegalStateException("Existed hardware is unable to service the reservation of so many tokens");
        }
        this.listener.onConsumed(tokensToConsume);
        if (nanosToSleep > 0L) {
            blockingStrategy.parkUninterruptibly(nanosToSleep);
            this.listener.onParked(nanosToSleep);
        }
    }

    @Override
    public long tryConsumeAsMuchAsPossible(long limit) {
        AbstractBucket.checkTokensToConsume(limit);
        long consumed = this.consumeAsMuchAsPossibleImpl(limit);
        if (consumed > 0L) {
            this.listener.onConsumed(consumed);
        }
        return consumed;
    }

    @Override
    public long tryConsumeAsMuchAsPossible() {
        long consumed = this.consumeAsMuchAsPossibleImpl(UNLIMITED_AMOUNT);
        if (consumed > 0L) {
            this.listener.onConsumed(consumed);
        }
        return consumed;
    }

    @Override
    public ConsumptionProbe tryConsumeAndReturnRemaining(long tokensToConsume) {
        AbstractBucket.checkTokensToConsume(tokensToConsume);
        ConsumptionProbe probe = this.tryConsumeAndReturnRemainingTokensImpl(tokensToConsume);
        if (probe.isConsumed()) {
            this.listener.onConsumed(tokensToConsume);
        } else {
            this.listener.onRejected(tokensToConsume);
        }
        return probe;
    }

    @Override
    public EstimationProbe estimateAbilityToConsume(long numTokens) {
        AbstractBucket.checkTokensToConsume(numTokens);
        return this.estimateAbilityToConsumeImpl(numTokens);
    }

    @Override
    public void addTokens(long tokensToAdd) {
        AbstractBucket.checkTokensToAdd(tokensToAdd);
        this.addTokensImpl(tokensToAdd);
    }

    @Override
    public void replaceConfiguration(BucketConfiguration newConfiguration) {
        AbstractBucket.checkConfiguration(newConfiguration);
        this.replaceConfigurationImpl(newConfiguration);
    }

    private static void checkTokensToAdd(long tokensToAdd) {
        if (tokensToAdd <= 0L) {
            throw new IllegalArgumentException("tokensToAdd should be >= 0");
        }
    }

    private static void checkTokensToConsume(long tokensToConsume) {
        if (tokensToConsume <= 0L) {
            throw BucketExceptions.nonPositiveTokensToConsume(tokensToConsume);
        }
    }

    private static void checkMaxWaitTime(long maxWaitTimeNanos) {
        if (maxWaitTimeNanos <= 0L) {
            throw BucketExceptions.nonPositiveNanosToWait(maxWaitTimeNanos);
        }
    }

    private static void checkScheduler(ScheduledExecutorService scheduler) {
        if (scheduler == null) {
            throw BucketExceptions.nullScheduler();
        }
    }

    private static void checkConfiguration(BucketConfiguration newConfiguration) {
        if (newConfiguration == null) {
            throw BucketExceptions.nullConfiguration();
        }
    }

    private static interface AsyncScheduledBucketImpl
    extends AsyncBucket,
    AsyncScheduledBucket {
    }
}

