/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.content.render.xhtml.analytics;

import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulationKey;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public class MarshallerMetrics {
    private final MarshallerMetricsAccumulationKey accumulationKey;
    private final int executionCount;
    private final long cumulativeExecutionTimeNanos;
    private final long cumulativeStreamingTimeNanos;
    private final Map<String, Long> customMetrics;

    public MarshallerMetrics(MarshallerMetricsAccumulationKey accumulationKey, int executionCount, long cumulativeExecutionTimeNanos, long cumulativeStreamingTimeNanos, Map<String, Long> customMetrics) {
        this.accumulationKey = (MarshallerMetricsAccumulationKey)Preconditions.checkNotNull((Object)accumulationKey);
        this.executionCount = executionCount;
        this.cumulativeExecutionTimeNanos = cumulativeExecutionTimeNanos;
        this.cumulativeStreamingTimeNanos = cumulativeStreamingTimeNanos;
        this.customMetrics = ImmutableMap.copyOf(customMetrics);
        Preconditions.checkArgument((executionCount >= 0 ? 1 : 0) != 0, (String)"executionCount [%s] is not >= 0", (int)executionCount);
        Preconditions.checkArgument((cumulativeExecutionTimeNanos >= 0L ? 1 : 0) != 0, (String)"cumulativeExecutionTimeNanos [%s] is not >= 0", (long)cumulativeExecutionTimeNanos);
        Preconditions.checkArgument((cumulativeStreamingTimeNanos >= 0L ? 1 : 0) != 0, (String)"cumulativeStreamingTimeNanos [%s] is not >= 0", (long)cumulativeStreamingTimeNanos);
    }

    public @NonNull MarshallerMetricsAccumulationKey getAccumulationKey() {
        return this.accumulationKey;
    }

    public int getExecutionCount() {
        return this.executionCount;
    }

    public long getCumulativeExecutionTimeNanos() {
        return this.cumulativeExecutionTimeNanos;
    }

    public long getCumulativeStreamingTimeNanos() {
        return this.cumulativeStreamingTimeNanos;
    }

    public @NonNull Map<String, Long> getCustomMetrics() {
        return this.customMetrics;
    }
}

