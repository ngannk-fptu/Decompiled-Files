/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.HistogramSupport;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import io.micrometer.core.instrument.util.DoubleFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

@Incubating(since="1.0.3")
public class HistogramGauges {
    volatile CountDownLatch polledGaugesLatch;
    private volatile HistogramSnapshot snapshot;
    private final HistogramSupport meter;
    private final int totalGauges;

    public static HistogramGauges registerWithCommonFormat(Timer timer, MeterRegistry registry) {
        return HistogramGauges.getHistogramGauges(timer, timer.getId(), timer.baseTimeUnit(), registry);
    }

    private static HistogramGauges getHistogramGauges(HistogramSupport histogramSupport, Meter.Id id, TimeUnit baseTimeUnit, MeterRegistry registry) {
        return HistogramGauges.register(histogramSupport, registry, percentile -> id.getName() + ".percentile", percentile -> Tags.concat(id.getTagsAsIterable(), "phi", DoubleFormat.decimalOrNan(percentile.percentile())), percentile -> percentile.value(baseTimeUnit), bucket -> id.getName() + ".histogram", bucket -> Tags.concat(id.getTagsAsIterable(), "le", bucket.isPositiveInf() ? "+Inf" : DoubleFormat.wholeOrDecimal(bucket.bucket(baseTimeUnit))));
    }

    public static HistogramGauges registerWithCommonFormat(LongTaskTimer ltt, MeterRegistry registry) {
        return HistogramGauges.getHistogramGauges(ltt, ltt.getId(), ltt.baseTimeUnit(), registry);
    }

    public static HistogramGauges registerWithCommonFormat(DistributionSummary summary, MeterRegistry registry) {
        Meter.Id id = summary.getId();
        return HistogramGauges.register(summary, registry, percentile -> id.getName() + ".percentile", percentile -> Tags.concat(id.getTagsAsIterable(), "phi", DoubleFormat.decimalOrNan(percentile.percentile())), ValueAtPercentile::value, bucket -> id.getName() + ".histogram", bucket -> Tags.concat(id.getTagsAsIterable(), "le", bucket.isPositiveInf() ? "+Inf" : DoubleFormat.wholeOrDecimal(bucket.bucket())));
    }

    public static HistogramGauges register(HistogramSupport meter, MeterRegistry registry, Function<ValueAtPercentile, String> percentileName, Function<ValueAtPercentile, Iterable<Tag>> percentileTags, Function<ValueAtPercentile, Double> percentileValue, Function<CountAtBucket, String> bucketName, Function<CountAtBucket, Iterable<Tag>> bucketTags) {
        return new HistogramGauges(meter, registry, percentileName, percentileTags, percentileValue, bucketName, bucketTags);
    }

    private HistogramGauges(HistogramSupport meter, MeterRegistry registry, Function<ValueAtPercentile, String> percentileName, Function<ValueAtPercentile, Iterable<Tag>> percentileTags, Function<ValueAtPercentile, Double> percentileValue, Function<CountAtBucket, String> bucketName, Function<CountAtBucket, Iterable<Tag>> bucketTags) {
        int index;
        int i;
        HistogramSnapshot initialSnapshot;
        this.meter = meter;
        this.snapshot = initialSnapshot = meter.takeSnapshot();
        ValueAtPercentile[] valueAtPercentiles = initialSnapshot.percentileValues();
        CountAtBucket[] countAtBuckets = initialSnapshot.histogramCounts();
        this.totalGauges = valueAtPercentiles.length + countAtBuckets.length;
        this.polledGaugesLatch = new CountDownLatch(0);
        for (i = 0; i < valueAtPercentiles.length; ++i) {
            index = i;
            ToDoubleFunction<HistogramSupport> percentileValueFunction = m -> {
                this.snapshotIfNecessary();
                this.polledGaugesLatch.countDown();
                return (Double)percentileValue.apply(this.snapshot.percentileValues()[index]);
            };
            Gauge.builder(percentileName.apply(valueAtPercentiles[i]), meter, percentileValueFunction).tags(percentileTags.apply(valueAtPercentiles[i])).baseUnit(meter.getId().getBaseUnit()).synthetic(meter.getId()).register(registry);
        }
        for (i = 0; i < countAtBuckets.length; ++i) {
            index = i;
            ToDoubleFunction<HistogramSupport> bucketCountFunction = m -> {
                this.snapshotIfNecessary();
                this.polledGaugesLatch.countDown();
                return this.snapshot.histogramCounts()[index].count();
            };
            Gauge.builder(bucketName.apply(countAtBuckets[i]), meter, bucketCountFunction).tags(bucketTags.apply(countAtBuckets[i])).synthetic(meter.getId()).register(registry);
        }
    }

    private void snapshotIfNecessary() {
        if (this.polledGaugesLatch.getCount() == 0L) {
            this.snapshot = this.meter.takeSnapshot();
            this.polledGaugesLatch = new CountDownLatch(this.totalGauges);
        }
    }
}

