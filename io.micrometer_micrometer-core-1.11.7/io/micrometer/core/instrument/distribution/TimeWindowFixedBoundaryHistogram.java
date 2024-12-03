/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.distribution.AbstractTimeWindowHistogram;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.FixedBoundaryHistogram;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.NavigableSet;
import java.util.Objects;

public class TimeWindowFixedBoundaryHistogram
extends AbstractTimeWindowHistogram<FixedBoundaryHistogram, Void> {
    private final double[] buckets;
    private final boolean isCumulativeBucketCounts;

    public TimeWindowFixedBoundaryHistogram(Clock clock, DistributionStatisticConfig config, boolean supportsAggregablePercentiles) {
        this(clock, config, supportsAggregablePercentiles, true);
    }

    public TimeWindowFixedBoundaryHistogram(Clock clock, DistributionStatisticConfig config, boolean supportsAggregablePercentiles, boolean isCumulativeBucketCounts) {
        super(clock, config, FixedBoundaryHistogram.class, supportsAggregablePercentiles);
        this.isCumulativeBucketCounts = isCumulativeBucketCounts;
        NavigableSet<Double> histogramBuckets = this.distributionStatisticConfig.getHistogramBuckets(supportsAggregablePercentiles);
        this.buckets = histogramBuckets.stream().filter(Objects::nonNull).mapToDouble(Double::doubleValue).toArray();
        this.initRingBuffer();
    }

    @Override
    FixedBoundaryHistogram newBucket() {
        return new FixedBoundaryHistogram(this.buckets, this.isCumulativeBucketCounts);
    }

    @Override
    void recordLong(FixedBoundaryHistogram bucket, long value) {
        bucket.record(value);
    }

    @Override
    final void recordDouble(FixedBoundaryHistogram bucket, double value) {
        this.recordLong(bucket, (long)Math.ceil(value));
    }

    @Override
    void resetBucket(FixedBoundaryHistogram bucket) {
        bucket.reset();
    }

    Void newAccumulatedHistogram(FixedBoundaryHistogram[] ringBuffer) {
        return null;
    }

    @Override
    void accumulate() {
    }

    @Override
    void resetAccumulatedHistogram() {
    }

    @Override
    double valueAtPercentile(double percentile) {
        return 0.0;
    }

    @Override
    Iterator<CountAtBucket> countsAtValues(Iterator<Double> values) {
        return ((FixedBoundaryHistogram)this.currentHistogram()).countsAtValues(values);
    }

    @Override
    void outputSummary(PrintStream printStream, double bucketScaling) {
        printStream.format("%14s %10s\n\n", "Bucket", "TotalCount");
        String bucketFormatString = "%14.1f %10d\n";
        FixedBoundaryHistogram currentHistogram = (FixedBoundaryHistogram)this.currentHistogram();
        for (int i = 0; i < this.buckets.length; ++i) {
            printStream.format(Locale.US, bucketFormatString, this.buckets[i] / bucketScaling, currentHistogram.values.get(i));
        }
        printStream.write(10);
    }

    protected double[] getBuckets() {
        return this.buckets;
    }
}

