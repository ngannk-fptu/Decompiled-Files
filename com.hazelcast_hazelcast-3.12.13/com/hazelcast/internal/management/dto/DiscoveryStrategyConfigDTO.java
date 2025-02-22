/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.util.JsonUtil;
import com.hazelcast.util.MapUtil;

public class DiscoveryStrategyConfigDTO
implements JsonSerializable {
    private DiscoveryStrategyConfig config;

    public DiscoveryStrategyConfigDTO() {
    }

    public DiscoveryStrategyConfigDTO(DiscoveryStrategyConfig config) {
        this.config = config;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
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
        this.config = new DiscoveryStrategyConfig();
        JsonValue className = json.get("className");
        if (className != null && !className.isNull()) {
            this.config.setClassName(className.asString());
        }
        this.config.setProperties(JsonUtil.fromJsonObject((JsonObject)json.get("properties")));
    }

    public DiscoveryStrategyConfig getConfig() {
        return this.config;
    }
}

