/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.monitor.impl.LocalMapStatsImpl;

public final class LocalMapStatsUtil {
    private LocalMapStatsUtil() {
    }

    public static void incrementOtherOperationsCount(MapService service, String mapName) {
        MapServiceContext mapServiceContext = service.getMapServiceContext();
        MapContainer mapContainer = mapServiceContext.getMapContainer(mapName);
        if (mapContainer.getMapConfig().isStatisticsEnabled()) {
            LocalMapStatsImpl localMapStats = mapServiceContext.getLocalMapStatsProvider().getLocalMapStatsImpl(mapName);
            localMapStats.incrementOtherOperations();
        }
    }
}

