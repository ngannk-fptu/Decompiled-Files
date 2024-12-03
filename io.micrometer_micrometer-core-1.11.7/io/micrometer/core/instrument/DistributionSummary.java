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
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramSupport;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public interface DistributionSummary
extends Meter,
HistogramSupport {
    public static Builder builder(String name) {
        return new Builder(name);
    }

    public void record(double var1);

    public long count();

    public double totalAmount();

    default public double mean() {
        long count = this.count();
        return count == 0L ? 0.0 : this.totalAmount() / (double)count;
    }

    public double max();

    @Deprecated
    default public double histogramCountAtValue(long value) {
        for (CountAtBucket countAtBucket : this.takeSnapshot().histogramCounts()) {
            if ((long)countAtBucket.bucket(TimeUnit.NANOSECONDS) != value) continue;
            return countAtBucket.count();
        }
        return Double.NaN;
    }

    @Deprecated
    default public double percentile(double percentile) {
        for (ValueAtPercentile valueAtPercentile : this.takeSnapshot().percentileValues()) {
            if (valueAtPercentile.percentile() != percentile) continue;
            return valueAtPercentile.value();
        }
        return Double.NaN;
    }

    @Override
    default public Iterable<Measurement> measure() {
        return Arrays.asList(new Measurement(() -> this.count(), Statistic.COUNT), new Measurement(this::totalAmount, Statistic.TOTAL));
    }

    public static class Builder {
        private final String name;
        private Tags tags = Tags.empty();
        private DistributionStatisticConfig.Builder distributionConfigBuilder = DistributionStatisticConfig.builder();
        @Nullable
        private String description;
        @Nullable
        private String baseUnit;
        private double scale = 1.0;

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

        @Deprecated
        public Builder sla(long ... sla) {
            return sla == null ? this : this.serviceLevelObjectives(Arrays.stream(sla).asDoubleStream().toArray());
        }

        @Deprecated
        public Builder sla(double ... sla) {
            this.distributionConfigBuilder.serviceLevelObjectives(sla);
            return this;
        }

        public Builder serviceLevelObjectives(double ... slos) {
            this.distributionConfigBuilder.serviceLevelObjectives(slos);
            return this;
        }

        @Deprecated
        public Builder minimumExpectedValue(@Nullable Long min) {
            return min == null ? this : this.minimumExpectedValue((double)min);
        }

        public Builder minimumExpectedValue(@Nullable Double min) {
            this.distributionConfigBuilder.minimumExpectedValue(min);
            return this;
        }

        @Deprecated
        public Builder maximumExpectedValue(@Nullable Long max) {
            return max == null ? this : this.maximumExpectedValue((double)max);
        }

        public Builder maximumExpectedValue(@Nullable Double max) {
            this.distributionConfigBuilder.maximumExpectedValue(max);
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

        public Builder scale(double scale) {
            this.scale = scale;
            return this;
        }

        public DistributionSummary register(MeterRegistry registry) {
            return registry.summary(new Meter.Id(this.name, this.tags, this.baseUnit, this.description, Meter.Type.DISTRIBUTION_SUMMARY), this.distributionConfigBuilder.build(), this.scale);
        }
    }
}

