/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.config.AliasedDiscoveryConfig;
import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.AzureConfig;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.EurekaConfig;
import com.hazelcast.config.GcpConfig;
import com.hazelcast.config.KubernetesConfig;
import com.hazelcast.config.WANQueueFullBehavior;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanPublisherState;
import com.hazelcast.config.WanSyncConfig;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.internal.management.dto.AliasedDiscoveryConfigDTO;
import com.hazelcast.internal.management.dto.DiscoveryConfigDTO;
import com.hazelcast.internal.management.dto.WanSyncConfigDTO;
import com.hazelcast.util.JsonUtil;
import com.hazelcast.util.MapUtil;

public class WanPublisherConfigDTO
implements JsonSerializable {
    private WanPublisherConfig config;

    public WanPublisherConfigDTO() {
    }

    public WanPublisherConfigDTO(WanPublisherConfig config) {
        this.config = config;
    }

    @Override
    public JsonObject toJson() {
        WanSyncConfig syncConfig;
        JsonObject root = new JsonObject();
        if (this.config.getGroupName() != null) {
            root.add("groupName", this.config.getGroupName());
        }
        if (this.config.getPublisherId() != null) {
            root.add("publisherId", this.config.getPublisherId());
        }
        root.add("queueCapacity", this.config.getQueueCapacity());
        if (this.config.getQueueFullBehavior() != null) {
            root.add("queueFullBehavior", this.config.getQueueFullBehavior().getId());
        }
        if (this.config.getInitialPublisherState() != null) {
            root.add("initialPublisherState", this.config.getInitialPublisherState().getId());
        }
        if (!MapUtil.isNullOrEmpty(this.config.getProperties())) {
            root.add("properties", JsonUtil.toJsonObject(this.config.getProperties()));
        }
        if (this.config.getClassName() != null) {
            root.add("className", this.config.getClassName());
        }
        this.serializeAliasedDiscoveryConfig(root, "aws", this.config.getAwsConfig());
        this.serializeAliasedDiscoveryConfig(root, "gcp", this.config.getGcpConfig());
        this.serializeAliasedDiscoveryConfig(root, "azure", this.config.getAzureConfig());
        this.serializeAliasedDiscoveryConfig(root, "kubernetes", this.config.getKubernetesConfig());
        this.serializeAliasedDiscoveryConfig(root, "eureka", this.config.getEurekaConfig());
        DiscoveryConfig discoveryConfig = this.config.getDiscoveryConfig();
        if (discoveryConfig != null) {
            root.add("discovery", new DiscoveryConfigDTO(discoveryConfig).toJson());
        }
        if ((syncConfig = this.config.getWanSyncConfig()) != null) {
            root.add("sync", new WanSyncConfigDTO(syncConfig).toJson());
        }
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        JsonValue syncJson;
        JsonValue discoveryJson;
        EurekaConfig eurekaConfig;
        KubernetesConfig kubernetesConfig;
        AzureConfig azureConfig;
        GcpConfig gcpConfig;
        AwsConfig awsConfig;
        JsonValue initialPublisherState;
        JsonValue queueFullBehavior;
        JsonValue queueCapacity;
        JsonValue publisherId;
        this.config = new WanPublisherConfig();
        JsonValue groupName = json.get("groupName");
        if (groupName != null && !groupName.isNull()) {
            this.config.setGroupName(groupName.asString());
        }
        if ((publisherId = json.get("publisherId")) != null && !publisherId.isNull()) {
            this.config.setPublisherId(publisherId.asString());
        }
        if ((queueCapacity = json.get("queueCapacity")) != null && !queueCapacity.isNull()) {
            this.config.setQueueCapacity(queueCapacity.asInt());
        }
        if ((queueFullBehavior = json.get("queueFullBehavior")) != null && !queueFullBehavior.isNull()) {
            this.config.setQueueFullBehavior(WANQueueFullBehavior.getByType(queueFullBehavior.asInt()));
        }
        if ((initialPublisherState = json.get("initialPublisherState")) != null && !initialPublisherState.isNull()) {
            this.config.setInitialPublisherState(WanPublisherState.getByType((byte)initialPublisherState.asInt()));
        }
        this.config.setProperties(JsonUtil.fromJsonObject((JsonObject)json.get("properties")));
        JsonValue className = json.get("className");
        if (className != null && !className.isNull()) {
            this.config.setClassName(className.asString());
        }
        if ((awsConfig = (AwsConfig)this.deserializeAliasedDiscoveryConfig(json, "aws")) != null) {
            this.config.setAwsConfig(awsConfig);
        }
        if ((gcpConfig = (GcpConfig)this.deserializeAliasedDiscoveryConfig(json, "gcp")) != null) {
            this.config.setGcpConfig(gcpConfig);
        }
        if ((azureConfig = (AzureConfig)this.deserializeAliasedDiscoveryConfig(json, "azure")) != null) {
            this.config.setAzureConfig(azureConfig);
        }
        if ((kubernetesConfig = (KubernetesConfig)this.deserializeAliasedDiscoveryConfig(json, "kubernetes")) != null) {
            this.config.setKubernetesConfig(kubernetesConfig);
        }
        if ((eurekaConfig = (EurekaConfig)this.deserializeAliasedDiscoveryConfig(json, "eureka")) != null) {
            this.config.setEurekaConfig(eurekaConfig);
        }
        if ((discoveryJson = json.get("discovery")) != null && !discoveryJson.isNull()) {
            DiscoveryConfigDTO discoveryDTO = new DiscoveryConfigDTO();
            discoveryDTO.fromJson(discoveryJson.asObject());
            this.config.setDiscoveryConfig(discoveryDTO.getConfig());
        }
        if ((syncJson = json.get("sync")) != null && !syncJson.isNull()) {
            WanSyncConfigDTO syncDTO = new WanSyncConfigDTO();
            syncDTO.fromJson(syncJson.asObject());
            this.config.setWanSyncConfig(syncDTO.getConfig());
        }
    }

    private AliasedDiscoveryConfig deserializeAliasedDiscoveryConfig(JsonObject json, String tag) {
        JsonValue configJson = json.get(tag);
        if (configJson != null && !configJson.isNull()) {
            AliasedDiscoveryConfigDTO dto = new AliasedDiscoveryConfigDTO(tag);
            dto.fromJson(configJson.asObject());
            return dto.getConfig();
        }
        return null;
    }

    private void serializeAliasedDiscoveryConfig(JsonObject object, String tag, AliasedDiscoveryConfig config) {
        if (config != null) {
            object.add(tag, new AliasedDiscoveryConfigDTO(config).toJson());
        }
    }

    public WanPublisherConfig getConfig() {
        return this.config;
    }
}

