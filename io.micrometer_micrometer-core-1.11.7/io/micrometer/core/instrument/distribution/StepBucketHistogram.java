/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.config.InvalidConfigurationException;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.FixedBoundaryHistogram;
import io.micrometer.core.instrument.distribution.Histogram;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.step.StepValue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.function.Supplier;

public class StepBucketHistogram
extends StepValue<CountAtBucket[]>
implements Histogram {
    private final FixedBoundaryHistogram fixedBoundaryHistogram;
    private final double[] buckets;

    public StepBucketHistogram(Clock clock, long stepMillis, DistributionStatisticConfig distributionStatisticConfig, boolean supportsAggregablePercentiles, boolean isCumulativeBucketCounts) {
        super(clock, stepMillis, StepBucketHistogram.getEmptyCounts(StepBucketHistogram.getBucketsFromDistributionStatisticConfig(distributionStatisticConfig, supportsAggregablePercentiles)));
        this.buckets = StepBucketHistogram.getBucketsFromDistributionStatisticConfig(distributionStatisticConfig, supportsAggregablePercentiles);
        this.fixedBoundaryHistogram = new FixedBoundaryHistogram(this.buckets, isCumulativeBucketCounts);
    }

    @Override
    public void recordLong(long value) {
        this.fixedBoundaryHistogram.record(value);
    }

    @Override
    public void recordDouble(double value) {
        this.recordLong((long)Math.ceil(value));
    }

    @Override
    public HistogramSnapshot takeSnapshot(long count, double total, double max) {
        return new HistogramSnapshot(count, total, max, null, (CountAtBucket[])this.poll(), null);
    }

    @Override
    protected Supplier<CountAtBucket[]> valueSupplier() {
        return () -> {
            CountAtBucket[] countAtBuckets = new CountAtBucket[this.buckets.length];
            FixedBoundaryHistogram fixedBoundaryHistogram = this.fixedBoundaryHistogram;
            synchronized (fixedBoundaryHistogram) {
                Iterator<CountAtBucket> iterator = this.fixedBoundaryHistogram.countsAtValues(Arrays.stream(this.buckets).iterator());
                for (int i = 0; i < countAtBuckets.length; ++i) {
                    countAtBuckets[i] = iterator.next();
                }
                this.fixedBoundaryHistogram.reset();
            }
            return countAtBuckets;
        };
    }

    @Override
    protected CountAtBucket[] noValue() {
        return StepBucketHistogram.getEmptyCounts(this.buckets);
    }

    private static CountAtBucket[] getEmptyCounts(double[] buckets) {
        CountAtBucket[] countAtBuckets = new CountAtBucket[buckets.length];
        for (int i = 0; i < buckets.length; ++i) {
            countAtBuckets[i] = new CountAtBucket(buckets[i], 0.0);
        }
        return countAtBuckets;
    }

    private static double[] getBucketsFromDistributionStatisticConfig(DistributionStatisticConfig distributionStatisticConfig, boolean supportsAggregablePercentiles) {
        if (distributionStatisticConfig.getMaximumExpectedValueAsDouble() == null || distributionStatisticConfig.getMinimumExpectedValueAsDouble() == null || distributionStatisticConfig.getMaximumExpectedValueAsDouble() <= 0.0 || distributionStatisticConfig.getMinimumExpectedValueAsDouble() <= 0.0) {
            throw new InvalidConfigurationException("minimumExpectedValue and maximumExpectedValue should be greater than 0.");
        }
        NavigableSet<Double> histogramBuckets = distributionStatisticConfig.getHistogramBuckets(supportsAggregablePercentiles);
        return histogramBuckets.stream().filter(Objects::nonNull).mapToDouble(Double::doubleValue).toArray();
    }
}

