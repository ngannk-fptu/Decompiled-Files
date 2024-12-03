/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Gauge;

public abstract class DerivativeGauge<F, T>
implements Gauge<T> {
    private final Gauge<F> base;

    protected DerivativeGauge(Gauge<F> base) {
        this.base = base;
    }

    @Override
    public T getValue() {
        return this.transform(this.base.getValue());
    }

    protected abstract T transform(F var1);
}

