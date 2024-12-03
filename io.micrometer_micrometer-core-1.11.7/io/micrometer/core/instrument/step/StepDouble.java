/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.step;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.step.StepValue;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.Supplier;

public class StepDouble
extends StepValue<Double> {
    private final DoubleAdder current = new DoubleAdder();

    public StepDouble(Clock clock, long stepMillis) {
        super(clock, stepMillis);
    }

    @Override
    protected Supplier<Double> valueSupplier() {
        return this.current::sumThenReset;
    }

    @Override
    protected Double noValue() {
        return 0.0;
    }

    public DoubleAdder getCurrent() {
        return this.current;
    }
}

