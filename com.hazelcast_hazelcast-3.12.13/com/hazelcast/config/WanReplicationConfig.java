/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.WanConsumerConfig;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WanReplicationConfig
implements IdentifiedDataSerializable,
Versioned {
    private String name;
    private WanConsumerConfig wanConsumerConfig;
    private List<WanPublisherConfig> wanPublisherConfigs = new ArrayList<WanPublisherConfig>(2);

    public String getName() {
        return this.name;
    }

    public WanReplicationConfig setName(String name) {
        this.name = name;
        return this;
    }

    public WanConsumerConfig getWanConsumerConfig() {
        return this.wanConsumerConfig;
    }

    public WanReplicationConfig setWanConsumerConfig(WanConsumerConfig wanConsumerConfig) {
        this.wanConsumerConfig = wanConsumerConfig;
        return this;
    }

    public List<WanPublisherConfig> getWanPublisherConfigs() {
        return this.wanPublisherConfigs;
    }

    public void setWanPublisherConfigs(List<WanPublisherConfig> wanPublisherConfigs) {
        if (wanPublisherConfigs != null && !wanPublisherConfigs.isEmpty()) {
            this.wanPublisherConfigs = wanPublisherConfigs;
        }
    }

    public WanReplicationConfig addWanPublisherConfig(WanPublisherConfig wanPublisherConfig) {
        this.wanPublisherConfigs.add(wanPublisherConfig);
        return this;
    }

    public String toString() {
        return "WanReplicationConfig{name='" + this.name + '\'' + ", wanPublisherConfigs=" + this.wanPublisherConfigs + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_12)) {
            out.writeObject(this.wanConsumerConfig);
        } else if (this.wanConsumerConfig != null) {
            out.writeBoolean(true);
            this.wanConsumerConfig.writeData(out);
        } else {
            out.writeBoolean(false);
        }
        int publisherCount = this.wanPublisherConfigs.size();
        out.writeInt(publisherCount);
        for (WanPublisherConfig wanPublisherConfig : this.wanPublisherConfigs) {
            if (out.getVersion().isGreaterOrEqual(Versions.V3_12)) {
                out.writeObject(wanPublisherConfig);
                continue;
            }
            wanPublisherConfig.writeData(out);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        if (in.getVersion().isGreaterOrEqual(Versions.V3_12)) {
            this.wanConsumerConfig = (WanConsumerConfig)in.readObject();
        } else {
            boolean consumerConfigExists = in.readBoolean();
            if (consumerConfigExists) {
                WanConsumerConfig consumerConfig = new WanConsumerConfig();
                consumerConfig.readData(in);
                this.wanConsumerConfig = consumerConfig;
            }
        }
        int publisherCount = in.readInt();
        for (int i = 0; i < publisherCount; ++i) {
            WanPublisherConfig publisherConfig;
            if (in.getVersion().isGreaterOrEqual(Versions.V3_12)) {
                publisherConfig = (WanPublisherConfig)in.readObject();
            } else {
                publisherConfig = new WanPublisherConfig();
                publisherConfig.readData(in);
            }
            this.wanPublisherConfigs.add(publisherConfig);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WanReplicationConfig that = (WanReplicationConfig)o;
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (this.wanConsumerConfig != null ? !this.wanConsumerConfig.equals(that.wanConsumerConfig) : that.wanConsumerConfig != null) {
            return false;
        }
        return this.wanPublisherConfigs.equals(that.wanPublisherConfigs);
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + (this.wanConsumerConfig != null ? this.wanConsumerConfig.hashCode() : 0);
        result = 31 * result + this.wanPublisherConfigs.hashCode();
        return result;
    }
}

