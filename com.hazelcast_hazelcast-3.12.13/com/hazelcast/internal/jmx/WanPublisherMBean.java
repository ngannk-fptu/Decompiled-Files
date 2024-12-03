/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.monitor.LocalWanPublisherStats;
import com.hazelcast.monitor.LocalWanStats;
import com.hazelcast.wan.WanReplicationService;
import java.util.Map;

@ManagedDescription(value="WanReplicationPublisher")
public class WanPublisherMBean
extends HazelcastMBean<WanReplicationService> {
    private final String wanReplicationName;
    private final String targetGroupName;

    public WanPublisherMBean(WanReplicationService wanReplicationService, String wanReplicationName, String targetGroupName, ManagementService service) {
        super(wanReplicationService, service);
        this.wanReplicationName = wanReplicationName;
        this.targetGroupName = targetGroupName;
        this.objectName = service.createObjectName("WanReplicationPublisher", wanReplicationName + "." + targetGroupName);
    }

    @ManagedAnnotation(value="state")
    @ManagedDescription(value="State of the WAN replication publisher")
    public String getState() {
        Map wanStats = ((WanReplicationService)this.managedObject).getStats();
        if (wanStats == null) {
            return "";
        }
        LocalWanStats wanReplicationStats = (LocalWanStats)wanStats.get(this.wanReplicationName);
        Map<String, LocalWanPublisherStats> wanDelegatingPublisherStats = wanReplicationStats.getLocalWanPublisherStats();
        LocalWanPublisherStats wanPublisherStats = wanDelegatingPublisherStats.get(this.targetGroupName);
        return wanPublisherStats.getPublisherState().name();
    }
}

