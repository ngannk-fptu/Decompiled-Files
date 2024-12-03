/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Metric;

@FunctionalInterface
public interface Gauge<T>
extends Metric {
    public T getValue();
}

