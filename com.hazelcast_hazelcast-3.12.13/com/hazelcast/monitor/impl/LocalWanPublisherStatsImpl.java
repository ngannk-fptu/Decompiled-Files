/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.config.WanPublisherState;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.monitor.LocalWanPublisherStats;
import com.hazelcast.util.JsonUtil;
import com.hazelcast.wan.WanSyncStats;
import com.hazelcast.wan.impl.DistributedServiceWanEventCounters;
import com.hazelcast.wan.merkletree.ConsistencyCheckResult;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class LocalWanPublisherStatsImpl
implements LocalWanPublisherStats {
    private static final AtomicLongFieldUpdater<LocalWanPublisherStatsImpl> TOTAL_PUBLISH_LATENCY = AtomicLongFieldUpdater.newUpdater(LocalWanPublisherStatsImpl.class, "totalPublishLatency");
    private static final AtomicLongFieldUpdater<LocalWanPublisherStatsImpl> TOTAL_PUBLISHED_EVENT_COUNT = AtomicLongFieldUpdater.newUpdater(LocalWanPublisherStatsImpl.class, "totalPublishedEventCount");
    private volatile boolean connected;
    private volatile WanPublisherState state;
    private volatile int outboundQueueSize;
    private volatile long totalPublishLatency;
    private volatile long totalPublishedEventCount;
    private volatile Map<String, DistributedServiceWanEventCounters.DistributedObjectWanEventCounters> sentMapEventCounter;
    private volatile Map<String, DistributedServiceWanEventCounters.DistributedObjectWanEventCounters> sentCacheEventCounter;
    private volatile Map<String, ConsistencyCheckResult> lastConsistencyCheckResults;
    private volatile Map<String, WanSyncStats> lastSyncStats;

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public int getOutboundQueueSize() {
        return this.outboundQueueSize;
    }

    public void setOutboundQueueSize(int outboundQueueSize) {
        this.outboundQueueSize = outboundQueueSize;
    }

    @Override
    public WanPublisherState getPublisherState() {
        return this.state;
    }

    public void setState(WanPublisherState state) {
        this.state = state;
    }

    @Override
    public long getTotalPublishLatency() {
        return this.totalPublishLatency;
    }

    @Override
    public long getTotalPublishedEventCount() {
        return this.totalPublishedEventCount;
    }

    @Override
    public Map<String, DistributedServiceWanEventCounters.DistributedObjectWanEventCounters> getSentMapEventCounter() {
        return this.sentMapEventCounter;
    }

    public void setSentMapEventCounter(Map<String, DistributedServiceWanEventCounters.DistributedObjectWanEventCounters> sentMapEventCounter) {
        this.sentMapEventCounter = sentMapEventCounter;
    }

    @Override
    public Map<String, DistributedServiceWanEventCounters.DistributedObjectWanEventCounters> getSentCacheEventCounter() {
        return this.sentCacheEventCounter;
    }

    public void setSentCacheEventCounter(Map<String, DistributedServiceWanEventCounters.DistributedObjectWanEventCounters> sentCacheEventCounter) {
        this.sentCacheEventCounter = sentCacheEventCounter;
    }

    public void setLastConsistencyCheckResults(Map<String, ConsistencyCheckResult> lastConsistencyCheckResults) {
        this.lastConsistencyCheckResults = lastConsistencyCheckResults;
    }

    @Override
    public Map<String, ConsistencyCheckResult> getLastConsistencyCheckResults() {
        return this.lastConsistencyCheckResults;
    }

    public void setLastSyncStats(Map<String, WanSyncStats> lastSyncStats) {
        this.lastSyncStats = lastSyncStats;
    }

    @Override
    public Map<String, WanSyncStats> getLastSyncStats() {
        return this.lastSyncStats;
    }

    public void incrementPublishedEventCount(long latency) {
        TOTAL_PUBLISHED_EVENT_COUNT.incrementAndGet(this);
        TOTAL_PUBLISH_LATENCY.addAndGet(this, latency);
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("isConnected", this.connected);
        root.add("totalPublishLatencies", this.totalPublishLatency);
        root.add("totalPublishedEventCount", this.totalPublishedEventCount);
        root.add("outboundQueueSize", this.outboundQueueSize);
        root.add("state", this.state.name());
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.connected = JsonUtil.getBoolean(json, "isConnected", false);
        this.totalPublishLatency = JsonUtil.getLong(json, "totalPublishLatencies", -1L);
        this.totalPublishedEventCount = JsonUtil.getLong(json, "totalPublishedEventCount", -1L);
        this.outboundQueueSize = JsonUtil.getInt(json, "outboundQueueSize", -1);
        this.state = WanPublisherState.valueOf(JsonUtil.getString(json, "state", WanPublisherState.REPLICATING.name()));
    }

    public String toString() {
        return "LocalPublisherStatsImpl{connected=" + this.connected + ", totalPublishLatency=" + this.totalPublishLatency + ", totalPublishedEventCount=" + this.totalPublishedEventCount + ", outboundQueueSize=" + this.outboundQueueSize + ", state=" + (Object)((Object)this.state) + '}';
    }
}

