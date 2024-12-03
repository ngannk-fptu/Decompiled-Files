/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Metric;
import java.util.Map;

public interface MetricSet
extends Metric {
    public Map<String, Metric> getMetrics();
}

