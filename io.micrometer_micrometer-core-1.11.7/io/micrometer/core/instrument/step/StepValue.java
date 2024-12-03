/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.step;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Clock;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public abstract class StepValue<V> {
    private final Clock clock;
    private final long stepMillis;
    private final AtomicLong lastInitPos;
    private volatile V previous;

    public StepValue(Clock clock, long stepMillis) {
        this(clock, stepMillis, null);
    }

    protected StepValue(Clock clock, long stepMillis, @Nullable V initValue) {
        this.clock = clock;
        this.stepMillis = stepMillis;
        this.previous = initValue == null ? this.noValue() : initValue;
        this.lastInitPos = new AtomicLong(clock.wallTime() / stepMillis);
    }

    protected abstract Supplier<V> valueSupplier();

    protected abstract V noValue();

    private void rollCount(long now) {
        long stepTime = now / this.stepMillis;
        long lastInit = this.lastInitPos.get();
        if (lastInit < stepTime && this.lastInitPos.compareAndSet(lastInit, stepTime)) {
            V v = this.valueSupplier().get();
            this.previous = lastInit == stepTime - 1L ? v : this.noValue();
        }
    }

    public V poll() {
        this.rollCount(this.clock.wallTime());
        return this.previous;
    }

    protected void _closingRollover() {
        this.lastInitPos.set(Long.MAX_VALUE);
        this.previous = this.valueSupplier().get();
    }
}

