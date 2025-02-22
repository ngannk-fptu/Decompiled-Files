/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.internal.management.dto.SlowOperationInvocationDTO;
import com.hazelcast.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;

public class SlowOperationDTO
implements JsonSerializable {
    public String operation;
    public String stackTrace;
    public int totalInvocations;
    public List<SlowOperationInvocationDTO> invocations;

    public SlowOperationDTO() {
    }

    public SlowOperationDTO(String operation, String stackTrace, int totalInvocations, List<SlowOperationInvocationDTO> invocations) {
        this.operation = operation;
        this.stackTrace = stackTrace;
        this.totalInvocations = totalInvocations;
        this.invocations = invocations;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("operation", this.operation);
        root.add("stackTrace", this.stackTrace);
        root.add("totalInvocations", this.totalInvocations);
        JsonArray invocationArray = new JsonArray();
        for (SlowOperationInvocationDTO invocation : this.invocations) {
            JsonObject json = invocation.toJson();
            if (json == null) continue;
            invocationArray.add(json);
        }
        root.add("invocations", invocationArray);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.operation = JsonUtil.getString(json, "operation");
        this.stackTrace = JsonUtil.getString(json, "stackTrace");
        this.totalInvocations = JsonUtil.getInt(json, "totalInvocations");
        this.invocations = new ArrayList<SlowOperationInvocationDTO>();
        for (JsonValue jsonValue : JsonUtil.getArray(json, "invocations")) {
            SlowOperationInvocationDTO slowOperationInvocationDTO = new SlowOperationInvocationDTO();
            slowOperationInvocationDTO.fromJson(jsonValue.asObject());
            this.invocations.add(slowOperationInvocationDTO);
        }
    }
}

