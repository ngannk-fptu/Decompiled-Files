/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.util.JsonUtil;
import com.hazelcast.util.executor.ManagedExecutorService;

public class ManagedExecutorDTO
implements JsonSerializable {
    public String name;
    public int queueSize;
    public int poolSize;
    public int remainingQueueCapacity;
    public int maximumPoolSize;
    public boolean isTerminated;
    public long completedTaskCount;

    public ManagedExecutorDTO() {
    }

    public ManagedExecutorDTO(ManagedExecutorService executorService) {
        this.name = executorService.getName();
        this.queueSize = executorService.getQueueSize();
        this.poolSize = executorService.getPoolSize();
        this.remainingQueueCapacity = executorService.getRemainingQueueCapacity();
        this.maximumPoolSize = executorService.getMaximumPoolSize();
        this.isTerminated = executorService.isTerminated();
        this.completedTaskCount = executorService.getCompletedTaskCount();
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("name", this.name);
        root.add("queueSize", this.queueSize);
        root.add("poolSize", this.poolSize);
        root.add("remainingQueueCapacity", this.remainingQueueCapacity);
        root.add("maximumPoolSize", this.maximumPoolSize);
        root.add("isTerminated", this.isTerminated);
        root.add("completedTaskCount", this.completedTaskCount);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.name = JsonUtil.getString(json, "name");
        this.queueSize = JsonUtil.getInt(json, "queueSize");
        this.poolSize = JsonUtil.getInt(json, "poolSize");
        this.remainingQueueCapacity = JsonUtil.getInt(json, "remainingQueueCapacity");
        this.maximumPoolSize = JsonUtil.getInt(json, "maximumPoolSize");
        this.isTerminated = JsonUtil.getBoolean(json, "isTerminated");
        this.completedTaskCount = JsonUtil.getLong(json, "completedTaskCount");
    }
}

