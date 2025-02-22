/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.config.ConsistencyCheckStrategy;
import com.hazelcast.config.WanSyncConfig;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;

public class WanSyncConfigDTO
implements JsonSerializable {
    private WanSyncConfig config;

    public WanSyncConfigDTO() {
    }

    public WanSyncConfigDTO(WanSyncConfig config) {
        this.config = config;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        if (this.config.getConsistencyCheckStrategy() != null) {
            root.add("consistencyCheckStrategy", this.config.getConsistencyCheckStrategy().getId());
        }
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.config = new WanSyncConfig();
        JsonValue consistencyCheckStrategy = json.get("consistencyCheckStrategy");
        if (consistencyCheckStrategy != null && !consistencyCheckStrategy.isNull()) {
            this.config.setConsistencyCheckStrategy(ConsistencyCheckStrategy.getById((byte)consistencyCheckStrategy.asInt()));
        }
    }

    public WanSyncConfig getConfig() {
        return this.config;
    }
}

