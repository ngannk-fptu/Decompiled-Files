/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.LongMetric
 *  com.atlassian.vcache.internal.MetricLabel
 *  com.atlassian.vcache.internal.RequestMetrics
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.internal.LongMetric;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.RequestMetrics;
import com.atlassian.vcache.internal.core.metrics.CacheType;
import com.atlassian.vcache.internal.core.metrics.DefaultLongMetric;
import com.atlassian.vcache.internal.core.metrics.MutableRequestMetrics;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class DefaultRequestMetrics
implements MutableRequestMetrics,
RequestMetrics {
    private final Map<CacheType, Map<String, Map<MetricLabel, DefaultLongMetric>>> allMetricsMap = Collections.synchronizedMap(new EnumMap(CacheType.class));

    DefaultRequestMetrics() {
    }

    @Override
    public void record(String cacheName, CacheType cacheType, MetricLabel metricLabel, long sample) {
        Map cacheTypeMetricsMap = this.allMetricsMap.computeIfAbsent(cacheType, k -> new ConcurrentHashMap());
        Map cacheMetricsMap = cacheTypeMetricsMap.computeIfAbsent(cacheName, k -> Collections.synchronizedMap(new EnumMap(MetricLabel.class)));
        DefaultLongMetric metric = cacheMetricsMap.computeIfAbsent(metricLabel, k -> new DefaultLongMetric());
        metric.record(sample);
    }

    public Map<String, Map<MetricLabel, ? extends LongMetric>> allJvmCacheLongMetrics() {
        return new HashMap<String, Map<MetricLabel, ? extends LongMetric>>(this.allMetricsMap.computeIfAbsent(CacheType.JVM, k -> new ConcurrentHashMap()));
    }

    public Map<String, Map<MetricLabel, ? extends LongMetric>> allRequestCacheLongMetrics() {
        return new HashMap<String, Map<MetricLabel, ? extends LongMetric>>(this.allMetricsMap.computeIfAbsent(CacheType.REQUEST, k -> new ConcurrentHashMap()));
    }

    public Map<String, Map<MetricLabel, ? extends LongMetric>> allExternalCacheLongMetrics() {
        return new HashMap<String, Map<MetricLabel, ? extends LongMetric>>(this.allMetricsMap.computeIfAbsent(CacheType.EXTERNAL, k -> new ConcurrentHashMap()));
    }
}

