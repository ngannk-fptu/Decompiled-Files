/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.util.JsonUtil;

public class SlowOperationInvocationDTO
implements JsonSerializable {
    public int id;
    public String operationDetails;
    public long startedAt;
    public int durationMs;

    public SlowOperationInvocationDTO() {
    }

    public SlowOperationInvocationDTO(int id, String operationDetails, long startedAt, int durationMs) {
        this.id = id;
        this.operationDetails = operationDetails;
        this.startedAt = startedAt;
        this.durationMs = durationMs;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("id", this.id);
        root.add("details", this.operationDetails);
        root.add("startedAt", this.startedAt);
        root.add("durationMs", this.durationMs);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.id = JsonUtil.getInt(json, "id");
        this.operationDetails = JsonUtil.getString(json, "details");
        this.startedAt = JsonUtil.getLong(json, "startedAt");
        this.durationMs = JsonUtil.getInt(json, "durationMs");
    }
}

