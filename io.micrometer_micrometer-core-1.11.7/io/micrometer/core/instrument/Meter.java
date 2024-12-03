/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.NamingConvention;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface Meter {
    public static Builder builder(String name, Type type, Iterable<Measurement> measurements) {
        return new Builder(name, type, measurements);
    }

    public Id getId();

    public Iterable<Measurement> measure();

    default public <T> T match(Function<Gauge, T> visitGauge, Function<Counter, T> visitCounter, Function<Timer, T> visitTimer, Function<DistributionSummary, T> visitSummary, Function<LongTaskTimer, T> visitLongTaskTimer, Function<TimeGauge, T> visitTimeGauge, Function<FunctionCounter, T> visitFunctionCounter, Function<FunctionTimer, T> visitFunctionTimer, Function<Meter, T> visitMeter) {
        if (this instanceof TimeGauge) {
            return visitTimeGauge.apply((TimeGauge)this);
        }
        if (this instanceof Gauge) {
            return visitGauge.apply((Gauge)this);
        }
        if (this instanceof Counter) {
            return visitCounter.apply((Counter)this);
        }
        if (this instanceof Timer) {
            return visitTimer.apply((Timer)this);
        }
        if (this instanceof DistributionSummary) {
            return visitSummary.apply((DistributionSummary)this);
        }
        if (this instanceof LongTaskTimer) {
            return visitLongTaskTimer.apply((LongTaskTimer)this);
        }
        if (this instanceof FunctionCounter) {
            return visitFunctionCounter.apply((FunctionCounter)this);
        }
        if (this instanceof FunctionTimer) {
            return visitFunctionTimer.apply((FunctionTimer)this);
        }
        return visitMeter.apply(this);
    }

    default public void use(Consumer<Gauge> visitGauge, Consumer<Counter> visitCounter, Consumer<Timer> visitTimer, Consumer<DistributionSummary> visitSummary, Consumer<LongTaskTimer> visitLongTaskTimer, Consumer<TimeGauge> visitTimeGauge, Consumer<FunctionCounter> visitFunctionCounter, Consumer<FunctionTimer> visitFunctionTimer, Consumer<Meter> visitMeter) {
        if (this instanceof TimeGauge) {
            visitTimeGauge.accept((TimeGauge)this);
        } else if (this instanceof Gauge) {
            visitGauge.accept((Gauge)this);
        } else if (this instanceof Counter) {
            visitCounter.accept((Counter)this);
        } else if (this instanceof Timer) {
            visitTimer.accept((Timer)this);
        } else if (this instanceof DistributionSummary) {
            visitSummary.accept((DistributionSummary)this);
        } else if (this instanceof LongTaskTimer) {
            visitLongTaskTimer.accept((LongTaskTimer)this);
        } else if (this instanceof FunctionCounter) {
            visitFunctionCounter.accept((FunctionCounter)this);
        } else if (this instanceof FunctionTimer) {
            visitFunctionTimer.accept((FunctionTimer)this);
        } else {
            visitMeter.accept(this);
        }
    }

    default public void close() {
    }

    public static class Builder {
        private final String name;
        private final Type type;
        private final Iterable<Measurement> measurements;
        private Tags tags = Tags.empty();
        @Nullable
        private String description;
        @Nullable
        private String baseUnit;

        private Builder(String name, Type type, Iterable<Measurement> measurements) {
            this.name = name;
            this.type = type;
            this.measurements = measurements;
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

        public Meter register(MeterRegistry registry) {
            return registry.register(new Id(this.name, this.tags, this.baseUnit, this.description, this.type), this.type, this.measurements);
        }
    }

    public static enum Type {
        COUNTER,
        GAUGE,
        LONG_TASK_TIMER,
        TIMER,
        DISTRIBUTION_SUMMARY,
        OTHER;

    }

    public static class Id {
        private final String name;
        private final Tags tags;
        private final Type type;
        @Nullable
        private final Id syntheticAssociation;
        @Nullable
        private final String description;
        @Nullable
        private final String baseUnit;

        @Incubating(since="1.1.0")
        Id(String name, Tags tags, @Nullable String baseUnit, @Nullable String description, Type type, @Nullable Id syntheticAssociation) {
            this.name = name;
            this.tags = tags;
            this.baseUnit = baseUnit;
            this.description = description;
            this.type = type;
            this.syntheticAssociation = syntheticAssociation;
        }

        public Id(String name, Tags tags, @Nullable String baseUnit, @Nullable String description, Type type) {
            this(name, tags, baseUnit, description, type, null);
        }

        public Id withName(String newName) {
            return new Id(newName, this.tags, this.baseUnit, this.description, this.type);
        }

        public Id withTag(Tag tag) {
            return this.withTags(Collections.singletonList(tag));
        }

        public Id withTags(Iterable<Tag> tags) {
            return new Id(this.name, Tags.concat(this.getTags(), tags), this.baseUnit, this.description, this.type);
        }

        public Id replaceTags(Iterable<Tag> tags) {
            return new Id(this.name, Tags.of(tags), this.baseUnit, this.description, this.type);
        }

        public Id withTag(Statistic statistic) {
            return this.withTag(Tag.of("statistic", statistic.getTagValueRepresentation()));
        }

        public Id withBaseUnit(@Nullable String newBaseUnit) {
            return new Id(this.name, this.tags, newBaseUnit, this.description, this.type);
        }

        public String getName() {
            return this.name;
        }

        public List<Tag> getTags() {
            ArrayList tags = new ArrayList();
            this.tags.forEach(tags::add);
            return Collections.unmodifiableList(tags);
        }

        public Iterable<Tag> getTagsAsIterable() {
            return this.tags;
        }

        @Nullable
        public String getTag(String key) {
            for (Tag tag : this.tags) {
                if (!tag.getKey().equals(key)) continue;
                return tag.getValue();
            }
            return null;
        }

        @Nullable
        public String getBaseUnit() {
            return this.baseUnit;
        }

        public String getConventionName(NamingConvention namingConvention) {
            return namingConvention.name(this.name, this.type, this.baseUnit);
        }

        public List<Tag> getConventionTags(NamingConvention namingConvention) {
            return StreamSupport.stream(this.tags.spliterator(), false).map(t -> Tag.of(namingConvention.tagKey(t.getKey()), namingConvention.tagValue(t.getValue()))).collect(Collectors.toList());
        }

        @Nullable
        public String getDescription() {
            return this.description;
        }

        public String toString() {
            return "MeterId{name='" + this.name + '\'' + ", tags=" + this.tags + '}';
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Id meterId = (Id)o;
            return Objects.equals(this.name, meterId.name) && Objects.equals(this.tags, meterId.tags);
        }

        public int hashCode() {
            int result = this.name.hashCode();
            result = 31 * result + this.tags.hashCode();
            return result;
        }

        public Type getType() {
            return this.type;
        }

        @Nullable
        @Incubating(since="1.1.0")
        public Id syntheticAssociation() {
            return this.syntheticAssociation;
        }
    }
}

