/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.util.profiling.MetricKey
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 *  com.atlassian.util.profiling.MetricsFilter
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.strategy.MetricStrategy
 *  io.micrometer.core.instrument.LongTaskTimer$Sample
 *  io.micrometer.core.instrument.Meter
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tag
 *  io.micrometer.core.instrument.Tags
 *  io.micrometer.core.instrument.Timer
 *  io.micrometer.core.instrument.Timer$Sample
 *  io.micrometer.core.instrument.search.MeterNotFoundException
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.util.profiling.micrometer;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.util.profiling.MetricKey;
import com.atlassian.util.profiling.MetricTag;
import com.atlassian.util.profiling.MetricsFilter;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.strategy.MetricStrategy;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.search.MeterNotFoundException;
import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Internal
public class MicrometerStrategy
implements MetricStrategy {
    private static final long INITIAL_GAUGE_VALUE = 0L;
    @VisibleForTesting
    final Map<MetricKey, AtomicLong> gauges = new ConcurrentHashMap<MetricKey, AtomicLong>();
    private final MeterRegistry registry;

    public MicrometerStrategy(MeterRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
        registry.config().onMeterRemoved(this::removeRelatedMetric);
    }

    public void cleanupMetrics(MetricsFilter filter) {
        for (Meter meter : this.registry.getMeters()) {
            String name = meter.getId().getName();
            if (filter.accepts(name)) continue;
            this.registry.remove(meter);
            meter.close();
        }
    }

    public void resetMetric(MetricKey metricKey) {
        for (Meter meter : this.registry.getMeters()) {
            List tagsToMatch;
            HashSet meterTags;
            if (!meter.getId().getName().equals(metricKey.getMetricName()) || meter.getId().getTags().size() != metricKey.getTags().size() || !(meterTags = new HashSet(meter.getId().getTags())).containsAll(tagsToMatch = metricKey.getTags().stream().map(profilingTag -> Tag.of((String)profilingTag.getKey(), (String)profilingTag.getValue())).collect(Collectors.toList()))) continue;
            this.registry.remove(meter);
            meter.close();
        }
    }

    @Nonnull
    public Ticker startTimer(String metricName) {
        return this.startTimer(this.registry.timer(metricName, new String[0]));
    }

    @Nonnull
    public Ticker startTimer(MetricKey metricKey) {
        return this.startTimer(this.registry.timer(metricKey.getMetricName(), MicrometerStrategy.getTags(metricKey)));
    }

    @Nonnull
    public Ticker startLongRunningTimer(String metricName) {
        return this.startLongRunningTimer(metricName, null);
    }

    @Nonnull
    public Ticker startLongRunningTimer(@Nonnull MetricKey metricKey) {
        Objects.requireNonNull(metricKey, "metricKey");
        return this.startLongRunningTimer(metricKey.getMetricName(), MicrometerStrategy.getTags(metricKey));
    }

    private Ticker startTimer(Timer timer) {
        Timer.Sample sample = Timer.start((MeterRegistry)this.registry);
        return () -> sample.stop(timer);
    }

    private Ticker startLongRunningTimer(String metricName, Collection<Tag> metricTags) {
        LongTaskTimer.Sample longTimerSample = this.registry.more().longTaskTimer(metricName, (Iterable)Tags.of(metricTags).and("subCategory", "current")).start();
        Timer timer = this.registry.timer(metricName, metricTags);
        Timer.Sample timerSample = Timer.start((MeterRegistry)this.registry);
        return () -> {
            longTimerSample.stop();
            timerSample.stop(timer);
        };
    }

    public void incrementCounter(MetricKey metricKey, long deltaValue) {
        this.registry.counter(metricKey.getMetricName(), MicrometerStrategy.getTags(metricKey)).increment((double)deltaValue);
    }

    public void updateHistogram(String metricName, long value) {
        this.registry.summary(metricName, new String[0]).record((double)value);
    }

    public void updateHistogram(MetricKey metricKey, long value) {
        this.registry.summary(metricKey.getMetricName(), MicrometerStrategy.getTags(metricKey)).record((double)value);
    }

    public void updateTimer(MetricKey metricKey, Duration time) {
        this.registry.timer(metricKey.getMetricName(), MicrometerStrategy.getTags(metricKey)).record(time);
    }

    public void updateTimer(String metricName, long time, TimeUnit timeUnit) {
        this.registry.timer(metricName, new String[0]).record(time, timeUnit);
    }

    public void incrementGauge(MetricKey metricKey, long deltaValue) {
        AtomicLong gauge = this.getAndSyncGauge(metricKey);
        gauge.addAndGet(deltaValue);
    }

    public void setGauge(MetricKey metricKey, long currentValue) {
        AtomicLong gauge = this.getAndSyncGauge(metricKey);
        gauge.set(currentValue);
    }

    private AtomicLong getAndSyncGauge(MetricKey metricKey) {
        AtomicLong gauge = this.gauges.get(metricKey);
        if (Objects.isNull(gauge) || this.isMissingInRegistry(metricKey)) {
            gauge = (AtomicLong)this.registry.gauge(metricKey.getMetricName(), MicrometerStrategy.getTags(metricKey), (Number)new AtomicLong(0L));
            this.gauges.put(metricKey, gauge);
        }
        return gauge;
    }

    private boolean isMissingInRegistry(MetricKey metricKey) {
        try {
            return this.registry.get(metricKey.getMetricName()).meters().isEmpty();
        }
        catch (MeterNotFoundException exception) {
            return true;
        }
    }

    private void removeRelatedMetric(Meter meter) {
        String name = meter.getId().getName();
        List<MetricTag.RequiredMetricTag> tags = MicrometerStrategy.getRequiredMetricTags(meter);
        this.gauges.remove(MetricKey.metricKey((String)name, tags));
    }

    private static List<MetricTag.RequiredMetricTag> getRequiredMetricTags(Meter meter) {
        return meter.getId().getTags().stream().map(tag -> MetricTag.RequiredMetricTag.of((String)tag.getKey(), (String)tag.getValue())).collect(Collectors.toList());
    }

    private static List<Tag> getTags(MetricKey metricKey) {
        return metricKey.getTags().stream().map(tag -> Tag.of((String)tag.getKey(), (String)tag.getValue())).collect(Collectors.toList());
    }
}

