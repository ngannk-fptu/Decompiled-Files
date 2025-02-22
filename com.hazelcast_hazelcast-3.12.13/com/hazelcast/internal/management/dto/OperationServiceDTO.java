/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.util.JsonUtil;

public class OperationServiceDTO
implements JsonSerializable {
    public int responseQueueSize;
    public int operationExecutorQueueSize;
    public int runningOperationsCount;
    public int remoteOperationCount;
    public long executedOperationCount;
    public long operationThreadCount;

    public OperationServiceDTO() {
    }

    public OperationServiceDTO(InternalOperationService os) {
        this.responseQueueSize = os.getResponseQueueSize();
        this.operationExecutorQueueSize = os.getOperationExecutorQueueSize();
        this.runningOperationsCount = os.getRunningOperationsCount();
        this.remoteOperationCount = os.getRemoteOperationsCount();
        this.executedOperationCount = os.getExecutedOperationCount();
        this.operationThreadCount = os.getPartitionThreadCount();
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("responseQueueSize", this.responseQueueSize);
        root.add("operationExecutorQueueSize", this.operationExecutorQueueSize);
        root.add("runningOperationsCount", this.runningOperationsCount);
        root.add("remoteOperationCount", this.remoteOperationCount);
        root.add("executedOperationCount", this.executedOperationCount);
        root.add("operationThreadCount", this.operationThreadCount);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.responseQueueSize = JsonUtil.getInt(json, "responseQueueSize", -1);
        this.operationExecutorQueueSize = JsonUtil.getInt(json, "operationExecutorQueueSize", -1);
        this.runningOperationsCount = JsonUtil.getInt(json, "runningOperationsCount", -1);
        this.remoteOperationCount = JsonUtil.getInt(json, "remoteOperationCount", -1);
        this.executedOperationCount = JsonUtil.getLong(json, "executedOperationCount", -1L);
        this.operationThreadCount = JsonUtil.getLong(json, "operationThreadCount", -1L);
    }
}

