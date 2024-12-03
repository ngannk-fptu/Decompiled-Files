/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.step;

import io.micrometer.core.instrument.AbstractTimer;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.Histogram;
import io.micrometer.core.instrument.distribution.TimeWindowMax;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.step.StepMeter;
import io.micrometer.core.instrument.step.StepTuple2;
import io.micrometer.core.instrument.util.TimeUtils;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class StepTimer
extends AbstractTimer
implements StepMeter {
    private final LongAdder count = new LongAdder();
    private final LongAdder total = new LongAdder();
    private final StepTuple2<Long, Long> countTotal;
    private final TimeWindowMax max;

    public StepTimer(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector, TimeUnit baseTimeUnit, long stepDurationMillis, boolean supportsAggregablePercentiles) {
        this(id, clock, distributionStatisticConfig, pauseDetector, baseTimeUnit, stepDurationMillis, StepTimer.defaultHistogram(clock, distributionStatisticConfig, supportsAggregablePercentiles));
    }

    protected StepTimer(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector, TimeUnit baseTimeUnit, long stepDurationMillis, Histogram histogram) {
        super(id, clock, pauseDetector, baseTimeUnit, histogram);
        this.countTotal = new StepTuple2<Long, Long>(clock, stepDurationMillis, 0L, 0L, this.count::sumThenReset, this.total::sumThenReset);
        this.max = new TimeWindowMax(clock, distributionStatisticConfig);
    }

    @Override
    protected void recordNonNegative(long amount, TimeUnit unit) {
        long nanoAmount = (long)TimeUtils.convert(amount, unit, TimeUnit.NANOSECONDS);
        this.count.add(1L);
        this.total.add(nanoAmount);
        this.max.record((double)nanoAmount);
    }

    @Override
    public long count() {
        return this.countTotal.poll1();
    }

    @Override
    public double totalTime(TimeUnit unit) {
        return TimeUtils.nanosToUnit(this.countTotal.poll2().longValue(), unit);
    }

    @Override
    public double max(TimeUnit unit) {
        return TimeUtils.nanosToUnit(this.max.poll(), unit);
    }

    @Override
    public void _closingRollover() {
        this.countTotal._closingRollover();
    }
}

