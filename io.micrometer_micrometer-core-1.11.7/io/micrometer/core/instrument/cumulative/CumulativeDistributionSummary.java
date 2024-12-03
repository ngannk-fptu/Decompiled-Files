/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.cumulative;

import io.micrometer.core.instrument.AbstractDistributionSummary;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.Histogram;
import io.micrometer.core.instrument.distribution.TimeWindowMax;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

public class CumulativeDistributionSummary
extends AbstractDistributionSummary {
    private final AtomicLong count = new AtomicLong();
    private final DoubleAdder total = new DoubleAdder();
    private final TimeWindowMax max;

    @Deprecated
    public CumulativeDistributionSummary(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        this(id, clock, distributionStatisticConfig, scale, false);
    }

    public CumulativeDistributionSummary(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, double scale, boolean supportsAggregablePercentiles) {
        this(id, clock, distributionStatisticConfig, scale, AbstractDistributionSummary.defaultHistogram(clock, distributionStatisticConfig, supportsAggregablePercentiles));
    }

    protected CumulativeDistributionSummary(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, double scale, Histogram histogram) {
        super(id, scale, histogram);
        this.max = new TimeWindowMax(clock, distributionStatisticConfig);
    }

    @Override
    protected void recordNonNegative(double amount) {
        this.count.incrementAndGet();
        this.total.add(amount);
        this.max.record(amount);
    }

    @Override
    public long count() {
        return this.count.get();
    }

    @Override
    public double totalAmount() {
        return this.total.sum();
    }

    @Override
    public double max() {
        return this.max.poll();
    }

    @Override
    public Iterable<Measurement> measure() {
        return Arrays.asList(new Measurement(() -> this.count(), Statistic.COUNT), new Measurement(this::totalAmount, Statistic.TOTAL), new Measurement(this::max, Statistic.MAX));
    }
}

