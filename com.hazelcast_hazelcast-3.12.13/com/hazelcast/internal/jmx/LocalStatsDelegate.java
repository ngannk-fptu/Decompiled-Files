/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.internal.jmx.suppliers.StatsSupplier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class LocalStatsDelegate<T> {
    private volatile T localStats;
    private final StatsSupplier<T> supplier;
    private final long intervalMs;
    private final AtomicLong lastUpdated = new AtomicLong(0L);
    private final AtomicBoolean inProgress = new AtomicBoolean(false);

    public LocalStatsDelegate(StatsSupplier<T> supplier, long intervalSec) {
        this.supplier = supplier;
        this.intervalMs = TimeUnit.SECONDS.toMillis(intervalSec);
        this.localStats = supplier.getEmpty();
    }

    public T getLocalStats() {
        long delta = System.currentTimeMillis() - this.lastUpdated.get();
        if (delta > this.intervalMs && this.inProgress.compareAndSet(false, true)) {
            try {
                this.localStats = this.supplier.get();
                this.lastUpdated.set(System.currentTimeMillis());
            }
            finally {
                this.inProgress.set(false);
            }
        }
        return this.localStats;
    }
}

