/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.internal.management.dto.DiscoveryStrategyConfigDTO;
import java.util.Collection;

public class DiscoveryConfigDTO
implements JsonSerializable {
    private DiscoveryConfig config;

    public DiscoveryConfigDTO() {
    }

    public DiscoveryConfigDTO(DiscoveryConfig config) {
        this.config = config;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject().add("nodeFilterClass", this.config.getNodeFilterClass());
        JsonArray strategies = new JsonArray();
        for (DiscoveryStrategyConfig strategyConfig : this.config.getDiscoveryStrategyConfigs()) {
            DiscoveryStrategyConfigDTO dto = new DiscoveryStrategyConfigDTO(strategyConfig);
            strategies.add(dto.toJson());
        }
        root.add("discoveryStrategy", strategies);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        JsonValue discoveryStrategies;
        this.config = new DiscoveryConfig();
        JsonValue nodeFilterClass = json.get("nodeFilterClass");
        if (nodeFilterClass != null && !nodeFilterClass.isNull()) {
            this.config.setNodeFilterClass(nodeFilterClass.asString());
        }
        if ((discoveryStrategies = json.get("discoveryStrategy")) != null && !discoveryStrategies.isNull()) {
            Collection<DiscoveryStrategyConfig> strategyConfigs = this.config.getDiscoveryStrategyConfigs();
            for (JsonValue strategy : discoveryStrategies.asArray()) {
                DiscoveryStrategyConfigDTO strategyDTO = new DiscoveryStrategyConfigDTO();
                strategyDTO.fromJson(strategy.asObject());
                strategyConfigs.add(strategyDTO.getConfig());
            }
        }
    }

    public DiscoveryConfig getConfig() {
        return this.config;
    }
}

