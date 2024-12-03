/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.internal;

import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;

public class DefaultMeter
extends AbstractMeter {
    private final Meter.Type type;
    private final Iterable<Measurement> measurements;

    public DefaultMeter(Meter.Id id, Meter.Type type, Iterable<Measurement> measurements) {
        super(id);
        this.type = type;
        this.measurements = measurements;
    }

    @Override
    public Iterable<Measurement> measure() {
        return this.measurements;
    }

    public Meter.Type getType() {
        return this.type;
    }
}

