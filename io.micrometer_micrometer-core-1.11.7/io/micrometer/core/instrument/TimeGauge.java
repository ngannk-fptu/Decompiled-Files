/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.StrongReferenceGaugeFunction;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.util.TimeUtils;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

public interface TimeGauge
extends Gauge {
    public static <T> Builder<T> builder(String name, @Nullable T obj, TimeUnit fUnits, ToDoubleFunction<T> f) {
        return new Builder(name, obj, fUnits, f);
    }

    @Incubating(since="1.7.0")
    public static Builder<Supplier<Number>> builder(String name, Supplier<Number> f, TimeUnit fUnits) {
        return new Builder(name, f, fUnits, f2 -> {
            Number val = (Number)f2.get();
            return val == null ? Double.NaN : val.doubleValue();
        }).strongReference(true);
    }

    public TimeUnit baseTimeUnit();

    default public double value(TimeUnit unit) {
        return TimeUtils.convert(this.value(), this.baseTimeUnit(), unit);
    }

    public static class Builder<T> {
        private final String name;
        private final TimeUnit fUnits;
        private final ToDoubleFunction<T> f;
        private Tags tags = Tags.empty();
        private boolean strongReference = false;
        @Nullable
        private final T obj;
        @Nullable
        private String description;

        private Builder(String name, @Nullable T obj, TimeUnit fUnits, ToDoubleFunction<T> f) {
            this.name = name;
            this.obj = obj;
            this.fUnits = fUnits;
            this.f = f;
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

        @Incubating(since="1.7.0")
        public Builder<T> strongReference(boolean strong) {
            this.strongReference = strong;
            return this;
        }

        public TimeGauge register(MeterRegistry registry) {
            return registry.more().timeGauge(new Meter.Id(this.name, this.tags, null, this.description, Meter.Type.GAUGE), this.obj, this.fUnits, this.strongReference ? new StrongReferenceGaugeFunction<T>(this.obj, this.f) : this.f);
        }
    }
}

