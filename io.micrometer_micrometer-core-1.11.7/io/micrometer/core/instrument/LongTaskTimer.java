/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramSupport;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface LongTaskTimer
extends Meter,
HistogramSupport {
    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static Builder builder(Timed timed) {
        if (!timed.longTask()) {
            throw new IllegalArgumentException("Cannot build a long task timer from a @Timed annotation that is not marked as a long task");
        }
        if (timed.value().isEmpty()) {
            throw new IllegalArgumentException("Long tasks instrumented with @Timed require the value attribute to be non-empty");
        }
        return new Builder(timed.value()).tags(timed.extraTags()).publishPercentileHistogram(timed.histogram()).description(timed.description().isEmpty() ? null : timed.description());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    default public <T> T recordCallable(Callable<T> f) throws Exception {
        Sample sample = this.start();
        try {
            T t = f.call();
            return t;
        }
        finally {
            sample.stop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    default public <T> T record(Supplier<T> f) {
        Sample sample = this.start();
        try {
            T t = f.get();
            return t;
        }
        finally {
            sample.stop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    default public boolean record(BooleanSupplier f) {
        Sample sample = this.start();
        try {
            boolean bl = f.getAsBoolean();
            return bl;
        }
        finally {
            sample.stop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    default public int record(IntSupplier f) {
        Sample sample = this.start();
        try {
            int n = f.getAsInt();
            return n;
        }
        finally {
            sample.stop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    default public long record(LongSupplier f) {
        Sample sample = this.start();
        try {
            long l = f.getAsLong();
            return l;
        }
        finally {
            sample.stop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    default public double record(DoubleSupplier f) {
        Sample sample = this.start();
        try {
            double d = f.getAsDouble();
            return d;
        }
        finally {
            sample.stop();
        }
    }

    default public void record(Consumer<Sample> f) {
        Sample sample = this.start();
        try {
            f.accept(sample);
        }
        finally {
            sample.stop();
        }
    }

    default public void record(Runnable f) {
        Sample sample = this.start();
        try {
            f.run();
        }
        finally {
            sample.stop();
        }
    }

    public Sample start();

    public double duration(TimeUnit var1);

    public int activeTasks();

    default public double mean(TimeUnit unit) {
        int activeTasks = this.activeTasks();
        return activeTasks == 0 ? 0.0 : this.duration(unit) / (double)activeTasks;
    }

    public double max(TimeUnit var1);

    public TimeUnit baseTimeUnit();

    @Deprecated
    default public long stop(long task) {
        return -1L;
    }

    @Deprecated
    default public double duration(long task, TimeUnit unit) {
        return -1.0;
    }

    @Override
    default public Iterable<Measurement> measure() {
        return Arrays.asList(new Measurement(() -> this.activeTasks(), Statistic.ACTIVE_TASKS), new Measurement(() -> this.duration(this.baseTimeUnit()), Statistic.DURATION));
    }

    public static class Builder {
        private final String name;
        private Tags tags = Tags.empty();
        private final DistributionStatisticConfig.Builder distributionConfigBuilder = new DistributionStatisticConfig.Builder();
        @Nullable
        private String description;

        private Builder(String name) {
            this.name = name;
            this.minimumExpectedValue(Duration.ofMinutes(2L));
            this.maximumExpectedValue(Duration.ofHours(2L));
        }

        public Builder tags(String ... tags) {
            return this.tags(Tags.of(tags));
        }

        public Builder tags(Iterable<Tag> tags) {
            this.tags = this.tags.and(tags);
            return this;
        }

        public Builder tag(String key, String value) {
            this.tags = this.tags.and(key, value);
            return this;
        }

        public Builder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        public Builder serviceLevelObjectives(Duration ... slos) {
            if (slos != null) {
                this.distributionConfigBuilder.serviceLevelObjectives(Arrays.stream(slos).mapToDouble(Duration::toNanos).toArray());
            }
            return this;
        }

        public Builder minimumExpectedValue(@Nullable Duration min) {
            if (min != null) {
                this.distributionConfigBuilder.minimumExpectedValue(Double.valueOf(min.toNanos()));
            }
            return this;
        }

        public Builder maximumExpectedValue(@Nullable Duration max) {
            if (max != null) {
                this.distributionConfigBuilder.maximumExpectedValue(Double.valueOf(max.toNanos()));
            }
            return this;
        }

        public Builder distributionStatisticExpiry(@Nullable Duration expiry) {
            this.distributionConfigBuilder.expiry(expiry);
            return this;
        }

        public Builder distributionStatisticBufferLength(@Nullable Integer bufferLength) {
            this.distributionConfigBuilder.bufferLength(bufferLength);
            return this;
        }

        public Builder publishPercentiles(double ... percentiles) {
            this.distributionConfigBuilder.percentiles(percentiles);
            return this;
        }

        public Builder percentilePrecision(@Nullable Integer digitsOfPrecision) {
            this.distributionConfigBuilder.percentilePrecision(digitsOfPrecision);
            return this;
        }

        public Builder publishPercentileHistogram() {
            return this.publishPercentileHistogram(true);
        }

        public Builder publishPercentileHistogram(@Nullable Boolean enabled) {
            this.distributionConfigBuilder.percentilesHistogram(enabled);
            return this;
        }

        public LongTaskTimer register(MeterRegistry registry) {
            return registry.more().longTaskTimer(new Meter.Id(this.name, this.tags, null, this.description, Meter.Type.LONG_TASK_TIMER), this.distributionConfigBuilder.build());
        }
    }

    public static abstract class Sample {
        public abstract long stop();

        public abstract double duration(TimeUnit var1);
    }
}

