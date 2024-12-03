/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.core.instrument.FunctionCounter
 *  io.micrometer.core.instrument.Gauge
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tags
 *  io.micrometer.core.instrument.binder.MeterBinder
 *  javax.annotation.Nullable
 *  org.hibernate.SessionFactory
 *  org.hibernate.stat.CacheRegionStatistics
 *  org.hibernate.stat.SecondLevelCacheStatistics
 *  org.hibernate.stat.Statistics
 */
package com.atlassian.confluence.impl.hibernate.metrics;

import com.atlassian.confluence.impl.metrics.CoreMetrics;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.Arrays;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.hibernate.SessionFactory;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;

final class HibernateL2CacheMetrics
implements MeterBinder {
    private static final Boolean INCLUDE_REGION_STATE_GAUGES = Boolean.getBoolean("confluence.micrometer.hibernate.l2cache.stateGauges.enabled");
    private final SecondLevelCacheStatistics statistics;
    private final Tags tags;

    static Stream<MeterBinder> getBinders(SessionFactory sessionFactory) {
        Statistics statistics = sessionFactory.getStatistics();
        if (statistics == null) {
            return Stream.empty();
        }
        return Arrays.stream(statistics.getSecondLevelCacheRegionNames()).map(regionName -> HibernateL2CacheMetrics.createBinder(statistics, regionName));
    }

    private static MeterBinder createBinder(Statistics statistics, String regionName) {
        return new HibernateL2CacheMetrics(statistics.getSecondLevelCacheStatistics(regionName), Tags.of((String)"regionName", (String)regionName));
    }

    private HibernateL2CacheMetrics(@Nullable SecondLevelCacheStatistics statistics, Tags tags) {
        this.statistics = statistics;
        this.tags = tags;
    }

    public void bindTo(MeterRegistry meterRegistry) {
        this.counter(meterRegistry, CoreMetrics.HIBERNATE_L2CACHE_GET, "The number of cacheable entities/collections successfully retrieved from the cache region", CacheRegionStatistics::getHitCount, "result", "hit");
        this.counter(meterRegistry, CoreMetrics.HIBERNATE_L2CACHE_GET, "The number of cacheable entities/collections not found in the cache region", CacheRegionStatistics::getMissCount, "result", "miss");
        this.counter(meterRegistry, CoreMetrics.HIBERNATE_L2CACHE_PUT, "The number of cacheable entities/collections put in to the cache region", CacheRegionStatistics::getPutCount, new String[0]);
        if (INCLUDE_REGION_STATE_GAUGES.booleanValue()) {
            this.bindStateGauges(meterRegistry);
        }
    }

    private void bindStateGauges(MeterRegistry meterRegistry) {
        this.gauge(meterRegistry, CoreMetrics.HIBERNATE_L2CACHE_ELEMENTS, "The count of entries currently contained in the region's in-memory store", CacheRegionStatistics::getElementCountInMemory, "store", "memory");
        this.gauge(meterRegistry, CoreMetrics.HIBERNATE_L2CACHE_ELEMENTS, "The count of entries currently contained in the region's disk store", CacheRegionStatistics::getElementCountOnDisk, "store", "disk");
        this.gauge(meterRegistry, CoreMetrics.HIBERNATE_L2CACHE_SIZE, "The number of bytes is this cache region currently consuming in memory", CacheRegionStatistics::getSizeInMemory, new String[0]);
    }

    private void counter(MeterRegistry registry, CoreMetrics metric, String description, ToDoubleFunction<SecondLevelCacheStatistics> f, String ... extraTags) {
        metric.resolve(name -> FunctionCounter.builder((String)name, (Object)this.statistics, (ToDoubleFunction)f)).tags((Iterable)this.tags).tags(extraTags).description(description).register(registry);
    }

    private void gauge(MeterRegistry registry, CoreMetrics metric, String description, ToDoubleFunction<SecondLevelCacheStatistics> f, String ... extraTags) {
        metric.resolve(name -> Gauge.builder((String)name, (Object)this.statistics, (ToDoubleFunction)f)).tags((Iterable)this.tags).tags(extraTags).description(description).register(registry);
    }
}

