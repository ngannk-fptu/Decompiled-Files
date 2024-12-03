/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.AbstractTimerBuilder;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.HistogramSupport;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface Timer
extends Meter,
HistogramSupport {
    public static Sample start() {
        return Timer.start(Clock.SYSTEM);
    }

    public static Sample start(MeterRegistry registry) {
        return Timer.start(registry.config().clock());
    }

    public static Sample start(Clock clock) {
        return new Sample(clock);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    @Incubating(since="1.6.0")
    public static ResourceSample resource(MeterRegistry registry, String name) {
        return new ResourceSample(registry, name);
    }

    public static Builder builder(Timed timed, String defaultName) {
        if (timed.longTask() && timed.value().isEmpty()) {
            throw new IllegalArgumentException("Long tasks instrumented with @Timed require the value attribute to be non-empty");
        }
        return new Builder(timed.value().isEmpty() ? defaultName : timed.value()).tags(timed.extraTags()).description(timed.description().isEmpty() ? null : timed.description()).publishPercentileHistogram(timed.histogram()).publishPercentiles(timed.percentiles().length > 0 ? timed.percentiles() : null);
    }

    public void record(long var1, TimeUnit var3);

    default public void record(Duration duration) {
        this.record(duration.toNanos(), TimeUnit.NANOSECONDS);
    }

    @Nullable
    public <T> T record(Supplier<T> var1);

    default public boolean record(BooleanSupplier f) {
        return this.record(f::getAsBoolean);
    }

    default public int record(IntSupplier f) {
        return this.record(f::getAsInt);
    }

    default public long record(LongSupplier f) {
        return this.record(f::getAsLong);
    }

    default public double record(DoubleSupplier f) {
        return this.record(f::getAsDouble);
    }

    @Nullable
    public <T> T recordCallable(Callable<T> var1) throws Exception;

    public void record(Runnable var1);

    default public Runnable wrap(Runnable f) {
        return () -> this.record(f);
    }

    default public <T> Callable<T> wrap(Callable<T> f) {
        return () -> this.recordCallable(f);
    }

    default public <T> Supplier<T> wrap(Supplier<T> f) {
        return () -> this.record(f);
    }

    public long count();

    public double totalTime(TimeUnit var1);

    default public double mean(TimeUnit unit) {
        long count = this.count();
        return count == 0L ? 0.0 : this.totalTime(unit) / (double)count;
    }

    public double max(TimeUnit var1);

    @Override
    default public Iterable<Measurement> measure() {
        return Arrays.asList(new Measurement(() -> this.count(), Statistic.COUNT), new Measurement(() -> this.totalTime(this.baseTimeUnit()), Statistic.TOTAL_TIME), new Measurement(() -> this.max(this.baseTimeUnit()), Statistic.MAX));
    }

    @Deprecated
    default public double histogramCountAtValue(long valueNanos) {
        for (CountAtBucket countAtBucket : this.takeSnapshot().histogramCounts()) {
            if ((long)countAtBucket.bucket(TimeUnit.NANOSECONDS) != valueNanos) continue;
            return countAtBucket.count();
        }
        return Double.NaN;
    }

    @Deprecated
    default public double percentile(double percentile, TimeUnit unit) {
        for (ValueAtPercentile valueAtPercentile : this.takeSnapshot().percentileValues()) {
            if (valueAtPercentile.percentile() != percentile) continue;
            return valueAtPercentile.value(unit);
        }
        return Double.NaN;
    }

    public TimeUnit baseTimeUnit();

    public static class Sample {
        private final long startTime;
        private final Clock clock;

        Sample(Clock clock) {
            this.clock = clock;
            this.startTime = clock.monotonicTime();
        }

        public long stop(Timer timer) {
            long durationNs = this.clock.monotonicTime() - this.startTime;
            timer.record(durationNs, TimeUnit.NANOSECONDS);
            return durationNs;
        }
    }

    public static class Builder
    extends AbstractTimerBuilder<Builder> {
        Builder(String name) {
            super(name);
        }

        @Override
        public Builder tags(String ... tags) {
            return (Builder)super.tags(tags);
        }

        @Override
        public Builder tags(Iterable<Tag> tags) {
            return (Builder)super.tags(tags);
        }

        @Override
        public Builder tag(String key, String value) {
            return (Builder)super.tag(key, value);
        }

        @Override
        public Builder publishPercentiles(double ... percentiles) {
            return (Builder)super.publishPercentiles(percentiles);
        }

        @Override
        public Builder percentilePrecision(Integer digitsOfPrecision) {
            return (Builder)super.percentilePrecision(digitsOfPrecision);
        }

        @Override
        public Builder publishPercentileHistogram() {
            return (Builder)super.publishPercentileHistogram();
        }

        @Override
        public Builder publishPercentileHistogram(Boolean enabled) {
            return (Builder)super.publishPercentileHistogram(enabled);
        }

        @Override
        public Builder sla(Duration ... sla) {
            return (Builder)super.sla(sla);
        }

        @Override
        public Builder serviceLevelObjectives(Duration ... slos) {
            return (Builder)super.serviceLevelObjectives(slos);
        }

        @Override
        public Builder minimumExpectedValue(Duration min) {
            return (Builder)super.minimumExpectedValue(min);
        }

        @Override
        public Builder maximumExpectedValue(Duration max) {
            return (Builder)super.maximumExpectedValue(max);
        }

        @Override
        public Builder distributionStatisticExpiry(Duration expiry) {
            return (Builder)super.distributionStatisticExpiry(expiry);
        }

        @Override
        public Builder distributionStatisticBufferLength(Integer bufferLength) {
            return (Builder)super.distributionStatisticBufferLength(bufferLength);
        }

        @Override
        public Builder pauseDetector(PauseDetector pauseDetector) {
            return (Builder)super.pauseDetector(pauseDetector);
        }

        @Override
        public Builder description(String description) {
            return (Builder)super.description(description);
        }

        public Timer register(MeterRegistry registry) {
            return registry.timer(new Meter.Id(this.name, this.tags, null, this.description, Meter.Type.TIMER), this.distributionConfigBuilder.build(), this.pauseDetector == null ? registry.config().pauseDetector() : this.pauseDetector);
        }
    }

    public static class ResourceSample
    extends AbstractTimerBuilder<ResourceSample>
    implements AutoCloseable {
        private final MeterRegistry registry;
        private final long startTime;

        ResourceSample(MeterRegistry registry, String name) {
            super(name);
            this.registry = registry;
            this.startTime = registry.config().clock().monotonicTime();
        }

        @Override
        public void close() {
            long durationNs = this.registry.config().clock().monotonicTime() - this.startTime;
            this.registry.timer(new Meter.Id(this.name, this.tags, null, this.description, Meter.Type.TIMER), this.distributionConfigBuilder.build(), this.pauseDetector == null ? this.registry.config().pauseDetector() : this.pauseDetector).record(durationNs, TimeUnit.NANOSECONDS);
        }
    }
}

