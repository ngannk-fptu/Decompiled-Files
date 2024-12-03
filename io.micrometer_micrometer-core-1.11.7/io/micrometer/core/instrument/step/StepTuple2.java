/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.step;

import io.micrometer.core.instrument.Clock;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class StepTuple2<T1, T2> {
    private final Clock clock;
    private final long stepMillis;
    private AtomicLong lastInitPos;
    private final T1 t1NoValue;
    private final T2 t2NoValue;
    private final Supplier<T1> t1Supplier;
    private final Supplier<T2> t2Supplier;
    private volatile T1 t1Previous;
    private volatile T2 t2Previous;

    public StepTuple2(Clock clock, long stepMillis, T1 t1NoValue, T2 t2NoValue, Supplier<T1> t1Supplier, Supplier<T2> t2Supplier) {
        this.clock = clock;
        this.stepMillis = stepMillis;
        this.t1NoValue = t1NoValue;
        this.t2NoValue = t2NoValue;
        this.t1Supplier = t1Supplier;
        this.t2Supplier = t2Supplier;
        this.t1Previous = t1NoValue;
        this.t2Previous = t2NoValue;
        this.lastInitPos = new AtomicLong(clock.wallTime() / stepMillis);
    }

    private void rollCount(long now) {
        long stepTime = now / this.stepMillis;
        long lastInit = this.lastInitPos.get();
        if (lastInit < stepTime && this.lastInitPos.compareAndSet(lastInit, stepTime)) {
            this.t1Previous = lastInit == stepTime - 1L ? this.t1Supplier.get() : this.t1NoValue;
            this.t2Previous = lastInit == stepTime - 1L ? this.t2Supplier.get() : this.t2NoValue;
        }
    }

    protected void _closingRollover() {
        this.lastInitPos.set(Long.MAX_VALUE);
        this.t1Previous = this.t1Supplier.get();
        this.t2Previous = this.t2Supplier.get();
    }

    public T1 poll1() {
        this.rollCount(this.clock.wallTime());
        return this.t1Previous;
    }

    public T2 poll2() {
        this.rollCount(this.clock.wallTime());
        return this.t2Previous;
    }
}

