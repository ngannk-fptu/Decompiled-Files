/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.local;

import io.github.bucket4j.AbstractBucket;
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
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeBucket
extends AbstractBucket
implements LocalBucket {
    private final TimeMeter timeMeter;
    private final AtomicReference<StateWithConfiguration> stateRef;

    public LockFreeBucket(BucketConfiguration configuration, TimeMeter timeMeter) {
        this(new AtomicReference<StateWithConfiguration>(LockFreeBucket.createStateWithConfiguration(configuration, timeMeter)), timeMeter, BucketListener.NOPE);
    }

    private LockFreeBucket(AtomicReference<StateWithConfiguration> stateRef, TimeMeter timeMeter, BucketListener listener) {
        super(listener);
        this.timeMeter = timeMeter;
        this.stateRef = stateRef;
    }

    @Override
    public Bucket toListenable(BucketListener listener) {
        return new LockFreeBucket(this.stateRef, this.timeMeter, listener);
    }

    @Override
    public boolean isAsyncModeSupported() {
        return true;
    }

    @Override
    protected long consumeAsMuchAsPossibleImpl(long limit) {
        StateWithConfiguration previousState = this.stateRef.get();
        StateWithConfiguration newState = previousState.copy();
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        while (true) {
            newState.refillAllBandwidth(currentTimeNanos);
            long availableToConsume = newState.getAvailableTokens();
            long toConsume = Math.min(limit, availableToConsume);
            if (toConsume == 0L) {
                return 0L;
            }
            newState.consume(toConsume);
            if (this.stateRef.compareAndSet(previousState, newState)) {
                return toConsume;
            }
            previousState = this.stateRef.get();
            newState.copyStateFrom(previousState);
        }
    }

    @Override
    protected boolean tryConsumeImpl(long tokensToConsume) {
        StateWithConfiguration previousState = this.stateRef.get();
        StateWithConfiguration newState = previousState.copy();
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        while (true) {
            newState.refillAllBandwidth(currentTimeNanos);
            long availableToConsume = newState.getAvailableTokens();
            if (tokensToConsume > availableToConsume) {
                return false;
            }
            newState.consume(tokensToConsume);
            if (this.stateRef.compareAndSet(previousState, newState)) {
                return true;
            }
            previousState = this.stateRef.get();
            newState.copyStateFrom(previousState);
        }
    }

    @Override
    protected ConsumptionProbe tryConsumeAndReturnRemainingTokensImpl(long tokensToConsume) {
        StateWithConfiguration previousState = this.stateRef.get();
        StateWithConfiguration newState = previousState.copy();
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        while (true) {
            newState.refillAllBandwidth(currentTimeNanos);
            long availableToConsume = newState.getAvailableTokens();
            if (tokensToConsume > availableToConsume) {
                long nanosToWaitForRefill = newState.delayNanosAfterWillBePossibleToConsume(tokensToConsume, currentTimeNanos);
                return ConsumptionProbe.rejected(availableToConsume, nanosToWaitForRefill);
            }
            newState.consume(tokensToConsume);
            if (this.stateRef.compareAndSet(previousState, newState)) {
                return ConsumptionProbe.consumed(availableToConsume - tokensToConsume);
            }
            previousState = this.stateRef.get();
            newState.copyStateFrom(previousState);
        }
    }

    @Override
    protected EstimationProbe estimateAbilityToConsumeImpl(long tokensToEstimate) {
        StateWithConfiguration previousState = this.stateRef.get();
        StateWithConfiguration newState = previousState.copy();
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        newState.refillAllBandwidth(currentTimeNanos);
        long availableToConsume = newState.getAvailableTokens();
        if (tokensToEstimate > availableToConsume) {
            long nanosToWaitForRefill = newState.delayNanosAfterWillBePossibleToConsume(tokensToEstimate, currentTimeNanos);
            return EstimationProbe.canNotBeConsumed(availableToConsume, nanosToWaitForRefill);
        }
        return EstimationProbe.canBeConsumed(availableToConsume);
    }

    @Override
    protected long reserveAndCalculateTimeToSleepImpl(long tokensToConsume, long waitIfBusyNanosLimit) {
        StateWithConfiguration previousState = this.stateRef.get();
        StateWithConfiguration newState = previousState.copy();
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        while (true) {
            newState.refillAllBandwidth(currentTimeNanos);
            long nanosToCloseDeficit = newState.delayNanosAfterWillBePossibleToConsume(tokensToConsume, currentTimeNanos);
            if (nanosToCloseDeficit == 0L) {
                newState.consume(tokensToConsume);
                if (this.stateRef.compareAndSet(previousState, newState)) {
                    return 0L;
                }
                previousState = this.stateRef.get();
                newState.copyStateFrom(previousState);
                continue;
            }
            if (nanosToCloseDeficit == Long.MAX_VALUE || nanosToCloseDeficit > waitIfBusyNanosLimit) {
                return Long.MAX_VALUE;
            }
            newState.consume(tokensToConsume);
            if (this.stateRef.compareAndSet(previousState, newState)) {
                return nanosToCloseDeficit;
            }
            previousState = this.stateRef.get();
            newState.copyStateFrom(previousState);
        }
    }

    @Override
    protected void addTokensImpl(long tokensToAdd) {
        StateWithConfiguration previousState = this.stateRef.get();
        StateWithConfiguration newState = previousState.copy();
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        while (true) {
            newState.refillAllBandwidth(currentTimeNanos);
            newState.state.addTokens(newState.configuration.getBandwidths(), tokensToAdd);
            if (this.stateRef.compareAndSet(previousState, newState)) {
                return;
            }
            previousState = this.stateRef.get();
            newState.copyStateFrom(previousState);
        }
    }

    @Override
    protected void replaceConfigurationImpl(BucketConfiguration newConfiguration) {
        StateWithConfiguration previousState = this.stateRef.get();
        StateWithConfiguration newState = previousState.copy();
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        while (true) {
            previousState.configuration.checkCompatibility(newConfiguration);
            newState.refillAllBandwidth(currentTimeNanos);
            newState.configuration = newConfiguration;
            if (this.stateRef.compareAndSet(previousState, newState)) {
                return;
            }
            previousState = this.stateRef.get();
            newState.copyStateFrom(previousState);
        }
    }

    @Override
    public long getAvailableTokens() {
        long currentTimeNanos = this.timeMeter.currentTimeNanos();
        StateWithConfiguration snapshot = this.stateRef.get().copy();
        snapshot.refillAllBandwidth(currentTimeNanos);
        return snapshot.getAvailableTokens();
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
    public BucketState createSnapshot() {
        return this.stateRef.get().state.copy();
    }

    @Override
    public BucketConfiguration getConfiguration() {
        return this.stateRef.get().configuration;
    }

    private static StateWithConfiguration createStateWithConfiguration(BucketConfiguration configuration, TimeMeter timeMeter) {
        BucketState initialState = BucketState.createInitialState(configuration, timeMeter.currentTimeNanos());
        return new StateWithConfiguration(configuration, initialState);
    }

    public String toString() {
        return "LockFreeBucket{state=" + this.stateRef.get() + ", configuration=" + this.getConfiguration() + '}';
    }

    private static class StateWithConfiguration {
        BucketConfiguration configuration;
        BucketState state;

        StateWithConfiguration(BucketConfiguration configuration, BucketState state) {
            this.configuration = configuration;
            this.state = state;
        }

        StateWithConfiguration copy() {
            return new StateWithConfiguration(this.configuration, this.state.copy());
        }

        void copyStateFrom(StateWithConfiguration other) {
            this.configuration = other.configuration;
            this.state.copyStateFrom(other.state);
        }

        void refillAllBandwidth(long currentTimeNanos) {
            this.state.refillAllBandwidth(this.configuration.getBandwidths(), currentTimeNanos);
        }

        long getAvailableTokens() {
            return this.state.getAvailableTokens(this.configuration.getBandwidths());
        }

        void consume(long tokensToConsume) {
            this.state.consume(this.configuration.getBandwidths(), tokensToConsume);
        }

        long delayNanosAfterWillBePossibleToConsume(long tokensToConsume, long currentTimeNanos) {
            return this.state.calculateDelayNanosAfterWillBePossibleToConsume(this.configuration.getBandwidths(), tokensToConsume, currentTimeNanos);
        }
    }
}

