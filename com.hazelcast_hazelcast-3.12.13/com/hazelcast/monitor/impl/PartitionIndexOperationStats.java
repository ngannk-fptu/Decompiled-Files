/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.monitor.impl.IndexOperationStats;

public class PartitionIndexOperationStats
implements IndexOperationStats {
    private long entryCountDelta;

    @Override
    public long getEntryCountDelta() {
        return this.entryCountDelta;
    }

    @Override
    public long getMemoryCostDelta() {
        return 0L;
    }

    @Override
    public void onEntryAdded(Object replacedValue, Object addedValue) {
        if (replacedValue == null) {
            ++this.entryCountDelta;
        }
    }

    @Override
    public void onEntryRemoved(Object removedValue) {
        if (removedValue != null) {
            --this.entryCountDelta;
        }
    }

    public void reset() {
        this.entryCountDelta = 0L;
    }
}

