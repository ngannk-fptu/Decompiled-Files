/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.AzureConfig;
import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.EurekaConfig;
import com.hazelcast.config.GcpConfig;
import com.hazelcast.config.KubernetesConfig;
import com.hazelcast.config.WANQueueFullBehavior;
import com.hazelcast.config.WanPublisherState;
import com.hazelcast.config.WanSyncConfig;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WanPublisherConfig
implements IdentifiedDataSerializable,
Versioned {
    private static final int DEFAULT_QUEUE_CAPACITY = 10000;
    private static final WANQueueFullBehavior DEFAULT_QUEUE_FULL_BEHAVIOR = WANQueueFullBehavior.DISCARD_AFTER_MUTATION;
    private String groupName = "dev";
    private String publisherId;
    private int queueCapacity = 10000;
    private WANQueueFullBehavior queueFullBehavior = DEFAULT_QUEUE_FULL_BEHAVIOR;
    private WanPublisherState initialPublisherState = WanPublisherState.REPLICATING;
    private Map<String, Comparable> properties = new HashMap<String, Comparable>();
    private String className;
    private Object implementation;
    private AwsConfig awsConfig = new AwsConfig();
    private GcpConfig gcpConfig = new GcpConfig();
    private AzureConfig azureConfig = new AzureConfig();
    private KubernetesConfig kubernetesConfig = new KubernetesConfig();
    private EurekaConfig eurekaConfig = new EurekaConfig();
    private DiscoveryConfig discoveryConfig = new DiscoveryConfig();
    private WanSyncConfig wanSyncConfig = new WanSyncConfig();
    private String endpoint;

    public WanSyncConfig getWanSyncConfig() {
        return this.wanSyncConfig;
    }

    public WanPublisherConfig setWanSyncConfig(WanSyncConfig wanSyncConfig) {
        this.wanSyncConfig = wanSyncConfig;
        return this;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public WanPublisherConfig setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public String getPublisherId() {
        return this.publisherId;
    }

    public WanPublisherConfig setPublisherId(String publisherId) {
        this.publisherId = !StringUtil.isNullOrEmptyAfterTrim(publisherId) ? publisherId : null;
        return this;
    }

    public int getQueueCapacity() {
        return this.queueCapacity;
    }

    public WanPublisherConfig setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
        return this;
    }

    public WANQueueFullBehavior getQueueFullBehavior() {
        return this.queueFullBehavior;
    }

    public WanPublisherConfig setQueueFullBehavior(WANQueueFullBehavior queueFullBehavior) {
        this.queueFullBehavior = queueFullBehavior;
        return this;
    }

    public WanPublisherState getInitialPublisherState() {
        return this.initialPublisherState;
    }

    public WanPublisherConfig setInitialPublisherState(WanPublisherState initialPublisherState) {
        Preconditions.checkNotNull(initialPublisherState, "Initial WAN publisher state must not be null");
        this.initialPublisherState = initialPublisherState;
        return this;
    }

    public Map<String, Comparable> getProperties() {
        return this.properties;
    }

    public WanPublisherConfig setProperties(Map<String, Comparable> properties) {
        this.properties = properties;
        return this;
    }

    public String getClassName() {
        return this.className;
    }

    public WanPublisherConfig setClassName(String className) {
        this.className = className;
        return this;
    }

    public Object getImplementation() {
        return this.implementation;
    }

    public WanPublisherConfig setImplementation(Object implementation) {
        this.implementation = implementation;
        return this;
    }

    public AwsConfig getAwsConfig() {
        return this.awsConfig;
    }

    public WanPublisherConfig setAwsConfig(AwsConfig awsConfig) {
        this.awsConfig = Preconditions.isNotNull(awsConfig, "awsConfig");
        return this;
    }

    public GcpConfig getGcpConfig() {
        return this.gcpConfig;
    }

    public WanPublisherConfig setGcpConfig(GcpConfig gcpConfig) {
        this.gcpConfig = Preconditions.isNotNull(gcpConfig, "gcpConfig");
        return this;
    }

    public AzureConfig getAzureConfig() {
        return this.azureConfig;
    }

    public WanPublisherConfig setAzureConfig(AzureConfig azureConfig) {
        this.azureConfig = Preconditions.isNotNull(azureConfig, "azureConfig");
        return this;
    }

    public KubernetesConfig getKubernetesConfig() {
        return this.kubernetesConfig;
    }

    public WanPublisherConfig setKubernetesConfig(KubernetesConfig kubernetesConfig) {
        this.kubernetesConfig = Preconditions.isNotNull(kubernetesConfig, "kubernetesConfig");
        return this;
    }

    public EurekaConfig getEurekaConfig() {
        return this.eurekaConfig;
    }

    public WanPublisherConfig setEurekaConfig(EurekaConfig eurekaConfig) {
        this.eurekaConfig = Preconditions.isNotNull(eurekaConfig, "eurekaConfig");
        return this;
    }

    public DiscoveryConfig getDiscoveryConfig() {
        return this.discoveryConfig;
    }

    public WanPublisherConfig setDiscoveryConfig(DiscoveryConfig discoveryConfig) {
        this.discoveryConfig = Preconditions.isNotNull(discoveryConfig, "discoveryProvidersConfig");
        return this;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public WanPublisherConfig setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public String toString() {
        return "WanPublisherConfig{groupName='" + this.groupName + '\'' + ", publisherId='" + this.publisherId + '\'' + ", queueCapacity=" + this.queueCapacity + ", queueFullBehavior=" + (Object)((Object)this.queueFullBehavior) + ", initialPublisherState=" + (Object)((Object)this.initialPublisherState) + ", wanSyncConfig=" + this.wanSyncConfig + ", properties=" + this.properties + ", className='" + this.className + '\'' + ", implementation=" + this.implementation + ", awsConfig=" + this.awsConfig + ", gcpConfig=" + this.gcpConfig + ", azureConfig=" + this.azureConfig + ", kubernetesConfig=" + this.kubernetesConfig + ", eurekaConfig=" + this.eurekaConfig + ", discoveryConfig=" + this.discoveryConfig + ", endpoint=" + this.endpoint + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.groupName);
        out.writeInt(this.queueCapacity);
        out.writeInt(this.queueFullBehavior.getId());
        int size = this.properties.size();
        out.writeInt(size);
        for (Map.Entry<String, Comparable> entry : this.properties.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeObject(entry.getValue());
        }
        out.writeUTF(this.className);
        out.writeObject(this.implementation);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            out.writeByte(this.initialPublisherState.getId());
            out.writeObject(this.wanSyncConfig);
            out.writeUTF(this.publisherId);
        }
        if (out.getVersion().isGreaterOrEqual(Versions.V3_12)) {
            out.writeObject(this.awsConfig);
            out.writeObject(this.gcpConfig);
            out.writeObject(this.azureConfig);
            out.writeObject(this.kubernetesConfig);
            out.writeObject(this.eurekaConfig);
            out.writeObject(this.discoveryConfig);
            out.writeUTF(this.endpoint);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.groupName = in.readUTF();
        this.queueCapacity = in.readInt();
        this.queueFullBehavior = WANQueueFullBehavior.getByType(in.readInt());
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            this.properties.put(in.readUTF(), (Comparable)in.readObject());
        }
        this.className = in.readUTF();
        this.implementation = in.readObject();
        if (in.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            this.initialPublisherState = WanPublisherState.getByType(in.readByte());
            this.wanSyncConfig = (WanSyncConfig)in.readObject();
            this.publisherId = in.readUTF();
        }
        if (in.getVersion().isGreaterOrEqual(Versions.V3_12)) {
            this.awsConfig = (AwsConfig)in.readObject();
            this.gcpConfig = (GcpConfig)in.readObject();
            this.azureConfig = (AzureConfig)in.readObject();
            this.kubernetesConfig = (KubernetesConfig)in.readObject();
            this.eurekaConfig = (EurekaConfig)in.readObject();
            this.discoveryConfig = (DiscoveryConfig)in.readObject();
            this.endpoint = in.readUTF();
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WanPublisherConfig that = (WanPublisherConfig)o;
        if (this.queueCapacity != that.queueCapacity) {
            return false;
        }
        if (this.groupName != null ? !this.groupName.equals(that.groupName) : that.groupName != null) {
            return false;
        }
        if (this.publisherId != null ? !this.publisherId.equals(that.publisherId) : that.publisherId != null) {
            return false;
        }
        if (this.queueFullBehavior != that.queueFullBehavior) {
            return false;
        }
        if (this.initialPublisherState != that.initialPublisherState) {
            return false;
        }
        if (this.properties != null ? !this.properties.equals(that.properties) : that.properties != null) {
            return false;
        }
        if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
            return false;
        }
        if (this.implementation != null ? !this.implementation.equals(that.implementation) : that.implementation != null) {
            return false;
        }
        if (!this.awsConfig.equals(that.awsConfig)) {
            return false;
        }
        if (!this.gcpConfig.equals(that.gcpConfig)) {
            return false;
        }
        if (!this.azureConfig.equals(that.azureConfig)) {
            return false;
        }
        if (!this.kubernetesConfig.equals(that.kubernetesConfig)) {
            return false;
        }
        if (!this.eurekaConfig.equals(that.eurekaConfig)) {
            return false;
        }
        if (!this.discoveryConfig.equals(that.discoveryConfig)) {
            return false;
        }
        if (this.endpoint != null ? !this.endpoint.equals(that.endpoint) : that.endpoint != null) {
            return false;
        }
        return this.wanSyncConfig != null ? this.wanSyncConfig.equals(that.wanSyncConfig) : that.wanSyncConfig == null;
    }

    public int hashCode() {
        int result = this.groupName != null ? this.groupName.hashCode() : 0;
        result = 31 * result + (this.publisherId != null ? this.publisherId.hashCode() : 0);
        result = 31 * result + this.queueCapacity;
        result = 31 * result + (this.queueFullBehavior != null ? this.queueFullBehavior.hashCode() : 0);
        result = 31 * result + this.initialPublisherState.hashCode();
        result = 31 * result + (this.properties != null ? this.properties.hashCode() : 0);
        result = 31 * result + (this.className != null ? this.className.hashCode() : 0);
        result = 31 * result + (this.implementation != null ? this.implementation.hashCode() : 0);
        result = 31 * result + this.awsConfig.hashCode();
        result = 31 * result + this.gcpConfig.hashCode();
        result = 31 * result + this.azureConfig.hashCode();
        result = 31 * result + this.kubernetesConfig.hashCode();
        result = 31 * result + this.eurekaConfig.hashCode();
        result = 31 * result + this.discoveryConfig.hashCode();
        result = 31 * result + (this.wanSyncConfig != null ? this.wanSyncConfig.hashCode() : 0);
        result = 31 * result + (this.endpoint != null ? this.endpoint.hashCode() : 0);
        return result;
    }
}

