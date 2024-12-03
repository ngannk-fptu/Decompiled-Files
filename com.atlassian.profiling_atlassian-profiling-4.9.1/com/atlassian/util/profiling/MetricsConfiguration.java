/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.util.profiling.MetricsFilter;
import com.atlassian.util.profiling.StrategiesRegistry;
import com.atlassian.util.profiling.strategy.MetricStrategy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Internal
public class MetricsConfiguration {
    private Map<String, List<String>> metricNameToRequiredTagNames;
    private boolean enabled;
    private MetricsFilter filter = MetricsFilter.ACCEPT_ALL;

    public MetricsConfiguration() {
        this.reloadConfigs();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public MetricsFilter getFilter() {
        return this.filter;
    }

    public void setFilter(@Nonnull MetricsFilter filter) {
        this.filter = Objects.requireNonNull(filter);
        this.cleanupMetrics(filter);
    }

    @VisibleForTesting
    public void reloadConfigs() {
        this.enabled = Boolean.parseBoolean(System.getProperty("atlassian.metrics.activate", "true"));
        this.metricNameToRequiredTagNames = this.computeRequiredOptionalTags();
    }

    private void cleanupMetrics(MetricsFilter filter) {
        for (MetricStrategy strategy : StrategiesRegistry.getMetricStrategies()) {
            strategy.cleanupMetrics(filter);
        }
    }

    boolean isOptionalTagEnabled(@Nonnull String metricName, @Nullable String tagName) {
        List<String> tags = this.metricNameToRequiredTagNames.get(metricName);
        return tags != null && tags.contains(tagName);
    }

    private Map<String, List<String>> computeRequiredOptionalTags() {
        HashMap<String, List<String>> requiredMetricOptionalTags = new HashMap<String, List<String>>();
        System.getProperties().forEach((BiConsumer<? super Object, ? super Object>)((BiConsumer<Object, Object>)(key, val) -> {
            String propKey = key.toString();
            if (propKey.startsWith("atlassian.metrics.optional.tags.")) {
                String metricName = propKey.substring("atlassian.metrics.optional.tags.".length());
                List tags = Arrays.stream(val.toString().split(",")).map(String::trim).filter(string -> !string.isEmpty()).collect(Collectors.toList());
                if (!tags.isEmpty()) {
                    requiredMetricOptionalTags.put(metricName, tags);
                }
            }
        }));
        return requiredMetricOptionalTags;
    }
}

