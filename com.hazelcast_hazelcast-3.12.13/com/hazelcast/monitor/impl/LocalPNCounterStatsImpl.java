/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.monitor.LocalPNCounterStats;
import com.hazelcast.util.Clock;
import com.hazelcast.util.JsonUtil;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class LocalPNCounterStatsImpl
implements LocalPNCounterStats {
    private static final AtomicLongFieldUpdater<LocalPNCounterStatsImpl> TOTAL_INCREMENT_OPERATION_COUNT = AtomicLongFieldUpdater.newUpdater(LocalPNCounterStatsImpl.class, "totalIncrementOperationCount");
    private static final AtomicLongFieldUpdater<LocalPNCounterStatsImpl> TOTAL_DECREMENT_OPERATION_COUNT = AtomicLongFieldUpdater.newUpdater(LocalPNCounterStatsImpl.class, "totalDecrementOperationCount");
    @Probe
    private long creationTime = Clock.currentTimeMillis();
    @Probe
    private volatile long value;
    @Probe
    private volatile long totalIncrementOperationCount;
    @Probe
    private volatile long totalDecrementOperationCount;

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public long getValue() {
        return this.value;
    }

    @Override
    public long getTotalIncrementOperationCount() {
        return this.totalIncrementOperationCount;
    }

    @Override
    public long getTotalDecrementOperationCount() {
        return this.totalDecrementOperationCount;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void incrementIncrementOperationCount() {
        TOTAL_INCREMENT_OPERATION_COUNT.incrementAndGet(this);
    }

    public void incrementDecrementOperationCount() {
        TOTAL_DECREMENT_OPERATION_COUNT.incrementAndGet(this);
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("creationTime", this.creationTime);
        root.add("value", this.value);
        root.add("totalIncrementOperationCount", this.totalIncrementOperationCount);
        root.add("totalDecrementOperationCount", this.totalDecrementOperationCount);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.creationTime = JsonUtil.getLong(json, "creationTime", -1L);
        this.value = JsonUtil.getLong(json, "value", -1L);
        this.totalIncrementOperationCount = JsonUtil.getLong(json, "totalIncrementOperationCount", -1L);
        this.totalDecrementOperationCount = JsonUtil.getLong(json, "totalDecrementOperationCount", -1L);
    }

    public String toString() {
        return "LocalPNCounterStatsImpl{creationTime=" + this.creationTime + ", value=" + this.value + ", totalIncrementOperationCount=" + this.totalIncrementOperationCount + ", totalDecrementOperationCount=" + this.totalDecrementOperationCount + '}';
    }
}

