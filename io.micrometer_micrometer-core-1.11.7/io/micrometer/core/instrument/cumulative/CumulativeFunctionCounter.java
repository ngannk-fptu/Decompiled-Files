/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.cumulative;

import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Meter;
import java.lang.ref.WeakReference;
import java.util.function.ToDoubleFunction;

public class CumulativeFunctionCounter<T>
extends AbstractMeter
implements FunctionCounter {
    private final WeakReference<T> ref;
    private final ToDoubleFunction<T> f;
    private volatile double last;

    public CumulativeFunctionCounter(Meter.Id id, T obj, ToDoubleFunction<T> f) {
        super(id);
        this.ref = new WeakReference<T>(obj);
        this.f = f;
    }

    @Override
    public double count() {
        Object obj2 = this.ref.get();
        return obj2 != null ? (this.last = this.f.applyAsDouble(obj2)) : this.last;
    }
}

