/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.step;

import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.step.StepDouble;
import io.micrometer.core.instrument.step.StepMeter;

public class StepCounter
extends AbstractMeter
implements Counter,
StepMeter {
    private final StepDouble value;

    public StepCounter(Meter.Id id, Clock clock, long stepMillis) {
        super(id);
        this.value = new StepDouble(clock, stepMillis);
    }

    @Override
    public void increment(double amount) {
        this.value.getCurrent().add(amount);
    }

    @Override
    public double count() {
        return (Double)this.value.poll();
    }

    @Override
    public void _closingRollover() {
        this.value._closingRollover();
    }
}

