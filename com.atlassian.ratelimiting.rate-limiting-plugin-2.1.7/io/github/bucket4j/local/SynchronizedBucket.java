/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.local;

import io.github.bucket4j.AbstractBucket;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.BucketListener;
import io.github.bucket4j.BucketState;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.EstimationProbe;
import io.github.bucket4j.IncompatibleConfigurationException;
import io.github.bucket4j.TimeMeter;
import io.github.bucket4j.local.LocalBucket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedBucket
extends AbstractBucket
implements LocalBucket {
    private BucketConfiguration configuration;
    private Bandwidth[] bandwidths;
    private final TimeMeter timeMeter;
    private final BucketState state;
    private final Lock lock;

    public SynchronizedBucket(BucketConfiguration configuration, TimeMeter timeMeter) {
        this(configuration, timeMeter, new ReentrantLock());
    }

    SynchronizedBucket(BucketConfiguration configuration, TimeMeter timeMeter, Lock lock) {
        this(BucketListener.NOPE, configuration, timeMeter, lock, BucketState.createInitialState(configuration, timeMeter.currentTimeNanos()));
    }

    private SynchronizedBucket(BucketListener listener, BucketConfiguration configuration, TimeMeter timeMeter, Lock lock, BucketState initialState) {
        super(listener);
        this.configuration = configuration;
        this.bandwidths = configuration.getBandwidths();
        this.timeMeter = timeMeter;
        this.state = initialState;
        this.lock = lock;
    }

    @Override
    public Bucket toListenable(BucketListener listener) {
        return new SynchronizedBucket(listener, this.configuration, this.timeMeter, this.lock, this.state);
    }

    @Override
    public boolean isAsyncModeSupported() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected long consumeAsMuchAsPossibleImpl(long limit) {
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        this.lock.lock();
        try {
            this.state.refillAllBandwidth(this.bandwidths, currentTimeNanos);
            long availableToConsume = this.state.getAvailableTokens(this.bandwidths);
            long toConsume = Math.min(limit, availableToConsume);
            if (toConsume == 0L) {
                long l = 0L;
                return l;
            }
            this.state.consume(this.bandwidths, toConsume);
            long l = toConsume;
            return l;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean tryConsumeImpl(long tokensToConsume) {
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        this.lock.lock();
        try {
            this.state.refillAllBandwidth(this.bandwidths, currentTimeNanos);
            long availableToConsume = this.state.getAvailableTokens(this.bandwidths);
            if (tokensToConsume > availableToConsume) {
                boolean bl = false;
                return bl;
            }
            this.state.consume(this.bandwidths, tokensToConsume);
            boolean bl = true;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected ConsumptionProbe tryConsumeAndReturnRemainingTokensImpl(long tokensToConsume) {
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        this.lock.lock();
        try {
            this.state.refillAllBandwidth(this.bandwidths, currentTimeNanos);
            long availableToConsume = this.state.getAvailableTokens(this.bandwidths);
            if (tokensToConsume > availableToConsume) {
                long nanosToWaitForRefill = this.state.calculateDelayNanosAfterWillBePossibleToConsume(this.bandwidths, tokensToConsume, currentTimeNanos);
                ConsumptionProbe consumptionProbe = ConsumptionProbe.rejected(availableToConsume, nanosToWaitForRefill);
                return consumptionProbe;
            }
            this.state.consume(this.bandwidths, tokensToConsume);
            ConsumptionProbe consumptionProbe = ConsumptionProbe.consumed(availableToConsume - tokensToConsume);
            return consumptionProbe;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected EstimationProbe estimateAbilityToConsumeImpl(long tokensToEstimate) {
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        this.lock.lock();
        try {
            this.state.refillAllBandwidth(this.bandwidths, currentTimeNanos);
            long availableToConsume = this.state.getAvailableTokens(this.bandwidths);
            if (tokensToEstimate > availableToConsume) {
                long nanosToWaitForRefill = this.state.calculateDelayNanosAfterWillBePossibleToConsume(this.bandwidths, tokensToEstimate, currentTimeNanos);
                EstimationProbe estimationProbe = EstimationProbe.canNotBeConsumed(availableToConsume, nanosToWaitForRefill);
                return estimationProbe;
            }
            EstimationProbe estimationProbe = EstimationProbe.canBeConsumed(availableToConsume);
            return estimationProbe;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected long reserveAndCalculateTimeToSleepImpl(long tokensToConsume, long waitIfBusyNanosLimit) {
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        this.lock.lock();
        try {
            this.state.refillAllBandwidth(this.bandwidths, currentTimeNanos);
            long nanosToCloseDeficit = this.state.calculateDelayNanosAfterWillBePossibleToConsume(this.bandwidths, tokensToConsume, currentTimeNanos);
            if (nanosToCloseDeficit == Long.MAX_VALUE || nanosToCloseDeficit > waitIfBusyNanosLimit) {
                long l = Long.MAX_VALUE;
                return l;
            }
            this.state.consume(this.bandwidths, tokensToConsume);
            long l = nanosToCloseDeficit;
            return l;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void addTokensImpl(long tokensToAdd) {
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        this.lock.lock();
        try {
            this.state.refillAllBandwidth(this.bandwidths, currentTimeNanos);
            this.state.addTokens(this.bandwidths, tokensToAdd);
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getAvailableTokens() {
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        this.lock.lock();
        try {
            this.state.refillAllBandwidth(this.bandwidths, currentTimeNanos);
            long l = this.state.getAvailableTokens(this.bandwidths);
            return l;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void replaceConfigurationImpl(BucketConfiguration newConfiguration) {
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        this.lock.lock();
        try {
            this.configuration.checkCompatibility(newConfiguration);
            this.state.refillAllBandwidth(this.bandwidths, currentTimeNanos);
            this.configuration = newConfiguration;
            this.bandwidths = newConfiguration.getBandwidths();
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    protected CompletableFuture<Boolean> tryConsumeAsyncImpl(long tokensToConsume) {
        boolean result = this.tryConsumeImpl(tokensToConsume);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    protected CompletableFuture<Void> addTokensAsyncImpl(long tokensToAdd) {
        this.addTokensImpl(tokensToAdd);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected CompletableFuture<ConsumptionProbe> tryConsumeAndReturnRemainingTokensAsyncImpl(long tokensToConsume) {
        ConsumptionProbe result = this.tryConsumeAndReturnRemainingTokensImpl(tokensToConsume);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    protected CompletableFuture<EstimationProbe> estimateAbilityToConsumeAsyncImpl(long tokensToEstimate) {
        EstimationProbe result = this.estimateAbilityToConsumeImpl(tokensToEstimate);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    protected CompletableFuture<Long> tryConsumeAsMuchAsPossibleAsyncImpl(long limit) {
        long result = this.consumeAsMuchAsPossibleImpl(limit);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    protected CompletableFuture<Long> reserveAndCalculateTimeToSleepAsyncImpl(long tokensToConsume, long maxWaitTimeNanos) {
        long result = this.reserveAndCalculateTimeToSleepImpl(tokensToConsume, maxWaitTimeNanos);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    protected CompletableFuture<Void> replaceConfigurationAsyncImpl(BucketConfiguration newConfiguration) {
        try {
            this.replaceConfigurationImpl(newConfiguration);
            return CompletableFuture.completedFuture(null);
        }
        catch (IncompatibleConfigurationException e) {
            CompletableFuture<Void> fail = new CompletableFuture<Void>();
            fail.completeExceptionally(e);
            return fail;
        }
    }

    @Override
    public BucketState createSnapshot() {
        this.lock.lock();
        try {
            BucketState bucketState = this.state.copy();
            return bucketState;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public BucketConfiguration getConfiguration() {
        return this.configuration;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        SynchronizedBucket synchronizedBucket = this;
        synchronized (synchronizedBucket) {
            return "SynchronizedBucket{state=" + this.state + ", configuration=" + this.getConfiguration() + '}';
        }
    }
}

