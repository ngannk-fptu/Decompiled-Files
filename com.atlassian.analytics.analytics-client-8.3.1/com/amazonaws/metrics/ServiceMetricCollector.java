/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.metrics;

import com.amazonaws.metrics.ByteThroughputProvider;
import com.amazonaws.metrics.ServiceLatencyProvider;

public abstract class ServiceMetricCollector {
    public static final ServiceMetricCollector NONE = new ServiceMetricCollector(){

        @Override
        public void collectByteThroughput(ByteThroughputProvider provider) {
        }

        @Override
        public void collectLatency(ServiceLatencyProvider provider) {
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    };

    public abstract void collectByteThroughput(ByteThroughputProvider var1);

    public abstract void collectLatency(ServiceLatencyProvider var1);

    public boolean isEnabled() {
        return true;
    }

    public static interface Factory {
        public ServiceMetricCollector getServiceMetricCollector();
    }
}

