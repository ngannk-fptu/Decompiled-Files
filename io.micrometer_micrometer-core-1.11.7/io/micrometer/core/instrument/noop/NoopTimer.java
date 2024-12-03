/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.noop;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.noop.NoopMeter;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class NoopTimer
extends NoopMeter
implements Timer {
    public NoopTimer(Meter.Id id) {
        super(id);
    }

    @Override
    public void record(long amount, TimeUnit unit) {
    }

    @Override
    public <T> T record(Supplier<T> f) {
        return f.get();
    }

    @Override
    public boolean record(BooleanSupplier f) {
        return f.getAsBoolean();
    }

    @Override
    public int record(IntSupplier f) {
        return f.getAsInt();
    }

    @Override
    public long record(LongSupplier f) {
        return f.getAsLong();
    }

    @Override
    public double record(DoubleSupplier f) {
        return f.getAsDouble();
    }

    @Override
    public <T> T recordCallable(Callable<T> f) throws Exception {
        return f.call();
    }

    @Override
    public void record(Runnable f) {
        f.run();
    }

    @Override
    public long count() {
        return 0L;
    }

    @Override
    public double totalTime(TimeUnit unit) {
        return 0.0;
    }

    @Override
    public double max(TimeUnit unit) {
        return 0.0;
    }

    @Override
    public TimeUnit baseTimeUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    public HistogramSnapshot takeSnapshot() {
        return HistogramSnapshot.empty(0L, 0.0, 0.0);
    }
}

