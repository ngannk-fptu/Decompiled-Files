/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Gauge;

public interface SettableGauge<T>
extends Gauge<T> {
    public void setValue(T var1);
}

