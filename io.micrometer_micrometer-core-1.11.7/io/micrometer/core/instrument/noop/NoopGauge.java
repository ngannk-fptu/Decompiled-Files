/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.noop;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.noop.NoopMeter;

public class NoopGauge
extends NoopMeter
implements Gauge {
    public NoopGauge(Meter.Id id) {
        super(id);
    }

    @Override
    public double value() {
        return 0.0;
    }
}

