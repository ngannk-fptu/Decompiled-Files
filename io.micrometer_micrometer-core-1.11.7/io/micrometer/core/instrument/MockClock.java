/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.util.TimeUtils;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class MockClock
implements Clock {
    private long timeNanos = (long)TimeUtils.millisToUnit(1.0, TimeUnit.NANOSECONDS);

    @Override
    public long monotonicTime() {
        return this.timeNanos;
    }

    @Override
    public long wallTime() {
        return TimeUnit.MILLISECONDS.convert(this.timeNanos, TimeUnit.NANOSECONDS);
    }

    public long add(long amount, TimeUnit unit) {
        this.timeNanos += unit.toNanos(amount);
        return this.timeNanos;
    }

    public long add(Duration duration) {
        return this.add(duration.toNanos(), TimeUnit.NANOSECONDS);
    }

    public long addSeconds(long amount) {
        return this.add(amount, TimeUnit.SECONDS);
    }

    public static MockClock clock(MeterRegistry registry) {
        return (MockClock)registry.config().clock();
    }
}

