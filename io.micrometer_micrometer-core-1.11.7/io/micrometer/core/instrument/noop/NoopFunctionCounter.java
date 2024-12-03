/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.noop;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.noop.NoopMeter;

public class NoopFunctionCounter
extends NoopMeter
implements FunctionCounter {
    public NoopFunctionCounter(Meter.Id id) {
        super(id);
    }

    @Override
    public double count() {
        return 0.0;
    }
}

