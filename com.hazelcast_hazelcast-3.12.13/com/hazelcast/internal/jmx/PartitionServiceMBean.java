/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.Address;
import com.hazelcast.util.MapUtil;
import java.util.Map;

@ManagedDescription(value="HazelcastInstance.PartitionServiceMBean")
public class PartitionServiceMBean
extends HazelcastMBean<InternalPartitionService> {
    private static final int INITIAL_CAPACITY = 3;
    private final HazelcastInstanceImpl hazelcastInstance;

    public PartitionServiceMBean(HazelcastInstanceImpl hazelcastInstance, InternalPartitionService partitionService, ManagementService service) {
        super(partitionService, service);
        this.hazelcastInstance = hazelcastInstance;
        Map<String, String> properties = MapUtil.createHashMap(3);
        properties.put("type", ManagementService.quote("HazelcastInstance.PartitionServiceMBean"));
        properties.put("name", ManagementService.quote(hazelcastInstance.getName()));
        properties.put("instance", ManagementService.quote(hazelcastInstance.getName()));
        this.setObjectName(properties);
    }

    @ManagedAnnotation(value="partitionCount")
    @ManagedDescription(value="Number of partitions")
    public int getPartitionCount() {
        return ((InternalPartitionService)this.managedObject).getPartitionCount();
    }

    @ManagedAnnotation(value="activePartitionCount")
    @ManagedDescription(value="Number of active partitions")
    public int getActivePartitionCount() {
        Address thisAddress = this.hazelcastInstance.getCluster().getLocalMember().getAddress();
        return ((InternalPartitionService)this.managedObject).getMemberPartitionsIfAssigned(thisAddress).size();
    }

    @ManagedAnnotation(value="isClusterSafe")
    @ManagedDescription(value="Is the cluster in a safe state")
    public boolean isClusterSafe() {
        return this.hazelcastInstance.getPartitionService().isClusterSafe();
    }

    @ManagedAnnotation(value="isLocalMemberSafe")
    @ManagedDescription(value="Is the local member safe to shutdown")
    public boolean isLocalMemberSafe() {
        return this.hazelcastInstance.getPartitionService().isLocalMemberSafe();
    }
}

