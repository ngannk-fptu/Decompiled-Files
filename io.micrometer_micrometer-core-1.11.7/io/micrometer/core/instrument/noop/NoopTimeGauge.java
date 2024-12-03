/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.noop;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.noop.NoopMeter;
import java.util.concurrent.TimeUnit;

public class NoopTimeGauge
extends NoopMeter
implements TimeGauge {
    public NoopTimeGauge(Meter.Id id) {
        super(id);
    }

    @Override
    public TimeUnit baseTimeUnit() {
        return TimeUnit.NANOSECONDS;
    }

    @Override
    public double value() {
        return 0.0;
    }
}

