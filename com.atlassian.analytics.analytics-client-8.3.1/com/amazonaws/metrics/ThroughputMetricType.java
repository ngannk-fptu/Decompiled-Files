/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.metrics;

import com.amazonaws.metrics.ServiceMetricType;

public interface ThroughputMetricType
extends ServiceMetricType {
    public ServiceMetricType getByteCountMetricType();
}

