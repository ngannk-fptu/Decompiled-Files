/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan;

import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.monitor.LocalWanStats;
import com.hazelcast.monitor.WanSyncState;
import com.hazelcast.spi.CoreService;
import com.hazelcast.spi.StatisticsAwareService;
import com.hazelcast.wan.AddWanConfigResult;
import com.hazelcast.wan.WanReplicationPublisher;
import com.hazelcast.wan.impl.DistributedServiceWanEventCounters;

public interface WanReplicationService
extends CoreService,
StatisticsAwareService<LocalWanStats> {
    public static final String SERVICE_NAME = "hz:core:wanReplicationService";

    public WanReplicationPublisher getWanReplicationPublisher(String var1);

    public void shutdown();

    public void pause(String var1, String var2);

    public void stop(String var1, String var2);

    public void resume(String var1, String var2);

    public void checkWanReplicationQueues(String var1);

    public void syncMap(String var1, String var2, String var3);

    public void syncAllMaps(String var1, String var2);

    public void consistencyCheck(String var1, String var2, String var3);

    public void clearQueues(String var1, String var2);

    public void addWanReplicationConfigLocally(WanReplicationConfig var1);

    public AddWanConfigResult addWanReplicationConfig(WanReplicationConfig var1);

    public WanSyncState getWanSyncState();

    public DistributedServiceWanEventCounters getReceivedEventCounters(String var1);

    public DistributedServiceWanEventCounters getSentEventCounters(String var1, String var2, String var3);

    public void removeWanEventCounters(String var1, String var2);
}

