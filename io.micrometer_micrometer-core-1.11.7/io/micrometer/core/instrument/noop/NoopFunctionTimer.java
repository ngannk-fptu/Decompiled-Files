/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.noop;

import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.noop.NoopMeter;
import java.util.concurrent.TimeUnit;

public class NoopFunctionTimer
extends NoopMeter
implements FunctionTimer {
    public NoopFunctionTimer(Meter.Id id) {
        super(id);
    }

    @Override
    public double count() {
        return 0.0;
    }

    @Override
    public double totalTime(TimeUnit unit) {
        return 0.0;
    }

    @Override
    public TimeUnit baseTimeUnit() {
        return TimeUnit.NANOSECONDS;
    }
}

