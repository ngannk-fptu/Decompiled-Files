/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.metrics;

import com.amazonaws.metrics.MetricType;

public abstract class SimpleMetricType
implements MetricType {
    @Override
    public abstract String name();

    public final int hashCode() {
        return this.name().hashCode();
    }

    public final boolean equals(Object o) {
        if (!(o instanceof MetricType)) {
            return false;
        }
        MetricType that = (MetricType)o;
        return this.name().equals(that.name());
    }

    public final String toString() {
        return this.name();
    }
}

