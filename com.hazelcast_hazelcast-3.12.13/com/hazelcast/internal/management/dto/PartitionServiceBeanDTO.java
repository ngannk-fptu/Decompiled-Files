/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.Address;
import com.hazelcast.util.JsonUtil;

public class PartitionServiceBeanDTO
implements JsonSerializable {
    private int partitionCount;
    private int activePartitionCount;

    public PartitionServiceBeanDTO() {
    }

    public PartitionServiceBeanDTO(InternalPartitionService partitionService, HazelcastInstanceImpl hazelcastInstance) {
        Address address = hazelcastInstance.getCluster().getLocalMember().getAddress();
        this.partitionCount = partitionService.getPartitionCount();
        this.activePartitionCount = partitionService.getMemberPartitionsIfAssigned(address).size();
    }

    public int getPartitionCount() {
        return this.partitionCount;
    }

    public void setPartitionCount(int partitionCount) {
        this.partitionCount = partitionCount;
    }

    public int getActivePartitionCount() {
        return this.activePartitionCount;
    }

    public void setActivePartitionCount(int activePartitionCount) {
        this.activePartitionCount = activePartitionCount;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("partitionCount", this.partitionCount);
        root.add("activePartitionCount", this.activePartitionCount);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.partitionCount = JsonUtil.getInt(json, "partitionCount", -1);
        this.activePartitionCount = JsonUtil.getInt(json, "activePartitionCount", -1);
    }
}

