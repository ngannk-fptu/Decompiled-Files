/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.impl;

import com.hazelcast.internal.metrics.DoubleProbeFunction;
import com.hazelcast.internal.metrics.LongGauge;
import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.ProbeFunction;
import com.hazelcast.internal.metrics.impl.AbstractGauge;
import com.hazelcast.internal.metrics.impl.MetricsRegistryImpl;
import com.hazelcast.internal.metrics.impl.ProbeInstance;

class LongGaugeImpl
extends AbstractGauge
implements LongGauge {
    private static final long DEFAULT_VALUE = 0L;

    LongGaugeImpl(MetricsRegistryImpl metricsRegistry, String name) {
        super(metricsRegistry, name);
    }

    @Override
    public long read() {
        ProbeInstance probeInstance = this.getProbeInstance();
        ProbeFunction function = null;
        Object source = null;
        if (probeInstance != null) {
            function = probeInstance.function;
            source = probeInstance.source;
        }
        if (function == null || source == null) {
            this.clearProbeInstance();
            return 0L;
        }
        try {
            if (function instanceof LongProbeFunction) {
                LongProbeFunction longFunction = (LongProbeFunction)function;
                return longFunction.get(source);
            }
            DoubleProbeFunction doubleFunction = (DoubleProbeFunction)function;
            double doubleResult = doubleFunction.get(source);
            return Math.round(doubleResult);
        }
        catch (Exception e) {
            this.metricsRegistry.logger.warning("Failed to access the probe: " + this.name, e);
            return 0L;
        }
    }

    @Override
    public void render(StringBuilder stringBuilder) {
        stringBuilder.append(this.read());
    }
}

