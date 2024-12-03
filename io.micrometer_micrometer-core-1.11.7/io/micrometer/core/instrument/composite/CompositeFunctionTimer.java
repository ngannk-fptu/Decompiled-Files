/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.composite;

import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.AbstractCompositeMeter;
import io.micrometer.core.instrument.noop.NoopFunctionTimer;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

class CompositeFunctionTimer<T>
extends AbstractCompositeMeter<FunctionTimer>
implements FunctionTimer {
    private final WeakReference<T> ref;
    private final ToLongFunction<T> countFunction;
    private final ToDoubleFunction<T> totalTimeFunction;
    private final TimeUnit totalTimeFunctionUnit;

    CompositeFunctionTimer(Meter.Id id, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
        super(id);
        this.ref = new WeakReference<T>(obj);
        this.countFunction = countFunction;
        this.totalTimeFunction = totalTimeFunction;
        this.totalTimeFunctionUnit = totalTimeFunctionUnit;
    }

    @Override
    public double count() {
        return ((FunctionTimer)this.firstChild()).count();
    }

    @Override
    public double totalTime(TimeUnit unit) {
        return ((FunctionTimer)this.firstChild()).totalTime(unit);
    }

    @Override
    public TimeUnit baseTimeUnit() {
        return ((FunctionTimer)this.firstChild()).baseTimeUnit();
    }

    @Override
    FunctionTimer newNoopMeter() {
        return new NoopFunctionTimer(this.getId());
    }

    @Override
    FunctionTimer registerNewMeter(MeterRegistry registry) {
        Object obj = this.ref.get();
        if (obj == null) {
            return null;
        }
        return FunctionTimer.builder(this.getId().getName(), obj, this.countFunction, this.totalTimeFunction, this.totalTimeFunctionUnit).tags(this.getId().getTagsAsIterable()).description(this.getId().getDescription()).register(registry);
    }
}

