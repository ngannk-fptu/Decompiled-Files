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

public interface Counter
extends Meter {
    public static Builder builder(String name) {
        return new Builder(name);
    }

    default public void increment() {
        this.increment(1.0);
    }

    public void increment(double var1);

    public double count();

    @Override
    default public Iterable<Measurement> measure() {
        return Collections.singletonList(new Measurement(this::count, Statistic.COUNT));
    }

    public static class Builder {
        private final String name;
        private Tags tags = Tags.empty();
        @Nullable
        private String description;
        @Nullable
        private String baseUnit;

        private Builder(String name) {
            this.name = name;
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

        public Builder baseUnit(@Nullable String unit) {
            this.baseUnit = unit;
            return this;
        }

        public Counter register(MeterRegistry registry) {
            return registry.counter(new Meter.Id(this.name, this.tags, this.baseUnit, this.description, Meter.Type.COUNTER));
        }
    }
}

