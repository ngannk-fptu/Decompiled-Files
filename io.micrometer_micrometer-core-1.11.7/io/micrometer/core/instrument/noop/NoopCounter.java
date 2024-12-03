/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.noop;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.noop.NoopMeter;

public class NoopCounter
extends NoopMeter
implements Counter {
    public NoopCounter(Meter.Id id) {
        super(id);
    }

    @Override
    public void increment(double amount) {
    }

    @Override
    public double count() {
        return 0.0;
    }
}

