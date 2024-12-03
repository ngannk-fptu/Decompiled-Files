/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.internal.management.dto.ClientEndPointDTO;
import com.hazelcast.internal.management.dto.ClusterHotRestartStatusDTO;
import com.hazelcast.internal.management.dto.MXBeansDTO;
import com.hazelcast.monitor.HotRestartState;
import com.hazelcast.monitor.LocalCacheStats;
import com.hazelcast.monitor.LocalExecutorStats;
import com.hazelcast.monitor.LocalFlakeIdGeneratorStats;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.monitor.LocalMemoryStats;
import com.hazelcast.monitor.LocalMultiMapStats;
import com.hazelcast.monitor.LocalOperationStats;
import com.hazelcast.monitor.LocalPNCounterStats;
import com.hazelcast.monitor.LocalQueueStats;
import com.hazelcast.monitor.LocalReplicatedMapStats;
import com.hazelcast.monitor.LocalTopicStats;
import com.hazelcast.monitor.LocalWanStats;
import com.hazelcast.monitor.MemberPartitionState;
import com.hazelcast.monitor.NodeState;
import com.hazelcast.monitor.WanSyncState;
import java.util.Collection;
import java.util.Map;

public interface MemberState
extends JsonSerializable {
    public String getAddress();

    public String getUuid();

    public String getCpMemberUuid();

    public Map<String, Long> getRuntimeProps();

    public LocalMapStats getLocalMapStats(String var1);

    public LocalMultiMapStats getLocalMultiMapStats(String var1);

    public LocalQueueStats getLocalQueueStats(String var1);

    public LocalTopicStats getLocalTopicStats(String var1);

    public LocalTopicStats getReliableLocalTopicStats(String var1);

    public LocalPNCounterStats getLocalPNCounterStats(String var1);

    public LocalReplicatedMapStats getLocalReplicatedMapStats(String var1);

    public LocalExecutorStats getLocalExecutorStats(String var1);

    public LocalCacheStats getLocalCacheStats(String var1);

    public LocalWanStats getLocalWanStats(String var1);

    public LocalFlakeIdGeneratorStats getLocalFlakeIdGeneratorStats(String var1);

    public Collection<ClientEndPointDTO> getClients();

    public MXBeansDTO getMXBeans();

    public LocalMemoryStats getLocalMemoryStats();

    public LocalOperationStats getOperationStats();

    public MemberPartitionState getMemberPartitionState();

    public NodeState getNodeState();

    public HotRestartState getHotRestartState();

    public ClusterHotRestartStatusDTO getClusterHotRestartStatus();

    public WanSyncState getWanSyncState();
}

