/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.LongMetric
 *  com.atlassian.vcache.internal.MetricLabel
 *  com.atlassian.vcache.internal.RequestMetrics
 *  com.atlassian.vcache.internal.core.metrics.DefaultLongMetric
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.impl.vcache.metrics;

import com.atlassian.confluence.impl.vcache.metrics.CacheStatistics;
import com.atlassian.vcache.internal.LongMetric;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.RequestMetrics;
import com.atlassian.vcache.internal.core.metrics.DefaultLongMetric;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class CacheStatisticsUtils {
    private static final Iterable<MetricLabel> REMOTE_CALLS = ImmutableList.of((Object)MetricLabel.TIMED_GET_CALL, (Object)MetricLabel.TIMED_IDENTIFIED_GET_CALL, (Object)MetricLabel.TIMED_IDENTIFIED_REMOVE_CALL, (Object)MetricLabel.TIMED_IDENTIFIED_REPLACE_CALL, (Object)MetricLabel.TIMED_PUT_CALL, (Object)MetricLabel.TIMED_REMOVE_CALL, (Object)MetricLabel.TIMED_REMOVE_ALL_CALL);
    private static final Iterable<MetricLabel> GENERATOR_CALLS = ImmutableList.of((Object)MetricLabel.TIMED_FACTORY_CALL, (Object)MetricLabel.TIMED_SUPPLIER_CALL);

    private CacheStatisticsUtils() {
    }

    public static Stream<CacheStatistics> collectVCacheStats(RequestMetrics metrics) {
        Stream<CacheStatistics> vCacheJvmStats = CacheStatisticsUtils.convertVCacheMetrics("JVMCache", metrics.allJvmCacheLongMetrics());
        Stream<CacheStatistics> vCacheRequestStats = CacheStatisticsUtils.convertVCacheMetrics("RequestCache", metrics.allRequestCacheLongMetrics());
        Stream<CacheStatistics> vCacheExternalStats = CacheStatisticsUtils.convertVCacheMetrics("ExternalCache", metrics.allExternalCacheLongMetrics());
        return Stream.of(vCacheJvmStats, vCacheRequestStats, vCacheExternalStats).flatMap(Function.identity());
    }

    public static CacheStatistics fromVCacheStatistics(String name, String cacheType, Map<MetricLabel, LongMetric> metrics) {
        DefaultLongMetric emptyMetric = new DefaultLongMetric();
        HashMap<String, LongMetric> otherStats = new HashMap<String, LongMetric>(metrics.size());
        for (Map.Entry<MetricLabel, LongMetric> entry : metrics.entrySet()) {
            otherStats.put(entry.getKey().name().toLowerCase(), entry.getValue());
        }
        return new CacheStatistics.CacheStatisticsBuilder().withName(name).withTags((List<String>)ImmutableList.of((Object)"cache", (Object)"vcache", (Object)cacheType)).withType(cacheType).withHits((int)metrics.getOrDefault(MetricLabel.NUMBER_OF_HITS, (LongMetric)emptyMetric).getSampleCount()).withMisses((int)metrics.getOrDefault(MetricLabel.NUMBER_OF_MISSES, (LongMetric)emptyMetric).getSampleCount()).withLoadTime(CacheStatisticsUtils.mean(metrics.getOrDefault(MetricLabel.TIMED_SUPPLIER_CALL, (LongMetric)emptyMetric))).withGetTime(CacheStatisticsUtils.mean(metrics.getOrDefault(MetricLabel.TIMED_GET_CALL, (LongMetric)emptyMetric))).withPutTime(CacheStatisticsUtils.mean(metrics.getOrDefault(MetricLabel.TIMED_PUT_CALL, (LongMetric)emptyMetric))).withOtherStats(otherStats).build();
    }

    public static Stream<CacheStatistics> convertVCacheMetrics(String cacheType, Map<String, Map<MetricLabel, ? extends LongMetric>> cachesMetrics) {
        return cachesMetrics.entrySet().stream().map(m -> CacheStatisticsUtils.fromVCacheStatistics((String)m.getKey(), cacheType, (Map)m.getValue()));
    }

    private static long mean(LongMetric metric) {
        return metric.getSampleCount() == 0L ? 0L : metric.getSamplesTotal() / metric.getSampleCount();
    }

    public static Map<String, Long> remoteStats(RequestMetrics metrics) {
        long estimatedNetworkCalls = 0L;
        long time = 0L;
        for (Map externalMetrics : metrics.allExternalCacheLongMetrics().values()) {
            LongMetric metric;
            for (MetricLabel remoteCall : REMOTE_CALLS) {
                metric = (LongMetric)externalMetrics.get(remoteCall);
                if (metric == null) continue;
                estimatedNetworkCalls += metric.getSampleCount();
                time += metric.getSamplesTotal();
            }
            for (MetricLabel generatorCall : GENERATOR_CALLS) {
                metric = (LongMetric)externalMetrics.get(generatorCall);
                if (metric == null) continue;
                estimatedNetworkCalls += metric.getSampleCount();
            }
        }
        return ImmutableMap.of((Object)"estimatedNetworkCalls", (Object)estimatedNetworkCalls, (Object)"time", (Object)time);
    }
}

