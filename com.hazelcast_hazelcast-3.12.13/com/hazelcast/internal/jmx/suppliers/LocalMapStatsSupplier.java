/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx.suppliers;

import com.hazelcast.core.IMap;
import com.hazelcast.internal.jmx.suppliers.StatsSupplier;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.monitor.impl.LocalMapStatsImpl;

public class LocalMapStatsSupplier
implements StatsSupplier<LocalMapStats> {
    private final IMap map;

    public LocalMapStatsSupplier(IMap map) {
        this.map = map;
    }

    @Override
    public LocalMapStats getEmpty() {
        return new LocalMapStatsImpl();
    }

    @Override
    public LocalMapStats get() {
        return this.map.getLocalMapStats();
    }
}

