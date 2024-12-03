/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.config.InvalidConfigurationException;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.Histogram;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

abstract class AbstractTimeWindowHistogram<T, U>
implements Histogram {
    private static final AtomicIntegerFieldUpdater<AbstractTimeWindowHistogram> rotatingUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractTimeWindowHistogram.class, "rotating");
    final DistributionStatisticConfig distributionStatisticConfig;
    private final Clock clock;
    private final boolean supportsAggregablePercentiles;
    private final T[] ringBuffer;
    private short currentBucket;
    private final long durationBetweenRotatesMillis;
    private volatile boolean accumulatedHistogramStale;
    private volatile long lastRotateTimestampMillis;
    private volatile int rotating;
    @Nullable
    private U accumulatedHistogram;

    AbstractTimeWindowHistogram(Clock clock, DistributionStatisticConfig distributionStatisticConfig, Class<T> bucketType, boolean supportsAggregablePercentiles) {
        this.clock = clock;
        this.distributionStatisticConfig = AbstractTimeWindowHistogram.validateDistributionConfig(distributionStatisticConfig);
        this.supportsAggregablePercentiles = supportsAggregablePercentiles;
        int ageBuckets = distributionStatisticConfig.getBufferLength();
        this.ringBuffer = (Object[])Array.newInstance(bucketType, ageBuckets);
        this.durationBetweenRotatesMillis = distributionStatisticConfig.getExpiry().toMillis() / (long)ageBuckets;
        if (this.durationBetweenRotatesMillis <= 0L) {
            AbstractTimeWindowHistogram.rejectHistogramConfig("expiry (" + distributionStatisticConfig.getExpiry().toMillis() + "ms) / bufferLength (" + ageBuckets + ") must be greater than 0.");
        }
        this.currentBucket = 0;
        this.lastRotateTimestampMillis = clock.wallTime();
    }

    private static DistributionStatisticConfig validateDistributionConfig(DistributionStatisticConfig distributionStatisticConfig) {
        if (distributionStatisticConfig.getPercentiles() != null && distributionStatisticConfig.getPercentilePrecision() == null) {
            AbstractTimeWindowHistogram.rejectHistogramConfig("when publishing percentiles a precision must be specified.");
        }
        if (distributionStatisticConfig.getMinimumExpectedValueAsDouble() == null || distributionStatisticConfig.getMaximumExpectedValueAsDouble() == null) {
            AbstractTimeWindowHistogram.rejectHistogramConfig("minimumExpectedValue and maximumExpectedValue must be non null");
        }
        return distributionStatisticConfig;
    }

    private static void rejectHistogramConfig(String msg) {
        throw new InvalidConfigurationException("Invalid distribution configuration: " + msg);
    }

    void initRingBuffer() {
        for (int i = 0; i < this.ringBuffer.length; ++i) {
            this.ringBuffer[i] = this.newBucket();
        }
        this.accumulatedHistogram = this.newAccumulatedHistogram(this.ringBuffer);
    }

    abstract T newBucket();

    abstract void recordLong(T var1, long var2);

    abstract void recordDouble(T var1, double var2);

    abstract void resetBucket(T var1);

    abstract U newAccumulatedHistogram(T[] var1);

    abstract void accumulate();

    abstract void resetAccumulatedHistogram();

    abstract double valueAtPercentile(double var1);

    abstract Iterator<CountAtBucket> countsAtValues(Iterator<Double> var1);

    void outputSummary(PrintStream out, double bucketScaling) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final HistogramSnapshot takeSnapshot(long count, double total, double max) {
        CountAtBucket[] counts;
        ValueAtPercentile[] values;
        this.rotate();
        AbstractTimeWindowHistogram abstractTimeWindowHistogram = this;
        synchronized (abstractTimeWindowHistogram) {
            this.accumulateIfStale();
            values = this.takeValueSnapshot();
            counts = this.takeCountSnapshot();
        }
        return new HistogramSnapshot(count, total, max, values, counts, this::outputSummary);
    }

    private void accumulateIfStale() {
        if (this.accumulatedHistogramStale) {
            this.accumulate();
            this.accumulatedHistogramStale = false;
        }
    }

    private ValueAtPercentile[] takeValueSnapshot() {
        double[] monitoredPercentiles = this.distributionStatisticConfig.getPercentiles();
        if (monitoredPercentiles == null || monitoredPercentiles.length == 0) {
            return null;
        }
        ValueAtPercentile[] values = new ValueAtPercentile[monitoredPercentiles.length];
        for (int i = 0; i < monitoredPercentiles.length; ++i) {
            double p = monitoredPercentiles[i];
            values[i] = new ValueAtPercentile(p, this.valueAtPercentile(p * 100.0));
        }
        return values;
    }

    private CountAtBucket[] takeCountSnapshot() {
        if (!this.distributionStatisticConfig.isPublishingHistogram()) {
            return null;
        }
        NavigableSet<Double> monitoredValues = this.distributionStatisticConfig.getHistogramBuckets(this.supportsAggregablePercentiles);
        if (monitoredValues.isEmpty()) {
            return null;
        }
        CountAtBucket[] counts = new CountAtBucket[monitoredValues.size()];
        Iterator<CountAtBucket> iterator = this.countsAtValues(monitoredValues.iterator());
        for (int i = 0; i < counts.length; ++i) {
            counts[i] = iterator.next();
        }
        return counts;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void recordLong(long value) {
        this.rotate();
        try {
            for (T bucket : this.ringBuffer) {
                this.recordLong(bucket, value);
            }
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        }
        finally {
            this.accumulatedHistogramStale = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void recordDouble(double value) {
        this.rotate();
        try {
            for (T bucket : this.ringBuffer) {
                this.recordDouble(bucket, value);
            }
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        }
        finally {
            this.accumulatedHistogramStale = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void rotate() {
        long timeSinceLastRotateMillis = this.clock.wallTime() - this.lastRotateTimestampMillis;
        if (timeSinceLastRotateMillis < this.durationBetweenRotatesMillis) {
            return;
        }
        if (!rotatingUpdater.compareAndSet(this, 0, 1)) {
            return;
        }
        try {
            int iterations = 0;
            AbstractTimeWindowHistogram abstractTimeWindowHistogram = this;
            synchronized (abstractTimeWindowHistogram) {
                do {
                    this.resetBucket(this.ringBuffer[this.currentBucket]);
                    this.currentBucket = (short)(this.currentBucket + 1);
                    if (this.currentBucket >= this.ringBuffer.length) {
                        this.currentBucket = 0;
                    }
                    this.lastRotateTimestampMillis += this.durationBetweenRotatesMillis;
                } while ((timeSinceLastRotateMillis -= this.durationBetweenRotatesMillis) >= this.durationBetweenRotatesMillis && ++iterations < this.ringBuffer.length);
                this.resetAccumulatedHistogram();
                this.accumulatedHistogramStale = true;
            }
        }
        finally {
            this.rotating = 0;
        }
    }

    protected U accumulatedHistogram() {
        return this.accumulatedHistogram;
    }

    protected T currentHistogram() {
        return this.ringBuffer[this.currentBucket];
    }
}

