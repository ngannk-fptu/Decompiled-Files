/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.config.WanConsumerConfig;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.internal.management.dto.WanConsumerConfigDTO;
import com.hazelcast.internal.management.dto.WanPublisherConfigDTO;
import java.util.List;

public class WanReplicationConfigDTO
implements JsonSerializable {
    private WanReplicationConfig config;

    public WanReplicationConfigDTO(WanReplicationConfig config) {
        this.config = config;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        if (this.config.getName() != null) {
            root.add("name", this.config.getName());
        }
        JsonArray publishers = new JsonArray();
        for (WanPublisherConfig publisherConfig : this.config.getWanPublisherConfigs()) {
            WanPublisherConfigDTO dto = new WanPublisherConfigDTO(publisherConfig);
            publishers.add(dto.toJson());
        }
        root.add("publishers", publishers);
        WanConsumerConfig consumerConfig = this.config.getWanConsumerConfig();
        if (consumerConfig != null) {
            root.add("consumer", new WanConsumerConfigDTO(consumerConfig).toJson());
        }
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        JsonValue consumer;
        JsonValue publishers;
        this.config = new WanReplicationConfig();
        JsonValue name = json.get("name");
        if (name != null) {
            this.config.setName(name.asString());
        }
        if ((publishers = json.get("publishers")) != null && !publishers.isNull()) {
            List<WanPublisherConfig> publisherConfigs = this.config.getWanPublisherConfigs();
            for (JsonValue publisher : publishers.asArray()) {
                WanPublisherConfigDTO publisherDTO = new WanPublisherConfigDTO();
                publisherDTO.fromJson(publisher.asObject());
                publisherConfigs.add(publisherDTO.getConfig());
            }
        }
        if ((consumer = json.get("consumer")) != null && !consumer.isNull()) {
            WanConsumerConfigDTO consumerDTO = new WanConsumerConfigDTO();
            consumerDTO.fromJson(consumer.asObject());
            this.config.setWanConsumerConfig(consumerDTO.getConfig());
        }
    }

    public WanReplicationConfig getConfig() {
        return this.config;
    }
}

