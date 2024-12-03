/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.StrongReferenceGaugeFunction;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Incubating(since="1.1.0")
public class MultiGauge {
    private final MeterRegistry registry;
    private final Meter.Id commonId;
    private final AtomicReference<Set<Meter.Id>> registeredRows = new AtomicReference(Collections.emptySet());

    private MultiGauge(MeterRegistry registry, Meter.Id commonId) {
        this.registry = registry;
        this.commonId = commonId;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public void register(Iterable<Row<?>> rows) {
        this.register(rows, false);
    }

    public void register(Iterable<Row<?>> rows, boolean overwrite) {
        this.registeredRows.getAndUpdate(oldRows -> {
            Stream<Meter.Id> idStream = StreamSupport.stream(rows.spliterator(), false).map(row -> {
                Row r = row;
                Meter.Id rowId = this.commonId.withTags(((Row)row).uniqueTags);
                boolean previouslyDefined = oldRows.contains(rowId);
                if (overwrite && previouslyDefined) {
                    this.registry.removeByPreFilterId(rowId);
                }
                if (overwrite || !previouslyDefined) {
                    this.registry.gauge(rowId, ((Row)row).obj, new StrongReferenceGaugeFunction<Object>(r.obj, r.valueFunction));
                }
                return rowId;
            });
            Set newRows = idStream.collect(Collectors.toSet());
            for (Meter.Id oldRow : oldRows) {
                if (newRows.contains(oldRow)) continue;
                this.registry.removeByPreFilterId(oldRow);
            }
            return newRows;
        });
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

        public MultiGauge register(MeterRegistry registry) {
            return new MultiGauge(registry, new Meter.Id(this.name, this.tags, this.baseUnit, this.description, Meter.Type.GAUGE, null));
        }
    }

    public static class Row<T> {
        private final Tags uniqueTags;
        private final T obj;
        private final ToDoubleFunction<T> valueFunction;

        private Row(Tags uniqueTags, T obj, ToDoubleFunction<T> valueFunction) {
            this.uniqueTags = uniqueTags;
            this.obj = obj;
            this.valueFunction = valueFunction;
        }

        public static <T> Row<T> of(Tags uniqueTags, T obj, ToDoubleFunction<T> valueFunction) {
            return new Row<T>(uniqueTags, obj, valueFunction);
        }

        public static Row<Number> of(Tags uniqueTags, Number number) {
            return new Row<Number>(uniqueTags, number, Number::doubleValue);
        }

        public static Row<Supplier<Number>> of(Tags uniqueTags, Supplier<Number> valueFunction) {
            return new Row<Supplier<Number>>(uniqueTags, valueFunction, f -> {
                Number value = (Number)valueFunction.get();
                return value == null ? Double.NaN : value.doubleValue();
            });
        }
    }
}

