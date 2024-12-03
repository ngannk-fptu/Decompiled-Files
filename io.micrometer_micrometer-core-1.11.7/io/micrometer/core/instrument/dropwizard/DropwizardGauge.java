/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.Gauge
 */
package io.micrometer.core.instrument.dropwizard;

import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;

public class DropwizardGauge
extends AbstractMeter
implements Gauge {
    private final com.codahale.metrics.Gauge<Double> impl;

    DropwizardGauge(Meter.Id id, com.codahale.metrics.Gauge<Double> impl) {
        super(id);
        this.impl = impl;
    }

    @Override
    public double value() {
        Double value = (Double)this.impl.getValue();
        return value == null ? Double.NaN : value;
    }
}

