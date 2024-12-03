/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.composite;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.AbstractCompositeMeter;
import io.micrometer.core.instrument.noop.NoopFunctionCounter;
import java.lang.ref.WeakReference;
import java.util.function.ToDoubleFunction;

public class CompositeFunctionCounter<T>
extends AbstractCompositeMeter<FunctionCounter>
implements FunctionCounter {
    private final WeakReference<T> ref;
    private final ToDoubleFunction<T> f;

    CompositeFunctionCounter(Meter.Id id, T obj, ToDoubleFunction<T> f) {
        super(id);
        this.ref = new WeakReference<T>(obj);
        this.f = f;
    }

    @Override
    public double count() {
        Object value = this.ref.get();
        return value != null ? this.f.applyAsDouble(value) : 0.0;
    }

    @Override
    FunctionCounter newNoopMeter() {
        return new NoopFunctionCounter(this.getId());
    }

    @Override
    FunctionCounter registerNewMeter(MeterRegistry registry) {
        Object obj = this.ref.get();
        if (obj == null) {
            return null;
        }
        return FunctionCounter.builder(this.getId().getName(), obj, this.f).tags(this.getId().getTagsAsIterable()).description(this.getId().getDescription()).baseUnit(this.getId().getBaseUnit()).register(registry);
    }
}

