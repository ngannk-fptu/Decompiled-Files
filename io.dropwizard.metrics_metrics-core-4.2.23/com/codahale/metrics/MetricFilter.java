/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Metric;

public interface MetricFilter {
    public static final MetricFilter ALL = (name, metric) -> true;

    public static MetricFilter startsWith(String prefix) {
        return (name, metric) -> name.startsWith(prefix);
    }

    public static MetricFilter endsWith(String suffix) {
        return (name, metric) -> name.endsWith(suffix);
    }

    public static MetricFilter contains(String substring) {
        return (name, metric) -> name.contains(substring);
    }

    public boolean matches(String var1, Metric var2);
}

