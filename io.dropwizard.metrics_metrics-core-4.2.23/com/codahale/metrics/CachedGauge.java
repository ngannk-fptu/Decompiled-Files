/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Gauge;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public abstract class CachedGauge<T>
implements Gauge<T> {
    private final Clock clock;
    private final AtomicLong reloadAt;
    private final long timeoutNS;
    private final AtomicReference<T> value;

    protected CachedGauge(long timeout, TimeUnit timeoutUnit) {
        this(Clock.defaultClock(), timeout, timeoutUnit);
    }

    protected CachedGauge(Clock clock, long timeout, TimeUnit timeoutUnit) {
        this.clock = clock;
        this.reloadAt = new AtomicLong(clock.getTick());
        this.timeoutNS = timeoutUnit.toNanos(timeout);
        this.value = new AtomicReference();
    }

    protected abstract T loadValue();

    @Override
    public T getValue() {
        T currentValue = this.value.get();
        if (this.shouldLoad() || currentValue == null) {
            T newValue = this.loadValue();
            if (!this.value.compareAndSet(currentValue, newValue)) {
                return this.value.get();
            }
            return newValue;
        }
        return currentValue;
    }

    private boolean shouldLoad() {
        long time;
        long current;
        do {
            time = this.clock.getTick();
            current = this.reloadAt.get();
            if (current <= time) continue;
            return false;
        } while (!this.reloadAt.compareAndSet(current, time + this.timeoutNS));
        return true;
    }
}

