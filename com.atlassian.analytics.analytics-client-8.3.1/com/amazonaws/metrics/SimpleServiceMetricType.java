/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.metrics;

import com.amazonaws.metrics.ServiceMetricType;
import com.amazonaws.metrics.SimpleMetricType;

public class SimpleServiceMetricType
extends SimpleMetricType
implements ServiceMetricType {
    private final String name;
    private final String serviceName;

    public SimpleServiceMetricType(String name, String serviceName) {
        this.name = name;
        this.serviceName = serviceName;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String getServiceName() {
        return this.serviceName;
    }
}

