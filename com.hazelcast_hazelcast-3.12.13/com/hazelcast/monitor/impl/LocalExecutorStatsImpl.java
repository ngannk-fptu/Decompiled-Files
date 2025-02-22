/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.monitor.LocalExecutorStats;
import com.hazelcast.util.Clock;
import com.hazelcast.util.JsonUtil;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class LocalExecutorStatsImpl
implements LocalExecutorStats {
    private static final AtomicLongFieldUpdater<LocalExecutorStatsImpl> PENDING = AtomicLongFieldUpdater.newUpdater(LocalExecutorStatsImpl.class, "pending");
    private static final AtomicLongFieldUpdater<LocalExecutorStatsImpl> STARTED = AtomicLongFieldUpdater.newUpdater(LocalExecutorStatsImpl.class, "started");
    private static final AtomicLongFieldUpdater<LocalExecutorStatsImpl> COMPLETED = AtomicLongFieldUpdater.newUpdater(LocalExecutorStatsImpl.class, "completed");
    private static final AtomicLongFieldUpdater<LocalExecutorStatsImpl> CANCELLED = AtomicLongFieldUpdater.newUpdater(LocalExecutorStatsImpl.class, "cancelled");
    private static final AtomicLongFieldUpdater<LocalExecutorStatsImpl> TOTAL_START_LATENCY = AtomicLongFieldUpdater.newUpdater(LocalExecutorStatsImpl.class, "totalStartLatency");
    private static final AtomicLongFieldUpdater<LocalExecutorStatsImpl> TOTAL_EXECUTION_TIME = AtomicLongFieldUpdater.newUpdater(LocalExecutorStatsImpl.class, "totalExecutionTime");
    private long creationTime = Clock.currentTimeMillis();
    @Probe
    private volatile long pending;
    @Probe
    private volatile long started;
    @Probe
    private volatile long completed;
    @Probe
    private volatile long cancelled;
    @Probe
    private volatile long totalStartLatency;
    @Probe
    private volatile long totalExecutionTime;

    public void startPending() {
        PENDING.incrementAndGet(this);
    }

    public void startExecution(long elapsed) {
        TOTAL_START_LATENCY.addAndGet(this, elapsed);
        STARTED.incrementAndGet(this);
        PENDING.decrementAndGet(this);
    }

    public void finishExecution(long elapsed) {
        TOTAL_EXECUTION_TIME.addAndGet(this, elapsed);
        COMPLETED.incrementAndGet(this);
    }

    public void rejectExecution() {
        PENDING.decrementAndGet(this);
    }

    public void cancelExecution() {
        CANCELLED.incrementAndGet(this);
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public long getPendingTaskCount() {
        return this.pending;
    }

    @Override
    public long getStartedTaskCount() {
        return this.started;
    }

    @Override
    public long getCompletedTaskCount() {
        return this.completed;
    }

    @Override
    public long getCancelledTaskCount() {
        return this.cancelled;
    }

    @Override
    public long getTotalStartLatency() {
        return this.totalStartLatency;
    }

    @Override
    public long getTotalExecutionLatency() {
        return this.totalExecutionTime;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("creationTime", this.creationTime);
        root.add("pending", this.pending);
        root.add("started", this.started);
        root.add("completed", this.completed);
        root.add("cancelled", this.cancelled);
        root.add("totalStartLatency", this.totalStartLatency);
        root.add("totalExecutionTime", this.totalExecutionTime);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.creationTime = JsonUtil.getLong(json, "creationTime", -1L);
        PENDING.set(this, JsonUtil.getLong(json, "pending", -1L));
        STARTED.set(this, JsonUtil.getLong(json, "started", -1L));
        COMPLETED.set(this, JsonUtil.getLong(json, "completed", -1L));
        CANCELLED.set(this, JsonUtil.getLong(json, "cancelled", -1L));
        TOTAL_START_LATENCY.set(this, JsonUtil.getLong(json, "totalStartLatency", -1L));
        TOTAL_EXECUTION_TIME.set(this, JsonUtil.getLong(json, "totalExecutionTime", -1L));
    }

    public String toString() {
        return "LocalExecutorStatsImpl{creationTime=" + this.creationTime + ", pending=" + this.pending + ", started=" + this.started + ", completed=" + this.completed + ", cancelled=" + this.cancelled + ", totalStartLatency=" + this.totalStartLatency + ", totalExecutionTime=" + this.totalExecutionTime + '}';
    }
}

