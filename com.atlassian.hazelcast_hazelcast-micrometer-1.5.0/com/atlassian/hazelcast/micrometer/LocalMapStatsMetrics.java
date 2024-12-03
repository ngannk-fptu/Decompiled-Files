/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.monitor.LocalMapStats
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tag
 */
package com.atlassian.hazelcast.micrometer;

import com.hazelcast.monitor.LocalMapStats;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.Collection;

final class LocalMapStatsMetrics {
    private static final String METER_PREFIX = "hazelcast.localmapstats.";
    private final MeterRegistry meterRegistry;

    LocalMapStatsMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    void bind(LocalMapStats mapStats, Collection<Tag> tags) {
        this.meterRegistry.gauge("hazelcast.localmapstats.ownedEntryCount", tags, (Object)mapStats, LocalMapStats::getOwnedEntryCount);
        this.meterRegistry.gauge("hazelcast.localmapstats.backupCount", tags, (Object)mapStats, LocalMapStats::getBackupCount);
        this.meterRegistry.gauge("hazelcast.localmapstats.backupEntryCount", tags, (Object)mapStats, LocalMapStats::getBackupEntryCount);
        this.meterRegistry.gauge("hazelcast.localmapstats.queryCount", tags, (Object)mapStats, LocalMapStats::getQueryCount);
        this.meterRegistry.gauge("hazelcast.localmapstats.dirtyEntryCount", tags, (Object)mapStats, LocalMapStats::getDirtyEntryCount);
        this.meterRegistry.gauge("hazelcast.localmapstats.eventOperationCount", tags, (Object)mapStats, LocalMapStats::getEventOperationCount);
        this.meterRegistry.gauge("hazelcast.localmapstats.getOperationCount", tags, (Object)mapStats, LocalMapStats::getGetOperationCount);
        this.meterRegistry.gauge("hazelcast.localmapstats.indexedQueryCount", tags, (Object)mapStats, LocalMapStats::getIndexedQueryCount);
        this.meterRegistry.gauge("hazelcast.localmapstats.lockedEntryCount", tags, (Object)mapStats, LocalMapStats::getLockedEntryCount);
        this.meterRegistry.gauge("hazelcast.localmapstats.otherOperationCount", tags, (Object)mapStats, LocalMapStats::getOtherOperationCount);
        this.meterRegistry.gauge("hazelcast.localmapstats.putOperationCount", tags, (Object)mapStats, LocalMapStats::getPutOperationCount);
        this.meterRegistry.gauge("hazelcast.localmapstats.removeOperationCount", tags, (Object)mapStats, LocalMapStats::getRemoveOperationCount);
        this.meterRegistry.gauge("hazelcast.localmapstats.maxGetLatency", tags, (Object)mapStats, LocalMapStats::getMaxGetLatency);
        this.meterRegistry.gauge("hazelcast.localmapstats.maxPutLatency", tags, (Object)mapStats, LocalMapStats::getMaxPutLatency);
        this.meterRegistry.gauge("hazelcast.localmapstats.maxRemoveLatency", tags, (Object)mapStats, LocalMapStats::getMaxRemoveLatency);
        this.meterRegistry.gauge("hazelcast.localmapstats.totalGetLatency", tags, (Object)mapStats, LocalMapStats::getTotalGetLatency);
        this.meterRegistry.gauge("hazelcast.localmapstats.totalPutLatency", tags, (Object)mapStats, LocalMapStats::getTotalPutLatency);
        this.meterRegistry.gauge("hazelcast.localmapstats.totalRemoveLatency", tags, (Object)mapStats, LocalMapStats::getTotalRemoveLatency);
        this.meterRegistry.gauge("hazelcast.localmapstats.total", tags, (Object)mapStats, LocalMapStats::total);
        this.meterRegistry.gauge("hazelcast.localmapstats.hits", tags, (Object)mapStats, LocalMapStats::getHits);
    }
}

