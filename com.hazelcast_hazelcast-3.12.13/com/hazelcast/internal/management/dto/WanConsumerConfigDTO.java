/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.config.WanConsumerConfig;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.util.JsonUtil;
import com.hazelcast.util.MapUtil;

public class WanConsumerConfigDTO
implements JsonSerializable {
    private WanConsumerConfig config;

    public WanConsumerConfigDTO() {
    }

    public WanConsumerConfigDTO(WanConsumerConfig config) {
        this.config = config;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject().add("persistWanReplicatedData", this.config.isPersistWanReplicatedData());
        if (this.config.getClassName() != null) {
            root.add("className", this.config.getClassName());
        }
        if (!MapUtil.isNullOrEmpty(this.config.getProperties())) {
            root.add("properties", JsonUtil.toJsonObject(this.config.getProperties()));
        }
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        JsonValue className;
        this.config = new WanConsumerConfig();
        JsonValue persistWanReplicatedData = json.get("persistWanReplicatedData");
        if (persistWanReplicatedData != null && !persistWanReplicatedData.isNull()) {
            this.config.setPersistWanReplicatedData(persistWanReplicatedData.asBoolean());
        }
        if ((className = json.get("className")) != null && !className.isNull()) {
            this.config.setClassName(className.asString());
        }
        this.config.setProperties(JsonUtil.fromJsonObject((JsonObject)json.get("properties")));
    }

    public WanConsumerConfig getConfig() {
        return this.config;
    }
}

