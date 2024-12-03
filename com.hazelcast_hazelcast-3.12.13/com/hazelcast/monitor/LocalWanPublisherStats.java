/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.config.WanPublisherState;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.wan.WanSyncStats;
import com.hazelcast.wan.impl.DistributedServiceWanEventCounters;
import com.hazelcast.wan.merkletree.ConsistencyCheckResult;
import java.util.Map;

public interface LocalWanPublisherStats
extends JsonSerializable {
    public boolean isConnected();

    public long getTotalPublishedEventCount();

    public long getTotalPublishLatency();

    public int getOutboundQueueSize();

    public WanPublisherState getPublisherState();

    public Map<String, DistributedServiceWanEventCounters.DistributedObjectWanEventCounters> getSentMapEventCounter();

    public Map<String, DistributedServiceWanEventCounters.DistributedObjectWanEventCounters> getSentCacheEventCounter();

    public Map<String, ConsistencyCheckResult> getLastConsistencyCheckResults();

    public Map<String, WanSyncStats> getLastSyncStats();
}

