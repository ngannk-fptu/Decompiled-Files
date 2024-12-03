/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.cumulative;

import io.micrometer.core.instrument.AbstractTimer;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.Histogram;
import io.micrometer.core.instrument.distribution.TimeWindowMax;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.util.TimeUtils;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class CumulativeTimer
extends AbstractTimer {
    private final AtomicLong count = new AtomicLong();
    private final AtomicLong total = new AtomicLong();
    private final TimeWindowMax max;

    public CumulativeTimer(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector, TimeUnit baseTimeUnit) {
        this(id, clock, distributionStatisticConfig, pauseDetector, baseTimeUnit, false);
    }

    public CumulativeTimer(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector, TimeUnit baseTimeUnit, boolean supportsAggregablePercentiles) {
        this(id, clock, distributionStatisticConfig, pauseDetector, baseTimeUnit, AbstractTimer.defaultHistogram(clock, distributionStatisticConfig, supportsAggregablePercentiles));
    }

    protected CumulativeTimer(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector, TimeUnit baseTimeUnit, Histogram histogram) {
        super(id, clock, pauseDetector, baseTimeUnit, histogram);
        this.max = new TimeWindowMax(clock, distributionStatisticConfig);
    }

    @Override
    protected void recordNonNegative(long amount, TimeUnit unit) {
        long nanoAmount = (long)TimeUtils.convert(amount, unit, TimeUnit.NANOSECONDS);
        this.count.getAndAdd(1L);
        this.total.getAndAdd(nanoAmount);
        this.max.record(nanoAmount, TimeUnit.NANOSECONDS);
    }

    @Override
    public long count() {
        return this.count.get();
    }

    @Override
    public double totalTime(TimeUnit unit) {
        return TimeUtils.nanosToUnit(this.total.get(), unit);
    }

    @Override
    public double max(TimeUnit unit) {
        return this.max.poll(unit);
    }
}

