/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.composite;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.AbstractCompositeMeter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.noop.NoopTimer;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

class CompositeTimer
extends AbstractCompositeMeter<Timer>
implements Timer {
    private final Clock clock;
    private final DistributionStatisticConfig distributionStatisticConfig;
    private final PauseDetector pauseDetector;

    CompositeTimer(Meter.Id id, Clock clock, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        super(id);
        this.clock = clock;
        this.distributionStatisticConfig = distributionStatisticConfig;
        this.pauseDetector = pauseDetector;
    }

    @Override
    public void record(long amount, TimeUnit unit) {
        for (Timer timer : this.getChildren()) {
            timer.record(amount, unit);
        }
    }

    @Override
    public void record(Duration duration) {
        for (Timer timer : this.getChildren()) {
            timer.record(duration);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> T record(Supplier<T> f) {
        long s = this.clock.monotonicTime();
        try {
            T t = f.get();
            return t;
        }
        finally {
            long e = this.clock.monotonicTime();
            this.record(e - s, TimeUnit.NANOSECONDS);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean record(BooleanSupplier f) {
        long s = this.clock.monotonicTime();
        try {
            boolean bl = f.getAsBoolean();
            return bl;
        }
        finally {
            long e = this.clock.monotonicTime();
            this.record(e - s, TimeUnit.NANOSECONDS);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int record(IntSupplier f) {
        long s = this.clock.monotonicTime();
        try {
            int n = f.getAsInt();
            return n;
        }
        finally {
            long e = this.clock.monotonicTime();
            this.record(e - s, TimeUnit.NANOSECONDS);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long record(LongSupplier f) {
        long s = this.clock.monotonicTime();
        try {
            long l = f.getAsLong();
            return l;
        }
        finally {
            long e = this.clock.monotonicTime();
            this.record(e - s, TimeUnit.NANOSECONDS);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double record(DoubleSupplier f) {
        long s = this.clock.monotonicTime();
        try {
            double d = f.getAsDouble();
            return d;
        }
        finally {
            long e = this.clock.monotonicTime();
            this.record(e - s, TimeUnit.NANOSECONDS);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> T recordCallable(Callable<T> f) throws Exception {
        long s = this.clock.monotonicTime();
        try {
            T t = f.call();
            return t;
        }
        finally {
            long e = this.clock.monotonicTime();
            this.record(e - s, TimeUnit.NANOSECONDS);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void record(Runnable f) {
        long s = this.clock.monotonicTime();
        try {
            f.run();
        }
        finally {
            long e = this.clock.monotonicTime();
            this.record(e - s, TimeUnit.NANOSECONDS);
        }
    }

    @Override
    public long count() {
        return ((Timer)this.firstChild()).count();
    }

    @Override
    public double totalTime(TimeUnit unit) {
        return ((Timer)this.firstChild()).totalTime(unit);
    }

    @Override
    public double max(TimeUnit unit) {
        return ((Timer)this.firstChild()).max(unit);
    }

    @Override
    public HistogramSnapshot takeSnapshot() {
        return ((Timer)this.firstChild()).takeSnapshot();
    }

    @Override
    public TimeUnit baseTimeUnit() {
        return ((Timer)this.firstChild()).baseTimeUnit();
    }

    @Override
    Timer newNoopMeter() {
        return new NoopTimer(this.getId());
    }

    @Override
    Timer registerNewMeter(MeterRegistry registry) {
        Timer.Builder builder = ((Timer.Builder)Timer.builder(this.getId().getName()).tags((Iterable)this.getId().getTagsAsIterable())).description(this.getId().getDescription()).maximumExpectedValue(Duration.ofNanos(this.distributionStatisticConfig.getMaximumExpectedValueAsDouble().longValue())).minimumExpectedValue(Duration.ofNanos(this.distributionStatisticConfig.getMinimumExpectedValueAsDouble().longValue())).publishPercentiles(this.distributionStatisticConfig.getPercentiles()).publishPercentileHistogram(this.distributionStatisticConfig.isPercentileHistogram()).distributionStatisticBufferLength(this.distributionStatisticConfig.getBufferLength()).distributionStatisticExpiry(this.distributionStatisticConfig.getExpiry()).percentilePrecision(this.distributionStatisticConfig.getPercentilePrecision()).pauseDetector(this.pauseDetector);
        double[] sloNanos = this.distributionStatisticConfig.getServiceLevelObjectiveBoundaries();
        if (sloNanos != null) {
            Duration[] slo = new Duration[sloNanos.length];
            for (int i = 0; i < sloNanos.length; ++i) {
                slo[i] = Duration.ofNanos((long)sloNanos[i]);
            }
            builder = builder.serviceLevelObjectives(slo);
        }
        return builder.register(registry);
    }
}

