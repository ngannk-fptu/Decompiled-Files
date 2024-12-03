/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.cumulative;

import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import java.util.concurrent.atomic.DoubleAdder;

public class CumulativeCounter
extends AbstractMeter
implements Counter {
    private final DoubleAdder value = new DoubleAdder();

    public CumulativeCounter(Meter.Id id) {
        super(id);
    }

    @Override
    public void increment(double amount) {
        this.value.add(amount);
    }

    @Override
    public double count() {
        return this.value.sum();
    }
}

