/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.Meter
 */
package io.micrometer.core.instrument.dropwizard;

import com.codahale.metrics.Meter;
import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;

public class DropwizardCounter
extends AbstractMeter
implements Counter {
    private final Meter impl;

    DropwizardCounter(Meter.Id id, Meter impl) {
        super(id);
        this.impl = impl;
    }

    @Override
    public void increment(double amount) {
        this.impl.mark((long)amount);
    }

    @Override
    public double count() {
        return this.impl.getCount();
    }
}

