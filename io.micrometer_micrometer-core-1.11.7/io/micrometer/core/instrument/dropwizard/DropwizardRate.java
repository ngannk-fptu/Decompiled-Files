/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.EWMA
 */
package io.micrometer.core.instrument.dropwizard;

import com.codahale.metrics.EWMA;
import io.micrometer.core.instrument.Clock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

class DropwizardRate {
    private static final long TICK_INTERVAL = TimeUnit.SECONDS.toNanos(5L);
    private final AtomicLong lastTime;
    private final EWMA m1Rate = EWMA.oneMinuteEWMA();
    private final EWMA m5Rate = EWMA.fiveMinuteEWMA();
    private final EWMA m15Rate = EWMA.fifteenMinuteEWMA();
    private final Clock clock;

    DropwizardRate(Clock clock) {
        this.clock = clock;
        this.lastTime = new AtomicLong(clock.monotonicTime());
    }

    private synchronized void tickIfNecessary(long increment) {
        long oldTime = this.lastTime.get();
        long currentTime = this.clock.monotonicTime();
        long age = currentTime - oldTime;
        if (age > TICK_INTERVAL) {
            long newIntervalStartTick = currentTime - age % TICK_INTERVAL;
            if (this.lastTime.compareAndSet(oldTime, newIntervalStartTick)) {
                long requiredTicks = age / TICK_INTERVAL;
                long updateAtEachInterval = increment / requiredTicks;
                for (long i = 0L; i < requiredTicks; ++i) {
                    this.m1Rate.update(updateAtEachInterval);
                    this.m5Rate.update(updateAtEachInterval);
                    this.m15Rate.update(updateAtEachInterval);
                    this.m1Rate.tick();
                    this.m5Rate.tick();
                    this.m15Rate.tick();
                }
                long updateRemainder = increment % requiredTicks;
                this.m1Rate.update(updateRemainder);
                this.m5Rate.update(updateRemainder);
                this.m15Rate.update(updateRemainder);
            }
        } else {
            this.m1Rate.update(increment);
            this.m5Rate.update(increment);
            this.m15Rate.update(increment);
        }
    }

    public void increment(long n) {
        this.tickIfNecessary(n);
    }

    public double getOneMinuteRate() {
        this.tickIfNecessary(0L);
        return this.m1Rate.getRate(TimeUnit.SECONDS);
    }

    public double getFifteenMinuteRate() {
        this.tickIfNecessary(0L);
        return this.m15Rate.getRate(TimeUnit.SECONDS);
    }

    public double getFiveMinuteRate() {
        this.tickIfNecessary(0L);
        return this.m5Rate.getRate(TimeUnit.SECONDS);
    }
}

