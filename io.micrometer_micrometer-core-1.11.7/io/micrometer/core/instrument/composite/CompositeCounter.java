/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.composite;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.AbstractCompositeMeter;
import io.micrometer.core.instrument.noop.NoopCounter;

class CompositeCounter
extends AbstractCompositeMeter<Counter>
implements Counter {
    CompositeCounter(Meter.Id id) {
        super(id);
    }

    @Override
    public void increment(double amount) {
        for (Counter c : this.getChildren()) {
            c.increment(amount);
        }
    }

    @Override
    public double count() {
        return ((Counter)this.firstChild()).count();
    }

    @Override
    Counter newNoopMeter() {
        return new NoopCounter(this.getId());
    }

    @Override
    Counter registerNewMeter(MeterRegistry registry) {
        return Counter.builder(this.getId().getName()).tags(this.getId().getTagsAsIterable()).description(this.getId().getDescription()).baseUnit(this.getId().getBaseUnit()).register(registry);
    }
}

