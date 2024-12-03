/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.monitor.impl.IndexOperationStats;
import com.hazelcast.query.impl.IndexHeapMemoryCostUtil;

public class GlobalIndexOperationStats
implements IndexOperationStats {
    private long entryCountDelta;
    private long memoryCostDelta;

    @Override
    public long getEntryCountDelta() {
        return this.entryCountDelta;
    }

    @Override
    public long getMemoryCostDelta() {
        return this.memoryCostDelta;
    }

    @Override
    public void onEntryAdded(Object replacedValue, Object addedValue) {
        this.memoryCostDelta += IndexHeapMemoryCostUtil.estimateValueCost(addedValue);
        if (replacedValue == null) {
            ++this.entryCountDelta;
        } else {
            this.memoryCostDelta -= IndexHeapMemoryCostUtil.estimateValueCost(replacedValue);
        }
    }

    @Override
    public void onEntryRemoved(Object removedValue) {
        if (removedValue != null) {
            --this.entryCountDelta;
            this.memoryCostDelta -= IndexHeapMemoryCostUtil.estimateValueCost(removedValue);
        }
    }
}

