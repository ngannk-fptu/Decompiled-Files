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
import java.util.Collections;
import java.util.function.ToDoubleFunction;

public interface FunctionCounter
extends Meter {
    public static <T> Builder<T> builder(String name, @Nullable T obj, ToDoubleFunction<T> f) {
        return new Builder(name, obj, f);
    }

    public double count();

    @Override
    default public Iterable<Measurement> measure() {
        return Collections.singletonList(new Measurement(this::count, Statistic.COUNT));
    }

    public static class Builder<T> {
        private final String name;
        private final ToDoubleFunction<T> f;
        private Tags tags = Tags.empty();
        @Nullable
        private final T obj;
        @Nullable
        private String description;
        @Nullable
        private String baseUnit;

        private Builder(String name, @Nullable T obj, ToDoubleFunction<T> f) {
            this.name = name;
            this.obj = obj;
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

        public Builder<T> baseUnit(@Nullable String unit) {
            this.baseUnit = unit;
            return this;
        }

        public FunctionCounter register(MeterRegistry registry) {
            return registry.more().counter(new Meter.Id(this.name, this.tags, this.baseUnit, this.description, Meter.Type.COUNTER), this.obj, this.f);
        }
    }
}

