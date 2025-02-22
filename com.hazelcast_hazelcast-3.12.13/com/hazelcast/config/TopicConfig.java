/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.config.TopicConfigReadOnly;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TopicConfig
implements IdentifiedDataSerializable,
NamedConfig {
    public static final boolean DEFAULT_GLOBAL_ORDERING_ENABLED = false;
    private String name;
    private boolean globalOrderingEnabled = false;
    private boolean statisticsEnabled = true;
    private boolean multiThreadingEnabled;
    private List<ListenerConfig> listenerConfigs;
    private transient TopicConfigReadOnly readOnly;

    public TopicConfig() {
    }

    public TopicConfig(String name) {
        this.setName(name);
    }

    public TopicConfig(TopicConfig config) {
        Preconditions.isNotNull(config, "config");
        this.name = config.name;
        this.globalOrderingEnabled = config.globalOrderingEnabled;
        this.multiThreadingEnabled = config.multiThreadingEnabled;
        this.listenerConfigs = new ArrayList<ListenerConfig>(config.getMessageListenerConfigs());
    }

    public TopicConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new TopicConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public TopicConfig setName(String name) {
        this.name = Preconditions.checkHasText(name, "name must contain text");
        return this;
    }

    public boolean isGlobalOrderingEnabled() {
        return this.globalOrderingEnabled;
    }

    public TopicConfig setGlobalOrderingEnabled(boolean globalOrderingEnabled) {
        if (this.multiThreadingEnabled && globalOrderingEnabled) {
            throw new IllegalArgumentException("Global ordering can not be enabled when multi-threading is used.");
        }
        this.globalOrderingEnabled = globalOrderingEnabled;
        return this;
    }

    public boolean isMultiThreadingEnabled() {
        return this.multiThreadingEnabled;
    }

    public TopicConfig setMultiThreadingEnabled(boolean multiThreadingEnabled) {
        if (this.globalOrderingEnabled && multiThreadingEnabled) {
            throw new IllegalArgumentException("Multi-threading can not be enabled when global ordering is used.");
        }
        this.multiThreadingEnabled = multiThreadingEnabled;
        return this;
    }

    public TopicConfig addMessageListenerConfig(ListenerConfig listenerConfig) {
        this.getMessageListenerConfigs().add(listenerConfig);
        return this;
    }

    public List<ListenerConfig> getMessageListenerConfigs() {
        if (this.listenerConfigs == null) {
            this.listenerConfigs = new ArrayList<ListenerConfig>();
        }
        return this.listenerConfigs;
    }

    public TopicConfig setMessageListenerConfigs(List<ListenerConfig> listenerConfigs) {
        this.listenerConfigs = listenerConfigs;
        return this;
    }

    public boolean isStatisticsEnabled() {
        return this.statisticsEnabled;
    }

    public TopicConfig setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
        return this;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof TopicConfig)) {
            return false;
        }
        TopicConfig that = (TopicConfig)o;
        if (this.globalOrderingEnabled != that.globalOrderingEnabled) {
            return false;
        }
        if (this.statisticsEnabled != that.statisticsEnabled) {
            return false;
        }
        if (this.multiThreadingEnabled != that.multiThreadingEnabled) {
            return false;
        }
        if (this.listenerConfigs != null && that.listenerConfigs != null && !this.listenerConfigs.equals(that.listenerConfigs)) {
            return false;
        }
        if (this.listenerConfigs != null && that.listenerConfigs == null && !this.listenerConfigs.isEmpty()) {
            return false;
        }
        if (this.listenerConfigs == null && that.listenerConfigs != null && !that.listenerConfigs.isEmpty()) {
            return false;
        }
        return this.name != null ? this.name.equals(that.name) : that.name == null;
    }

    public final int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.globalOrderingEnabled ? 1 : 0);
        result = 31 * result + (this.statisticsEnabled ? 1 : 0);
        result = 31 * result + (this.multiThreadingEnabled ? 1 : 0);
        result = 31 * result + (this.listenerConfigs != null ? this.listenerConfigs.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "TopicConfig [name=" + this.name + ", globalOrderingEnabled=" + this.globalOrderingEnabled + ", multiThreadingEnabled=" + this.multiThreadingEnabled + ", statisticsEnabled=" + this.statisticsEnabled + "]";
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 22;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeBoolean(this.globalOrderingEnabled);
        out.writeBoolean(this.statisticsEnabled);
        out.writeBoolean(this.multiThreadingEnabled);
        SerializationUtil.writeNullableList(this.listenerConfigs, out);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.globalOrderingEnabled = in.readBoolean();
        this.statisticsEnabled = in.readBoolean();
        this.multiThreadingEnabled = in.readBoolean();
        this.listenerConfigs = SerializationUtil.readNullableList(in);
    }
}

