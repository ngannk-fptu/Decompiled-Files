/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx.suppliers;

import com.hazelcast.core.IQueue;
import com.hazelcast.internal.jmx.suppliers.StatsSupplier;
import com.hazelcast.monitor.LocalQueueStats;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;

public class LocalQueueStatsSupplier
implements StatsSupplier<LocalQueueStats> {
    private final IQueue queue;

    public LocalQueueStatsSupplier(IQueue queue) {
        this.queue = queue;
    }

    @Override
    public LocalQueueStats getEmpty() {
        return new LocalQueueStatsImpl();
    }

    @Override
    public LocalQueueStats get() {
        return this.queue.getLocalQueueStats();
    }
}

