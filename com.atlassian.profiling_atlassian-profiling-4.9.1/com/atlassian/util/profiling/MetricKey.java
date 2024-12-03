/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.MetricTag;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

@ParametersAreNonnullByDefault
@Immutable
@Internal
public final class MetricKey {
    private final String metricName;
    private final Set<MetricTag.RequiredMetricTag> tags;

    private MetricKey(String metricName, Set<MetricTag.RequiredMetricTag> tags) {
        this.metricName = Objects.requireNonNull(metricName);
        this.tags = tags;
    }

    @Nonnull
    public String getMetricName() {
        return this.metricName;
    }

    @Nonnull
    public Collection<MetricTag.RequiredMetricTag> getTags() {
        return this.tags;
    }

    public String toString() {
        return this.tags.isEmpty() ? this.metricName : this.metricName + this.getFormattedTags();
    }

    private String getFormattedTags() {
        return this.tags.stream().map(MetricTag::toString).collect(Collectors.joining(",", "[", "]"));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MetricKey)) {
            return false;
        }
        MetricKey metricKey = (MetricKey)o;
        return this.metricName.equals(metricKey.metricName) && this.tags.equals(metricKey.tags);
    }

    public int hashCode() {
        return Objects.hash(this.metricName, this.tags);
    }

    public static MetricKey metricKey(String metricName) {
        return MetricKey.metricKey(metricName, Collections.emptySet());
    }

    public static MetricKey metricKey(String metricName, Collection<MetricTag.RequiredMetricTag> tags) {
        return new MetricKey(metricName, new HashSet<MetricTag.RequiredMetricTag>(tags));
    }

    public static MetricKey metricKey(String metricName, MetricTag.RequiredMetricTag ... tags) {
        return MetricKey.metricKey(metricName, Arrays.asList(tags));
    }
}

