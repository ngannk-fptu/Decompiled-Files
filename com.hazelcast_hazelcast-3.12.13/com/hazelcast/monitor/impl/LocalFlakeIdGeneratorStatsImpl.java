/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.monitor.LocalFlakeIdGeneratorStats;
import com.hazelcast.util.Clock;
import com.hazelcast.util.JsonUtil;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class LocalFlakeIdGeneratorStatsImpl
implements LocalFlakeIdGeneratorStats {
    private static final AtomicLongFieldUpdater<LocalFlakeIdGeneratorStatsImpl> BATCH_COUNT = AtomicLongFieldUpdater.newUpdater(LocalFlakeIdGeneratorStatsImpl.class, "batchCount");
    private static final AtomicLongFieldUpdater<LocalFlakeIdGeneratorStatsImpl> ID_COUNT = AtomicLongFieldUpdater.newUpdater(LocalFlakeIdGeneratorStatsImpl.class, "idCount");
    @Probe
    private volatile long creationTime = Clock.currentTimeMillis();
    @Probe
    private volatile long batchCount;
    @Probe
    private volatile long idCount;

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public long getBatchCount() {
        return this.batchCount;
    }

    @Override
    public long getIdCount() {
        return this.idCount;
    }

    public void update(int batchSize) {
        BATCH_COUNT.incrementAndGet(this);
        ID_COUNT.addAndGet(this, batchSize);
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("creationTime", this.creationTime);
        root.add("batchCount", this.batchCount);
        root.add("idCount", this.idCount);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.creationTime = JsonUtil.getLong(json, "creationTime", -1L);
        this.batchCount = JsonUtil.getLong(json, "batchCount", 0L);
        this.idCount = JsonUtil.getLong(json, "idCount", 0L);
    }

    public String toString() {
        return "LocalFlakeIdStatsImpl{creationTime=" + this.creationTime + ", batchCount=" + this.batchCount + ", idCount=" + this.idCount + '}';
    }
}

