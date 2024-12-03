/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.metrics;

import com.amazonaws.metrics.ServiceMetricType;
import com.amazonaws.metrics.SimpleServiceMetricType;
import com.amazonaws.metrics.ThroughputMetricType;

public class SimpleThroughputMetricType
extends SimpleServiceMetricType
implements ThroughputMetricType {
    private final ServiceMetricType byteCountMetricType;

    public SimpleThroughputMetricType(String name, String serviceName, String byteCountMetricName) {
        super(name, serviceName);
        this.byteCountMetricType = new SimpleServiceMetricType(byteCountMetricName, serviceName);
    }

    @Override
    public ServiceMetricType getByteCountMetricType() {
        return this.byteCountMetricType;
    }
}

