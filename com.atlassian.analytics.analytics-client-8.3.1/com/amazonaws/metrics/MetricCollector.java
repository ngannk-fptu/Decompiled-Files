/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.metrics;

import com.amazonaws.metrics.RequestMetricCollector;
import com.amazonaws.metrics.ServiceMetricCollector;

public abstract class MetricCollector {
    public static final MetricCollector NONE = new MetricCollector(){

        @Override
        public boolean start() {
            return true;
        }

        @Override
        public boolean stop() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public RequestMetricCollector getRequestMetricCollector() {
            return RequestMetricCollector.NONE;
        }

        @Override
        public ServiceMetricCollector getServiceMetricCollector() {
            return ServiceMetricCollector.NONE;
        }
    };

    public abstract boolean start();

    public abstract boolean stop();

    public abstract boolean isEnabled();

    public abstract RequestMetricCollector getRequestMetricCollector();

    public abstract ServiceMetricCollector getServiceMetricCollector();

    public static interface Factory {
        public MetricCollector getInstance();
    }
}

