/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.step;

import io.micrometer.core.instrument.AbstractDistributionSummary;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.Histogram;
import io.micrometer.core.instrument.distribution.TimeWindowMax;
import io.micrometer.core.instrument.step.StepMeter;
import io.micrometer.core.instrument.step.StepTuple2;
import java.util.Arrays;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

public class StepDistributionSummary
extends AbstractDistributionSummary
implements StepMeter {
    private final LongAdder count = new LongAdder();
    private final DoubleAdder total = new DoubleAdder();
    private final StepTuple2<Long, Double> countTotal;
    private final TimeWindowMax max;

    public StepDistributionSummary(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, double scale, long stepMillis, boolean supportsAggregablePercentiles) {
        this(id, clock, distributionStatisticConfig, scale, stepMillis, StepDistributionSummary.defaultHistogram(clock, distributionStatisticConfig, supportsAggregablePercentiles));
    }

    protected StepDistributionSummary(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, double scale, long stepMillis, Histogram histogram) {
        super(id, scale, histogram);
        this.countTotal = new StepTuple2<Long, Double>(clock, stepMillis, 0L, 0.0, this.count::sumThenReset, this.total::sumThenReset);
        this.max = new TimeWindowMax(clock, distributionStatisticConfig);
    }

    @Override
    protected void recordNonNegative(double amount) {
        this.count.add(1L);
        this.total.add(amount);
        this.max.record(amount);
    }

    @Override
    public long count() {
        return this.countTotal.poll1();
    }

    @Override
    public double totalAmount() {
        return this.countTotal.poll2();
    }

    @Override
    public double max() {
        return this.max.poll();
    }

    @Override
    public Iterable<Measurement> measure() {
        return Arrays.asList(new Measurement(() -> this.count(), Statistic.COUNT), new Measurement(this::totalAmount, Statistic.TOTAL), new Measurement(this::max, Statistic.MAX));
    }

    @Override
    public void _closingRollover() {
        this.countTotal._closingRollover();
    }
}

