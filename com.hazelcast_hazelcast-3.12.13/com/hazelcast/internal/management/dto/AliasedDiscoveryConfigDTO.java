/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.config.AliasedDiscoveryConfig;
import com.hazelcast.config.AliasedDiscoveryConfigUtils;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.util.JsonUtil;
import com.hazelcast.util.MapUtil;
import java.util.Map;

public class AliasedDiscoveryConfigDTO
implements JsonSerializable {
    private String tag;
    private AliasedDiscoveryConfig config;

    public AliasedDiscoveryConfigDTO(String tag) {
        this.tag = tag;
    }

    public AliasedDiscoveryConfigDTO(AliasedDiscoveryConfig config) {
        this.config = config;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject().add("enabled", this.config.isEnabled()).add("usePublicIp", this.config.isUsePublicIp());
        if (!MapUtil.isNullOrEmpty(this.config.getProperties())) {
            root.add("properties", JsonUtil.toJsonObject(this.config.getProperties()));
        }
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        JsonValue usePublicIp;
        this.config = AliasedDiscoveryConfigUtils.newConfigFor(this.tag);
        JsonValue enabled = json.get("enabled");
        if (enabled != null && !enabled.isNull()) {
            this.config.setEnabled(enabled.asBoolean());
        }
        if ((usePublicIp = json.get("usePublicIp")) != null && !usePublicIp.isNull()) {
            this.config.setUsePublicIp(usePublicIp.asBoolean());
        }
        Map<String, Comparable> properties = JsonUtil.fromJsonObject((JsonObject)json.get("properties"));
        for (Map.Entry<String, Comparable> property : properties.entrySet()) {
            this.config.setProperty(property.getKey(), (String)((Object)property.getValue()));
        }
    }

    public AliasedDiscoveryConfig getConfig() {
        return this.config;
    }
}

