/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.SettableGauge;

public class DefaultSettableGauge<T>
implements SettableGauge<T> {
    private volatile T value;

    public DefaultSettableGauge() {
        this(null);
    }

    public DefaultSettableGauge(T defaultValue) {
        this.value = defaultValue;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return this.value;
    }
}

