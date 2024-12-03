/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

public interface FunctionTimer
extends Meter {
    public static <T> Builder<T> builder(String name, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
        return new Builder(name, obj, countFunction, totalTimeFunction, totalTimeFunctionUnit);
    }

    public double count();

    public double totalTime(TimeUnit var1);

    default public double mean(TimeUnit unit) {
        double count = this.count();
        return count == 0.0 ? 0.0 : this.totalTime(unit) / count;
    }

    public TimeUnit baseTimeUnit();

    @Override
    default public Iterable<Measurement> measure() {
        return Arrays.asList(new Measurement(this::count, Statistic.COUNT), new Measurement(() -> this.totalTime(this.baseTimeUnit()), Statistic.TOTAL_TIME));
    }

    public static class Builder<T> {
        private final String name;
        private final ToLongFunction<T> countFunction;
        private final ToDoubleFunction<T> totalTimeFunction;
        private final TimeUnit totalTimeFunctionUnit;
        private Tags tags = Tags.empty();
        @Nullable
        private final T obj;
        @Nullable
        private String description;

        private Builder(String name, @Nullable T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
            this.name = name;
            this.obj = obj;
            this.countFunction = countFunction;
            this.totalTimeFunction = totalTimeFunction;
            this.totalTimeFunctionUnit = totalTimeFunctionUnit;
        }

        public Builder<T> tags(String ... tags) {
            return this.tags(Tags.of(tags));
        }

        public Builder<T> tags(Iterable<Tag> tags) {
            this.tags = this.tags.and(tags);
            return this;
        }

        public Builder<T> tag(String key, String value) {
            this.tags = this.tags.and(key, value);
            return this;
        }

        public Builder<T> description(@Nullable String description) {
            this.description = description;
            return this;
        }

        public FunctionTimer register(MeterRegistry registry) {
            return registry.more().timer(new Meter.Id(this.name, this.tags, null, this.description, Meter.Type.TIMER), this.obj, this.countFunction, this.totalTimeFunction, this.totalTimeFunctionUnit);
        }
    }
}

