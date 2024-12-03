/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.LocalMapStatsProvider;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.spi.StatisticsAwareService;
import java.util.Map;

class MapStatisticsAwareService
implements StatisticsAwareService<LocalMapStats> {
    private final MapServiceContext mapServiceContext;

    MapStatisticsAwareService(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
    }

    @Override
    public Map<String, LocalMapStats> getStats() {
        LocalMapStatsProvider localMapStatsProvider = this.mapServiceContext.getLocalMapStatsProvider();
        return localMapStatsProvider.createAllLocalMapStats();
    }
}

