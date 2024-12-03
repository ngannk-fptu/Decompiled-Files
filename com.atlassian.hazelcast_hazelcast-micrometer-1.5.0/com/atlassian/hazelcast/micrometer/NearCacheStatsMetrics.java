/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.monitor.NearCacheStats
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tag
 */
package com.atlassian.hazelcast.micrometer;

import com.hazelcast.monitor.NearCacheStats;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.Collection;

final class NearCacheStatsMetrics {
    private static final String METER_PREFIX = "hazelcast.nearcachestats.";
    private final MeterRegistry meterRegistry;

    NearCacheStatsMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    void bind(NearCacheStats nearCacheStats, Collection<Tag> tags) {
        this.meterRegistry.gauge("hazelcast.nearcachestats.ownedEntryCount", tags, (Object)nearCacheStats, NearCacheStats::getOwnedEntryCount);
        this.meterRegistry.gauge("hazelcast.nearcachestats.invalidations", tags, (Object)nearCacheStats, NearCacheStats::getInvalidations);
        this.meterRegistry.gauge("hazelcast.nearcachestats.expirations", tags, (Object)nearCacheStats, NearCacheStats::getExpirations);
        this.meterRegistry.gauge("hazelcast.nearcachestats.evictions", tags, (Object)nearCacheStats, NearCacheStats::getEvictions);
        this.meterRegistry.gauge("hazelcast.nearcachestats.ratio", tags, (Object)nearCacheStats, NearCacheStats::getRatio);
        this.meterRegistry.gauge("hazelcast.nearcachestats.misses", tags, (Object)nearCacheStats, NearCacheStats::getMisses);
        this.meterRegistry.gauge("hazelcast.nearcachestats.hits", tags, (Object)nearCacheStats, NearCacheStats::getHits);
    }
}

