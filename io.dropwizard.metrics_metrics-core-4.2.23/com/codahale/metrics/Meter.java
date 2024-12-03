/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Clock;
import com.codahale.metrics.ExponentialMovingAverages;
import com.codahale.metrics.Metered;
import com.codahale.metrics.MovingAverages;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class Meter
implements Metered {
    private final MovingAverages movingAverages;
    private final LongAdder count = new LongAdder();
    private final long startTime;
    private final Clock clock;

    public Meter(MovingAverages movingAverages) {
        this(movingAverages, Clock.defaultClock());
    }

    public Meter() {
        this(Clock.defaultClock());
    }

    public Meter(Clock clock) {
        this(new ExponentialMovingAverages(clock), clock);
    }

    public Meter(MovingAverages movingAverages, Clock clock) {
        this.movingAverages = movingAverages;
        this.clock = clock;
        this.startTime = this.clock.getTick();
    }

    public void mark() {
        this.mark(1L);
    }

    public void mark(long n) {
        this.movingAverages.tickIfNecessary();
        this.count.add(n);
        this.movingAverages.update(n);
    }

    @Override
    public long getCount() {
        return this.count.sum();
    }

    @Override
    public double getFifteenMinuteRate() {
        this.movingAverages.tickIfNecessary();
        return this.movingAverages.getM15Rate();
    }

    @Override
    public double getFiveMinuteRate() {
        this.movingAverages.tickIfNecessary();
        return this.movingAverages.getM5Rate();
    }

    @Override
    public double getMeanRate() {
        if (this.getCount() == 0L) {
            return 0.0;
        }
        double elapsed = this.clock.getTick() - this.startTime;
        return (double)this.getCount() / elapsed * (double)TimeUnit.SECONDS.toNanos(1L);
    }

    @Override
    public double getOneMinuteRate() {
        this.movingAverages.tickIfNecessary();
        return this.movingAverages.getM1Rate();
    }
}

