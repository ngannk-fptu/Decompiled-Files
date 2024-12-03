/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.impl;

import com.hazelcast.internal.metrics.Metric;
import com.hazelcast.internal.metrics.impl.MetricsRegistryImpl;
import com.hazelcast.internal.metrics.impl.ProbeInstance;

abstract class AbstractGauge
implements Metric {
    protected final MetricsRegistryImpl metricsRegistry;
    protected final String name;
    private volatile ProbeInstance probeInstance;

    AbstractGauge(MetricsRegistryImpl metricsRegistry, String name) {
        this.metricsRegistry = metricsRegistry;
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    void clearProbeInstance() {
        this.probeInstance = null;
    }

    ProbeInstance getProbeInstance() {
        ProbeInstance probeInstance = this.probeInstance;
        if (probeInstance == null) {
            this.probeInstance = probeInstance = this.metricsRegistry.getProbeInstance(this.name);
        }
        return probeInstance;
    }
}

