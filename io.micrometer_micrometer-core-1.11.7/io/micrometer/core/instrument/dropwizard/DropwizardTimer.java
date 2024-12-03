/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.Timer
 */
package io.micrometer.core.instrument.dropwizard;

import com.codahale.metrics.Timer;
import io.micrometer.core.instrument.AbstractTimer;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.TimeWindowMax;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.util.TimeUtils;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class DropwizardTimer
extends AbstractTimer {
    private final Timer impl;
    private final AtomicLong totalTime = new AtomicLong();
    private final TimeWindowMax max;

    DropwizardTimer(Meter.Id id, Timer impl, Clock clock, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        super(id, clock, distributionStatisticConfig, pauseDetector, TimeUnit.MILLISECONDS, false);
        this.impl = impl;
        this.max = new TimeWindowMax(clock, distributionStatisticConfig);
    }

    @Override
    protected void recordNonNegative(long amount, TimeUnit unit) {
        if (amount >= 0L) {
            this.impl.update(amount, unit);
            long nanoAmount = TimeUnit.NANOSECONDS.convert(amount, unit);
            this.max.record(nanoAmount, TimeUnit.NANOSECONDS);
            this.totalTime.addAndGet(nanoAmount);
        }
    }

    @Override
    public long count() {
        return this.impl.getCount();
    }

    @Override
    public double totalTime(TimeUnit unit) {
        return TimeUtils.nanosToUnit(this.totalTime.get(), unit);
    }

    @Override
    public double max(TimeUnit unit) {
        return this.max.poll(unit);
    }
}

