/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.composite;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeter;
import io.micrometer.core.instrument.internal.DefaultMeter;

class CompositeCustomMeter
extends DefaultMeter
implements CompositeMeter {
    CompositeCustomMeter(Meter.Id id, Meter.Type type, Iterable<Measurement> measurements) {
        super(id, type, measurements);
    }

    @Override
    public void add(MeterRegistry registry) {
        Meter.builder(this.getId().getName(), this.getType(), this.measure()).tags(this.getId().getTagsAsIterable()).description(this.getId().getDescription()).baseUnit(this.getId().getBaseUnit()).register(registry);
    }

    @Override
    public void remove(MeterRegistry registry) {
    }
}

