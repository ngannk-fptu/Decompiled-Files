/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.noop;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.noop.NoopMeter;
import java.util.concurrent.TimeUnit;

public class NoopLongTaskTimer
extends NoopMeter
implements LongTaskTimer {
    public NoopLongTaskTimer(Meter.Id id) {
        super(id);
    }

    @Override
    public LongTaskTimer.Sample start() {
        return new NoopSample();
    }

    @Override
    public double duration(TimeUnit unit) {
        return 0.0;
    }

    @Override
    public int activeTasks() {
        return 0;
    }

    @Override
    public double max(TimeUnit unit) {
        return 0.0;
    }

    @Override
    public HistogramSnapshot takeSnapshot() {
        return HistogramSnapshot.empty(0L, 0.0, 0.0);
    }

    @Override
    public TimeUnit baseTimeUnit() {
        return TimeUnit.SECONDS;
    }

    static class NoopSample
    extends LongTaskTimer.Sample {
        NoopSample() {
        }

        @Override
        public long stop() {
            return 0L;
        }

        @Override
        public double duration(TimeUnit unit) {
            return 0.0;
        }
    }
}

