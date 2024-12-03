/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.impl;

import com.hazelcast.internal.metrics.DoubleGauge;
import com.hazelcast.internal.metrics.DoubleProbeFunction;
import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.ProbeFunction;
import com.hazelcast.internal.metrics.impl.AbstractGauge;
import com.hazelcast.internal.metrics.impl.MetricsRegistryImpl;
import com.hazelcast.internal.metrics.impl.ProbeInstance;

class DoubleGaugeImpl
extends AbstractGauge
implements DoubleGauge {
    private static final double DEFAULT_VALUE = 0.0;

    DoubleGaugeImpl(MetricsRegistryImpl metricsRegistry, String name) {
        super(metricsRegistry, name);
    }

    @Override
    public double read() {
        ProbeInstance probeInstance = this.getProbeInstance();
        ProbeFunction function = null;
        Object source = null;
        if (probeInstance != null) {
            function = probeInstance.function;
            source = probeInstance.source;
        }
        if (function == null || source == null) {
            this.clearProbeInstance();
            return 0.0;
        }
        try {
            if (function instanceof LongProbeFunction) {
                LongProbeFunction longFunction = (LongProbeFunction)function;
                return longFunction.get(source);
            }
            DoubleProbeFunction doubleFunction = (DoubleProbeFunction)function;
            return doubleFunction.get(source);
        }
        catch (Exception e) {
            this.metricsRegistry.logger.warning("Failed to access the probe: " + this.name, e);
            return 0.0;
        }
    }

    @Override
    public void render(StringBuilder stringBuilder) {
        stringBuilder.append(this.read());
    }
}

