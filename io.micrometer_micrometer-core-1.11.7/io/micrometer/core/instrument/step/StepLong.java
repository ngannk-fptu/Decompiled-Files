/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.step;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.step.StepValue;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

public class StepLong
extends StepValue<Long> {
    private final LongAdder current = new LongAdder();

    public StepLong(Clock clock, long stepMillis) {
        super(clock, stepMillis);
    }

    @Override
    protected Supplier<Long> valueSupplier() {
        return this.current::sumThenReset;
    }

    @Override
    protected Long noValue() {
        return 0L;
    }

    public LongAdder getCurrent() {
        return this.current;
    }
}

