/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.HdrHistogram.DoubleHistogram
 *  org.HdrHistogram.DoubleRecorder
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.distribution.AbstractTimeWindowHistogram;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import java.io.PrintStream;
import java.util.Iterator;
import org.HdrHistogram.DoubleHistogram;
import org.HdrHistogram.DoubleRecorder;

public class TimeWindowPercentileHistogram
extends AbstractTimeWindowHistogram<DoubleRecorder, DoubleHistogram> {
    private final DoubleHistogram intervalHistogram;

    public TimeWindowPercentileHistogram(Clock clock, DistributionStatisticConfig distributionStatisticConfig, boolean supportsAggregablePercentiles) {
        super(clock, distributionStatisticConfig, DoubleRecorder.class, supportsAggregablePercentiles);
        this.intervalHistogram = new DoubleHistogram(this.percentilePrecision(distributionStatisticConfig));
        this.initRingBuffer();
    }

    @Override
    DoubleRecorder newBucket() {
        return new DoubleRecorder(this.percentilePrecision(this.distributionStatisticConfig));
    }

    @Override
    void recordDouble(DoubleRecorder bucket, double value) {
        bucket.recordValue(value);
    }

    @Override
    void recordLong(DoubleRecorder bucket, long value) {
        bucket.recordValue((double)value);
    }

    @Override
    void resetBucket(DoubleRecorder bucket) {
        bucket.reset();
    }

    DoubleHistogram newAccumulatedHistogram(DoubleRecorder[] ringBuffer) {
        return new DoubleHistogram(this.percentilePrecision(this.distributionStatisticConfig));
    }

    @Override
    void accumulate() {
        ((DoubleRecorder)this.currentHistogram()).getIntervalHistogramInto(this.intervalHistogram);
        ((DoubleHistogram)this.accumulatedHistogram()).add(this.intervalHistogram);
    }

    @Override
    void resetAccumulatedHistogram() {
        ((DoubleHistogram)this.accumulatedHistogram()).reset();
    }

    @Override
    double valueAtPercentile(double percentile) {
        return ((DoubleHistogram)this.accumulatedHistogram()).getValueAtPercentile(percentile);
    }

    @Override
    Iterator<CountAtBucket> countsAtValues(final Iterator<Double> values) {
        return new Iterator<CountAtBucket>(){
            private double cumulativeCount = 0.0;
            private double lowerBoundValue = 0.0;

            @Override
            public boolean hasNext() {
                return values.hasNext();
            }

            @Override
            public CountAtBucket next() {
                double higherBoundValue = (Double)values.next();
                double count = ((DoubleHistogram)TimeWindowPercentileHistogram.this.accumulatedHistogram()).getCountBetweenValues(this.lowerBoundValue, higherBoundValue);
                this.lowerBoundValue = ((DoubleHistogram)TimeWindowPercentileHistogram.this.accumulatedHistogram()).nextNonEquivalentValue(higherBoundValue);
                this.cumulativeCount += count;
                return new CountAtBucket(higherBoundValue, this.cumulativeCount);
            }
        };
    }

    private int percentilePrecision(DistributionStatisticConfig config) {
        return config.getPercentilePrecision() == null ? 1 : config.getPercentilePrecision();
    }

    @Override
    void outputSummary(PrintStream out, double bucketScaling) {
        ((DoubleHistogram)this.accumulatedHistogram()).outputPercentileDistribution(out, Double.valueOf(bucketScaling));
    }
}

