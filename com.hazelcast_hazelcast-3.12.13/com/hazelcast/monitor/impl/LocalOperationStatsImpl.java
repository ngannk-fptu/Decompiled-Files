/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.instance.Node;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.dto.SlowOperationDTO;
import com.hazelcast.monitor.LocalOperationStats;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.Clock;
import com.hazelcast.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;

public class LocalOperationStatsImpl
implements LocalOperationStats {
    private long maxVisibleSlowOperationCount;
    private List<SlowOperationDTO> slowOperations;
    private long creationTime;

    public LocalOperationStatsImpl() {
        this.maxVisibleSlowOperationCount = Long.MAX_VALUE;
        this.slowOperations = new ArrayList<SlowOperationDTO>();
        this.creationTime = Clock.currentTimeMillis();
    }

    public LocalOperationStatsImpl(Node node) {
        this.maxVisibleSlowOperationCount = node.getProperties().getInteger(GroupProperty.MC_MAX_VISIBLE_SLOW_OPERATION_COUNT);
        this.slowOperations = node.nodeEngine.getOperationService().getSlowOperationDTOs();
        this.creationTime = Clock.currentTimeMillis();
    }

    public long getMaxVisibleSlowOperationCount() {
        return this.maxVisibleSlowOperationCount;
    }

    public List<SlowOperationDTO> getSlowOperations() {
        return this.slowOperations;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("maxVisibleSlowOperationCount", this.maxVisibleSlowOperationCount);
        JsonArray slowOperationArray = new JsonArray();
        int logCount = 0;
        for (SlowOperationDTO slowOperation : this.slowOperations) {
            if ((long)logCount++ >= this.maxVisibleSlowOperationCount) continue;
            slowOperationArray.add(slowOperation.toJson());
        }
        root.add("slowOperations", slowOperationArray);
        root.add("creationTime", this.creationTime);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.maxVisibleSlowOperationCount = JsonUtil.getLong(json, "maxVisibleSlowOperationCount", Long.MAX_VALUE);
        for (JsonValue jsonValue : JsonUtil.getArray(json, "slowOperations")) {
            SlowOperationDTO slowOperationDTO = new SlowOperationDTO();
            slowOperationDTO.fromJson(jsonValue.asObject());
            this.slowOperations.add(slowOperationDTO);
        }
        this.creationTime = JsonUtil.getLong(json, "creationTime", -1L);
    }

    public String toString() {
        return "LocalOperationStatsImpl{maxVisibleSlowOperationCount=" + this.maxVisibleSlowOperationCount + ", slowOperations=" + this.slowOperations + ", creationTime=" + this.creationTime + '}';
    }
}

