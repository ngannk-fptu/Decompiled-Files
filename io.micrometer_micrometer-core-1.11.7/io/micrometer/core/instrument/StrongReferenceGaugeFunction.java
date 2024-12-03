/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import java.util.function.ToDoubleFunction;

class StrongReferenceGaugeFunction<T>
implements ToDoubleFunction<T> {
    @Nullable
    private final T obj;
    private final ToDoubleFunction<T> f;

    StrongReferenceGaugeFunction(@Nullable T obj, ToDoubleFunction<T> f) {
        this.obj = obj;
        this.f = f;
    }

    @Override
    public double applyAsDouble(T value) {
        return this.f.applyAsDouble(value);
    }
}

