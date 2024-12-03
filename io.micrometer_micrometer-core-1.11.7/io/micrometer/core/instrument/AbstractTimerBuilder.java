/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import java.time.Duration;
import java.util.Arrays;

public abstract class AbstractTimerBuilder<B extends AbstractTimerBuilder<B>> {
    protected final String name;
    protected Tags tags = Tags.empty();
    protected final DistributionStatisticConfig.Builder distributionConfigBuilder;
    @Nullable
    protected String description;
    @Nullable
    protected PauseDetector pauseDetector;

    protected AbstractTimerBuilder(String name) {
        this.name = name;
        this.distributionConfigBuilder = new DistributionStatisticConfig.Builder();
        this.minimumExpectedValue(Duration.ofMillis(1L));
        this.maximumExpectedValue(Duration.ofSeconds(30L));
    }

    public B tags(String ... tags) {
        return this.tags(Tags.of(tags));
    }

    public B tags(Iterable<Tag> tags) {
        this.tags = this.tags.and(tags);
        return (B)this;
    }

    public B tag(String key, String value) {
        this.tags = this.tags.and(key, value);
        return (B)this;
    }

    public B publishPercentiles(double ... percentiles) {
        this.distributionConfigBuilder.percentiles(percentiles);
        return (B)this;
    }

    public B percentilePrecision(@Nullable Integer digitsOfPrecision) {
        this.distributionConfigBuilder.percentilePrecision(digitsOfPrecision);
        return (B)this;
    }

    public B publishPercentileHistogram() {
        return this.publishPercentileHistogram(true);
    }

    public B publishPercentileHistogram(@Nullable Boolean enabled) {
        this.distributionConfigBuilder.percentilesHistogram(enabled);
        return (B)this;
    }

    @Deprecated
    public B sla(Duration ... sla) {
        return this.serviceLevelObjectives(sla);
    }

    public B serviceLevelObjectives(Duration ... slos) {
        if (slos != null) {
            this.distributionConfigBuilder.serviceLevelObjectives(Arrays.stream(slos).mapToDouble(Duration::toNanos).toArray());
        }
        return (B)this;
    }

    public B minimumExpectedValue(@Nullable Duration min) {
        if (min != null) {
            this.distributionConfigBuilder.minimumExpectedValue(Double.valueOf(min.toNanos()));
        }
        return (B)this;
    }

    public B maximumExpectedValue(@Nullable Duration max) {
        if (max != null) {
            this.distributionConfigBuilder.maximumExpectedValue(Double.valueOf(max.toNanos()));
        }
        return (B)this;
    }

    public B distributionStatisticExpiry(@Nullable Duration expiry) {
        this.distributionConfigBuilder.expiry(expiry);
        return (B)this;
    }

    public B distributionStatisticBufferLength(@Nullable Integer bufferLength) {
        this.distributionConfigBuilder.bufferLength(bufferLength);
        return (B)this;
    }

    public B pauseDetector(@Nullable PauseDetector pauseDetector) {
        this.pauseDetector = pauseDetector;
        return (B)this;
    }

    public B description(@Nullable String description) {
        this.description = description;
        return (B)this;
    }
}

