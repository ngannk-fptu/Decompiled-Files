/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.plugin.util.PluginKeyStack
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.plugin.util.PluginKeyStack;
import com.atlassian.util.profiling.CompositeTicker;
import com.atlassian.util.profiling.Histogram;
import com.atlassian.util.profiling.LongRunningMetricTimer;
import com.atlassian.util.profiling.MetricKey;
import com.atlassian.util.profiling.MetricTag;
import com.atlassian.util.profiling.MetricTagContext;
import com.atlassian.util.profiling.MetricTimer;
import com.atlassian.util.profiling.MetricsConfiguration;
import com.atlassian.util.profiling.StrategiesRegistry;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Tickers;
import com.atlassian.util.profiling.strategy.MetricStrategy;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Internal
public class Metrics {
    private static final MetricsConfiguration CONFIGURATION = new MetricsConfiguration();

    private Metrics() {
        throw new UnsupportedOperationException("Metrics is an utility class and should not be instantiated");
    }

    public static void resetMetric(MetricKey metricKey) {
        for (MetricStrategy strategy : StrategiesRegistry.getMetricStrategies()) {
            strategy.resetMetric(metricKey);
        }
    }

    @Nonnull
    public static MetricsConfiguration getConfiguration() {
        return CONFIGURATION;
    }

    @Deprecated
    @Nonnull
    public static Histogram histogram(String name) {
        return Metrics.metric(name).histogram();
    }

    @Deprecated
    @Nonnull
    public static Histogram histogram(String name, Collection<MetricTag.RequiredMetricTag> tags) {
        return Metrics.metric(name).tags(tags).histogram();
    }

    @Deprecated
    @Nonnull
    public static Histogram histogram(String name, MetricTag.RequiredMetricTag ... tags) {
        return Metrics.metric(name).tags(tags).histogram();
    }

    @Deprecated
    @Nonnull
    public static MetricTimer timer(String name) {
        return Metrics.metric(name).timer();
    }

    @Deprecated
    @Nonnull
    public static MetricTimer timer(String name, Collection<MetricTag.RequiredMetricTag> tags) {
        return Metrics.metric(name).tags(tags).timer();
    }

    @Deprecated
    @Nonnull
    public static MetricTimer timer(String name, MetricTag.RequiredMetricTag ... tags) {
        return Metrics.metric(name).tags(tags).timer();
    }

    @Deprecated
    @Nonnull
    public static LongRunningMetricTimer longRunningTimer(String name) {
        return Metrics.metric(name).longRunningTimer();
    }

    @Deprecated
    @Nonnull
    public static LongRunningMetricTimer longRunningTimer(String name, Collection<MetricTag.RequiredMetricTag> tags) {
        return Metrics.metric(name).tags(tags).longRunningTimer();
    }

    @Deprecated
    @Nonnull
    public static LongRunningMetricTimer longRunningTimer(String name, MetricTag.RequiredMetricTag ... tags) {
        return Metrics.longRunningTimer(name, Arrays.asList(tags));
    }

    @Deprecated
    @Nonnull
    public static Ticker startTimer(String name) {
        return Metrics.startTimer(name, Collections.emptySet());
    }

    @Deprecated
    @Nonnull
    public static Ticker startTimer(String name, MetricTag.RequiredMetricTag ... tags) {
        return Metrics.startTimer(name, Arrays.asList(tags));
    }

    @Deprecated
    @Nonnull
    public static Ticker startTimer(String name, Collection<MetricTag.RequiredMetricTag> tags) {
        return Metrics.metric(name).tags(tags).startTimer();
    }

    @Deprecated
    @Nonnull
    public static Ticker startLongRunningTimer(String name) {
        return Metrics.metric(name).startLongRunningTimer();
    }

    @Deprecated
    @Nonnull
    public static Ticker startLongRunningTimer(String name, MetricTag.RequiredMetricTag ... tags) {
        return Metrics.metric(name).tags(tags).startLongRunningTimer();
    }

    @Deprecated
    @Nonnull
    public static Ticker startLongRunningTimer(String name, Collection<MetricTag.RequiredMetricTag> tags) {
        return Metrics.metric(name).tags(tags).startLongRunningTimer();
    }

    public static Builder metric(String name) {
        return new Builder(name);
    }

    private static boolean accepts(MetricKey metricKey) {
        return Metrics.getConfiguration().isEnabled() && Metrics.getConfiguration().getFilter().accepts(metricKey.getMetricName());
    }

    @ParametersAreNonnullByDefault
    private static class DefaultHistogram
    implements Histogram {
        private static final Logger log = LoggerFactory.getLogger(DefaultHistogram.class);
        private final MetricKey metricKey;

        private DefaultHistogram(MetricKey metricKey) {
            this.metricKey = Objects.requireNonNull(metricKey, "metricKey");
        }

        @Override
        public void update(long value) {
            if (!Metrics.accepts(this.metricKey)) {
                return;
            }
            for (MetricStrategy strategy : StrategiesRegistry.getMetricStrategies()) {
                try {
                    strategy.updateHistogram(this.metricKey, value);
                }
                catch (RuntimeException e) {
                    log.warn("Failed to update histogram for {}", (Object)this.metricKey, (Object)e);
                }
            }
        }
    }

    @ParametersAreNonnullByDefault
    private static class DefaultLongRunningMetricTimer
    implements LongRunningMetricTimer {
        private static final Logger log = LoggerFactory.getLogger(DefaultLongRunningMetricTimer.class);
        private final MetricKey metricKey;

        DefaultLongRunningMetricTimer(MetricKey name) {
            this.metricKey = Objects.requireNonNull(name, "metricKey");
        }

        @Override
        @Nonnull
        public Ticker start() {
            if (!Metrics.accepts(this.metricKey)) {
                return Ticker.NO_OP;
            }
            Collection<MetricStrategy> metricStrategies = StrategiesRegistry.getMetricStrategies();
            if (metricStrategies.isEmpty()) {
                return Ticker.NO_OP;
            }
            CompositeTicker compositeTicker = null;
            for (MetricStrategy strategy : metricStrategies) {
                try {
                    compositeTicker = Tickers.addTicker(strategy.startLongRunningTimer(this.metricKey), compositeTicker);
                }
                catch (RuntimeException e) {
                    log.warn("Failed to start metric trace for {}", (Object)this.metricKey, (Object)e);
                }
            }
            return compositeTicker == null ? Ticker.NO_OP : compositeTicker;
        }
    }

    @ParametersAreNonnullByDefault
    private static class DefaultMetricTimer
    implements MetricTimer {
        private static final Logger log = LoggerFactory.getLogger(DefaultMetricTimer.class);
        private final MetricKey metricKey;

        DefaultMetricTimer(MetricKey name) {
            this.metricKey = Objects.requireNonNull(name, "metricKey");
        }

        @Override
        @Nonnull
        public Ticker start() {
            if (!Metrics.accepts(this.metricKey)) {
                return Ticker.NO_OP;
            }
            Collection<MetricStrategy> metricStrategies = StrategiesRegistry.getMetricStrategies();
            if (metricStrategies.isEmpty()) {
                return Ticker.NO_OP;
            }
            CompositeTicker compositeTicker = null;
            for (MetricStrategy strategy : metricStrategies) {
                try {
                    compositeTicker = Tickers.addTicker(strategy.startTimer(this.metricKey), compositeTicker);
                }
                catch (RuntimeException e) {
                    log.warn("Failed to start metric trace for {}", (Object)this.metricKey, (Object)e);
                }
            }
            return compositeTicker == null ? Ticker.NO_OP : compositeTicker;
        }

        @Override
        public void update(Duration time) {
            Objects.requireNonNull(time, "time");
            if (!Metrics.accepts(this.metricKey)) {
                return;
            }
            for (MetricStrategy strategy : StrategiesRegistry.getMetricStrategies()) {
                try {
                    strategy.updateTimer(this.metricKey, time);
                }
                catch (RuntimeException e) {
                    log.warn("Failed to update metric for {}", (Object)this.metricKey, (Object)e);
                }
            }
        }
    }

    @ParametersAreNonnullByDefault
    public static class Builder {
        private final String name;
        private final Map<String, MetricTag.RequiredMetricTag> requiredTags = new HashMap<String, MetricTag.RequiredMetricTag>();
        private final Map<String, MetricTag.OptionalMetricTag> optionalTags = new HashMap<String, MetricTag.OptionalMetricTag>();

        Builder(String name) {
            this.name = name;
        }

        @Nonnull
        public Builder collect(String ... metricTagKeys) {
            return this.collect(Arrays.asList(metricTagKeys));
        }

        @Nonnull
        public Builder collect(Iterable<String> metricTagKeys) {
            Set metricTagKeysSet = StreamSupport.stream(metricTagKeys.spliterator(), false).collect(Collectors.toSet());
            return this.tags(MetricTagContext.getAll().stream().filter(metricTag -> metricTagKeysSet.contains(metricTag.getKey())).map(MetricTag.OptionalMetricTag::convert).collect(Collectors.toList()));
        }

        @Nonnull
        public Builder tag(String key, boolean value) {
            return this.tags(MetricTag.of(key, value));
        }

        @Nonnull
        public Builder tag(String key, int value) {
            return this.tags(MetricTag.of(key, value));
        }

        @Nonnull
        public Builder tag(String key, @Nullable String value) {
            return this.tags(MetricTag.of(key, value));
        }

        @Nonnull
        public Builder tags(MetricTag.RequiredMetricTag ... tags) {
            return this.tags(Arrays.asList(tags));
        }

        @Nonnull
        public Builder tags(Iterable<MetricTag.RequiredMetricTag> requiredMetricTags) {
            requiredMetricTags.forEach(requiredMetricTag -> this.requiredTags.put(requiredMetricTag.getKey(), (MetricTag.RequiredMetricTag)requiredMetricTag));
            return this;
        }

        @Nonnull
        public Builder optionalTag(String key, boolean value) {
            return this.optionalTags(MetricTag.optionalOf(key, value));
        }

        @Nonnull
        public Builder optionalTag(String key, int value) {
            return this.optionalTags(MetricTag.optionalOf(key, value));
        }

        @Nonnull
        public Builder optionalTag(String key, @Nullable String value) {
            return this.optionalTags(MetricTag.optionalOf(key, value));
        }

        @Nonnull
        public Builder optionalTags(MetricTag.OptionalMetricTag ... optionalMetricTags) {
            return this.optionalTags(Arrays.asList(optionalMetricTags));
        }

        @Nonnull
        public Builder optionalTags(Iterable<MetricTag.OptionalMetricTag> optionalMetricTags) {
            optionalMetricTags.forEach(optionalMetricTag -> this.optionalTags.put(optionalMetricTag.getKey(), (MetricTag.OptionalMetricTag)optionalMetricTag));
            return this;
        }

        private boolean isOptionalTagEnabled(MetricTag.OptionalMetricTag optionalMetricTag) {
            return CONFIGURATION.isOptionalTagEnabled(this.name, optionalMetricTag.getKey());
        }

        @Nonnull
        public Builder fromPluginKey(@Nullable String pluginKey) {
            if (Objects.isNull(pluginKey)) {
                this.requiredTags.remove("fromPluginKey");
                return this;
            }
            return this.tag("fromPluginKey", pluginKey);
        }

        @Nonnull
        public Builder invokerPluginKey(@Nullable String pluginKey) {
            if (Objects.isNull(pluginKey)) {
                this.requiredTags.remove("invokerPluginKey");
                return this;
            }
            return this.tag("invokerPluginKey", pluginKey);
        }

        @Nonnull
        public Builder withInvokerPluginKey() {
            return this.invokerPluginKey(PluginKeyStack.getFirstPluginKey());
        }

        @Nonnull
        public Builder withAnalytics() {
            return this.tags(MetricTag.SEND_ANALYTICS);
        }

        @Nonnull
        public Histogram histogram() {
            return new DefaultHistogram(MetricKey.metricKey(this.name, this.getAllTags()));
        }

        public void incrementCounter(@Nullable Long deltaValue) {
            MetricKey metricKey = MetricKey.metricKey(this.name, this.getAllTags());
            if (!Metrics.accepts(metricKey)) {
                return;
            }
            if (Objects.isNull(deltaValue)) {
                return;
            }
            if (deltaValue < 0L) {
                throw new IllegalArgumentException("The delta value must be not be negative; received: " + deltaValue + " for the metric: " + metricKey);
            }
            StrategiesRegistry.getMetricStrategies().forEach(metricStrategy -> metricStrategy.incrementCounter(metricKey, deltaValue));
        }

        public void incrementGauge(@Nullable Long deltaValue) {
            MetricKey metricKey = MetricKey.metricKey(this.name, this.getAllTags());
            if (!Metrics.accepts(metricKey)) {
                return;
            }
            if (Objects.isNull(deltaValue)) {
                return;
            }
            StrategiesRegistry.getMetricStrategies().forEach(metricStrategy -> metricStrategy.incrementGauge(metricKey, deltaValue));
        }

        public void setGauge(@Nullable Long currentValue) {
            MetricKey metricKey = MetricKey.metricKey(this.name, this.getAllTags());
            if (!Metrics.accepts(metricKey)) {
                return;
            }
            if (Objects.isNull(currentValue)) {
                return;
            }
            StrategiesRegistry.getMetricStrategies().forEach(metricStrategy -> metricStrategy.setGauge(metricKey, currentValue));
        }

        @Nonnull
        public MetricTimer timer() {
            return new DefaultMetricTimer(MetricKey.metricKey(this.name, this.getAllTags()));
        }

        private Collection<MetricTag.RequiredMetricTag> getAllTags() {
            Set<MetricTag.OptionalMetricTag> optionalContext = MetricTagContext.getAll();
            if (optionalContext.isEmpty() && this.optionalTags.isEmpty()) {
                return this.requiredTags.values();
            }
            Stream<MetricTag.RequiredMetricTag> allEnabledOptionalTags = Stream.concat(optionalContext.stream(), this.optionalTags.values().stream()).filter(this::isOptionalTagEnabled).map(MetricTag.OptionalMetricTag::convert);
            return Stream.concat(allEnabledOptionalTags, this.requiredTags.values().stream()).collect(Collectors.toMap(MetricTag::getKey, Function.identity(), (firstTag, secondTag) -> secondTag)).values();
        }

        @Nonnull
        public LongRunningMetricTimer longRunningTimer() {
            return new DefaultLongRunningMetricTimer(MetricKey.metricKey(this.name, this.getAllTags()));
        }

        @Nonnull
        public Ticker startTimer() {
            return this.timer().start();
        }

        @Nonnull
        public Ticker startLongRunningTimer() {
            return this.longRunningTimer().start();
        }
    }
}

