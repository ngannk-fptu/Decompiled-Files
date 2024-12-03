/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import java.io.Serializable;
import java.util.Arrays;

public class BucketState
implements Serializable {
    private static final long serialVersionUID = 42L;
    private static final int BANDWIDTH_SIZE = 3;
    final long[] stateData;

    BucketState(long[] stateData) {
        this.stateData = stateData;
    }

    public BucketState(BucketConfiguration configuration, long currentTimeNanos) {
        Bandwidth[] bandwidths = configuration.getBandwidths();
        this.stateData = new long[bandwidths.length * 3];
        for (int i = 0; i < bandwidths.length; ++i) {
            this.setCurrentSize(i, bandwidths[i].initialTokens);
            this.setLastRefillTimeNanos(i, currentTimeNanos);
        }
    }

    public BucketState copy() {
        return new BucketState((long[])this.stateData.clone());
    }

    public void copyStateFrom(BucketState sourceState) {
        System.arraycopy(sourceState.stateData, 0, this.stateData, 0, this.stateData.length);
    }

    public static BucketState createInitialState(BucketConfiguration configuration, long currentTimeNanos) {
        return new BucketState(configuration, currentTimeNanos);
    }

    public long getAvailableTokens(Bandwidth[] bandwidths) {
        long availableTokens = this.getCurrentSize(0);
        for (int i = 1; i < bandwidths.length; ++i) {
            availableTokens = Math.min(availableTokens, this.getCurrentSize(i));
        }
        return availableTokens;
    }

    public void consume(Bandwidth[] bandwidths, long toConsume) {
        for (int i = 0; i < bandwidths.length; ++i) {
            this.consume(i, toConsume);
        }
    }

    public long calculateDelayNanosAfterWillBePossibleToConsume(Bandwidth[] bandwidths, long tokensToConsume, long currentTimeNanos) {
        long delayAfterWillBePossibleToConsume = this.calculateDelayNanosAfterWillBePossibleToConsume(0, bandwidths[0], tokensToConsume, currentTimeNanos);
        for (int i = 1; i < bandwidths.length; ++i) {
            Bandwidth bandwidth = bandwidths[i];
            long delay = this.calculateDelayNanosAfterWillBePossibleToConsume(i, bandwidth, tokensToConsume, currentTimeNanos);
            delayAfterWillBePossibleToConsume = Math.max(delayAfterWillBePossibleToConsume, delay);
        }
        return delayAfterWillBePossibleToConsume;
    }

    public void refillAllBandwidth(Bandwidth[] limits, long currentTimeNanos) {
        for (int i = 0; i < limits.length; ++i) {
            this.refill(i, limits[i], currentTimeNanos);
        }
    }

    public void addTokens(Bandwidth[] limits, long tokensToAdd) {
        for (int i = 0; i < limits.length; ++i) {
            this.addTokens(i, limits[i], tokensToAdd);
        }
    }

    private void addTokens(int bandwidthIndex, Bandwidth bandwidth, long tokensToAdd) {
        long currentSize = this.getCurrentSize(bandwidthIndex);
        long newSize = currentSize + tokensToAdd;
        if (newSize >= bandwidth.capacity) {
            this.resetBandwidth(bandwidthIndex, bandwidth.capacity);
        } else if (newSize < currentSize) {
            this.resetBandwidth(bandwidthIndex, bandwidth.capacity);
        } else {
            this.setCurrentSize(bandwidthIndex, newSize);
        }
    }

    private void refill(int bandwidthIndex, Bandwidth bandwidth, long currentTimeNanos) {
        long previousRefillNanos = this.getLastRefillTimeNanos(bandwidthIndex);
        if (currentTimeNanos <= previousRefillNanos) {
            return;
        }
        if (bandwidth.refillIntervally) {
            long incompleteIntervalCorrection = (currentTimeNanos - previousRefillNanos) % bandwidth.refillPeriodNanos;
            currentTimeNanos -= incompleteIntervalCorrection;
        }
        if (currentTimeNanos <= previousRefillNanos) {
            return;
        }
        this.setLastRefillTimeNanos(bandwidthIndex, currentTimeNanos);
        long capacity = bandwidth.capacity;
        long refillPeriodNanos = bandwidth.refillPeriodNanos;
        long refillTokens = bandwidth.refillTokens;
        long currentSize = this.getCurrentSize(bandwidthIndex);
        long durationSinceLastRefillNanos = currentTimeNanos - previousRefillNanos;
        long newSize = currentSize;
        if (durationSinceLastRefillNanos > refillPeriodNanos) {
            long elapsedPeriods = durationSinceLastRefillNanos / refillPeriodNanos;
            long calculatedRefill = elapsedPeriods * refillTokens;
            if ((newSize += calculatedRefill) > capacity) {
                this.resetBandwidth(bandwidthIndex, capacity);
                return;
            }
            if (newSize < currentSize) {
                this.resetBandwidth(bandwidthIndex, capacity);
                return;
            }
            durationSinceLastRefillNanos %= refillPeriodNanos;
        }
        long roundingError = this.getRoundingError(bandwidthIndex);
        long dividedWithoutError = BucketState.multiplyExactOrReturnMaxValue(refillTokens, durationSinceLastRefillNanos);
        long divided = dividedWithoutError + roundingError;
        if (divided < 0L || dividedWithoutError == Long.MAX_VALUE) {
            long calculatedRefill = (long)((double)durationSinceLastRefillNanos / (double)refillPeriodNanos * (double)refillTokens);
            newSize += calculatedRefill;
            roundingError = 0L;
        } else {
            long calculatedRefill = divided / refillPeriodNanos;
            if (calculatedRefill == 0L) {
                roundingError = divided;
            } else {
                newSize += calculatedRefill;
                roundingError = divided % refillPeriodNanos;
            }
        }
        if (newSize >= capacity) {
            this.resetBandwidth(bandwidthIndex, capacity);
            return;
        }
        if (newSize < currentSize) {
            this.resetBandwidth(bandwidthIndex, capacity);
            return;
        }
        this.setCurrentSize(bandwidthIndex, newSize);
        this.setRoundingError(bandwidthIndex, roundingError);
    }

    private void resetBandwidth(int bandwidthIndex, long capacity) {
        this.setCurrentSize(bandwidthIndex, capacity);
        this.setRoundingError(bandwidthIndex, 0L);
    }

    private long calculateDelayNanosAfterWillBePossibleToConsume(int bandwidthIndex, Bandwidth bandwidth, long tokens, long currentTimeNanos) {
        long currentSize = this.getCurrentSize(bandwidthIndex);
        if (tokens <= currentSize) {
            return 0L;
        }
        long deficit = tokens - currentSize;
        if (deficit <= 0L) {
            return Long.MAX_VALUE;
        }
        if (bandwidth.refillIntervally) {
            return this.calculateDelayNanosAfterWillBePossibleToConsumeForIntervalBandwidth(bandwidthIndex, bandwidth, deficit, currentTimeNanos);
        }
        return this.calculateDelayNanosAfterWillBePossibleToConsumeForGreedyBandwidth(bandwidthIndex, bandwidth, deficit);
    }

    private long calculateDelayNanosAfterWillBePossibleToConsumeForGreedyBandwidth(int bandwidthIndex, Bandwidth bandwidth, long deficit) {
        long refillPeriodNanos = bandwidth.refillPeriodNanos;
        long refillPeriodTokens = bandwidth.refillTokens;
        long divided = BucketState.multiplyExactOrReturnMaxValue(refillPeriodNanos, deficit);
        if (divided == Long.MAX_VALUE) {
            return (long)((double)deficit / (double)refillPeriodTokens * (double)refillPeriodNanos);
        }
        long correctionForPartiallyRefilledToken = this.getRoundingError(bandwidthIndex);
        return (divided -= correctionForPartiallyRefilledToken) / refillPeriodTokens;
    }

    private long calculateDelayNanosAfterWillBePossibleToConsumeForIntervalBandwidth(int bandwidthIndex, Bandwidth bandwidth, long deficit, long currentTimeNanos) {
        long refillPeriodNanos = bandwidth.refillPeriodNanos;
        long refillTokens = bandwidth.refillTokens;
        long previousRefillNanos = this.getLastRefillTimeNanos(bandwidthIndex);
        long timeOfNextRefillNanos = previousRefillNanos + refillPeriodNanos;
        long waitForNextRefillNanos = timeOfNextRefillNanos - currentTimeNanos;
        if (deficit <= refillTokens) {
            return waitForNextRefillNanos;
        }
        if ((deficit -= refillTokens) < refillTokens) {
            return waitForNextRefillNanos + refillPeriodNanos;
        }
        long deficitPeriods = deficit / refillTokens + (long)(deficit % refillTokens == 0L ? 0 : 1);
        long deficitNanos = BucketState.multiplyExactOrReturnMaxValue(deficitPeriods, refillPeriodNanos);
        if (deficitNanos == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        if ((deficitNanos += waitForNextRefillNanos) < 0L) {
            return Long.MAX_VALUE;
        }
        return deficitNanos;
    }

    private long getLastRefillTimeNanos(int bandwidth) {
        return this.stateData[bandwidth * 3];
    }

    private void setLastRefillTimeNanos(int bandwidth, long nanos) {
        this.stateData[bandwidth * 3] = nanos;
    }

    long getCurrentSize(int bandwidth) {
        return this.stateData[bandwidth * 3 + 1];
    }

    private void setCurrentSize(int bandwidth, long currentSize) {
        this.stateData[bandwidth * 3 + 1] = currentSize;
    }

    private void consume(int bandwidth, long tokens) {
        int n = bandwidth * 3 + 1;
        this.stateData[n] = this.stateData[n] - tokens;
    }

    long getRoundingError(int bandwidth) {
        return this.stateData[bandwidth * 3 + 2];
    }

    private void setRoundingError(int bandwidth, long roundingError) {
        this.stateData[bandwidth * 3 + 2] = roundingError;
    }

    public String toString() {
        return "BucketState{bandwidthStates=" + Arrays.toString(this.stateData) + '}';
    }

    private static long multiplyExactOrReturnMaxValue(long x, long y) {
        long ay;
        long r = x * y;
        long ax = Math.abs(x);
        if ((ax | (ay = Math.abs(y))) >>> 31 != 0L && (y != 0L && r / y != x || x == Long.MIN_VALUE && y == -1L)) {
            return Long.MAX_VALUE;
        }
        return r;
    }
}

