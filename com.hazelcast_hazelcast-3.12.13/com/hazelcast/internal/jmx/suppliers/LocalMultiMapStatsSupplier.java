/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx.suppliers;

import com.hazelcast.core.MultiMap;
import com.hazelcast.internal.jmx.suppliers.StatsSupplier;
import com.hazelcast.monitor.LocalMultiMapStats;
import com.hazelcast.monitor.impl.LocalMultiMapStatsImpl;

public class LocalMultiMapStatsSupplier
implements StatsSupplier<LocalMultiMapStats> {
    private final MultiMap multiMap;

    public LocalMultiMapStatsSupplier(MultiMap multiMap) {
        this.multiMap = multiMap;
    }

    @Override
    public LocalMultiMapStats getEmpty() {
        return new LocalMultiMapStatsImpl();
    }

    @Override
    public LocalMultiMapStats get() {
        return this.multiMap.getLocalMultiMapStats();
    }
}

