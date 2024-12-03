/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Clock;
import com.codahale.metrics.EWMA;
import com.codahale.metrics.MovingAverages;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ExponentialMovingAverages
implements MovingAverages {
    private static final long TICK_INTERVAL = TimeUnit.SECONDS.toNanos(5L);
    private final EWMA m1Rate = EWMA.oneMinuteEWMA();
    private final EWMA m5Rate = EWMA.fiveMinuteEWMA();
    private final EWMA m15Rate = EWMA.fifteenMinuteEWMA();
    private final AtomicLong lastTick;
    private final Clock clock;

    public ExponentialMovingAverages() {
        this(Clock.defaultClock());
    }

    public ExponentialMovingAverages(Clock clock) {
        this.clock = clock;
        this.lastTick = new AtomicLong(this.clock.getTick());
    }

    @Override
    public void update(long n) {
        this.m1Rate.update(n);
        this.m5Rate.update(n);
        this.m15Rate.update(n);
    }

    @Override
    public void tickIfNecessary() {
        long newIntervalStartTick;
        long oldTick = this.lastTick.get();
        long newTick = this.clock.getTick();
        long age = newTick - oldTick;
        if (age > TICK_INTERVAL && this.lastTick.compareAndSet(oldTick, newIntervalStartTick = newTick - age % TICK_INTERVAL)) {
            long requiredTicks = age / TICK_INTERVAL;
            for (long i = 0L; i < requiredTicks; ++i) {
                this.m1Rate.tick();
                this.m5Rate.tick();
                this.m15Rate.tick();
            }
        }
    }

    @Override
    public double getM1Rate() {
        return this.m1Rate.getRate(TimeUnit.SECONDS);
    }

    @Override
    public double getM5Rate() {
        return this.m5Rate.getRate(TimeUnit.SECONDS);
    }

    @Override
    public double getM15Rate() {
        return this.m15Rate.getRate(TimeUnit.SECONDS);
    }
}

