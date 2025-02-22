/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.config.ProbabilisticQuorumConfigBuilder;
import com.hazelcast.config.QuorumListenerConfig;
import com.hazelcast.config.RecentlyActiveQuorumConfigBuilder;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.quorum.QuorumFunction;
import com.hazelcast.quorum.QuorumType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuorumConfig
implements IdentifiedDataSerializable,
NamedConfig {
    private String name;
    private boolean enabled;
    private int size;
    private List<QuorumListenerConfig> listenerConfigs = new ArrayList<QuorumListenerConfig>();
    private QuorumType type = QuorumType.READ_WRITE;
    private String quorumFunctionClassName;
    private QuorumFunction quorumFunctionImplementation;

    public QuorumConfig() {
    }

    public QuorumConfig(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    public QuorumConfig(String name, boolean enabled, int size) {
        this.name = name;
        this.enabled = enabled;
        this.size = size;
    }

    public QuorumConfig(QuorumConfig quorumConfig) {
        this.name = quorumConfig.name;
        this.enabled = quorumConfig.enabled;
        this.size = quorumConfig.size;
        this.listenerConfigs = quorumConfig.listenerConfigs;
        this.type = quorumConfig.type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public QuorumConfig setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public QuorumConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public int getSize() {
        return this.size;
    }

    public QuorumConfig setSize(int size) {
        if (size < 2) {
            throw new InvalidConfigurationException("Minimum quorum size cannot be less than 2");
        }
        this.size = size;
        return this;
    }

    public QuorumType getType() {
        return this.type;
    }

    public QuorumConfig setType(QuorumType type) {
        this.type = type;
        return this;
    }

    public List<QuorumListenerConfig> getListenerConfigs() {
        return this.listenerConfigs;
    }

    public QuorumConfig setListenerConfigs(List<QuorumListenerConfig> listenerConfigs) {
        this.listenerConfigs = listenerConfigs;
        return this;
    }

    public QuorumConfig addListenerConfig(QuorumListenerConfig listenerConfig) {
        this.listenerConfigs.add(listenerConfig);
        return this;
    }

    public String getQuorumFunctionClassName() {
        return this.quorumFunctionClassName;
    }

    public QuorumConfig setQuorumFunctionClassName(String quorumFunctionClassName) {
        this.quorumFunctionClassName = quorumFunctionClassName;
        return this;
    }

    public QuorumFunction getQuorumFunctionImplementation() {
        return this.quorumFunctionImplementation;
    }

    public QuorumConfig setQuorumFunctionImplementation(QuorumFunction quorumFunctionImplementation) {
        this.quorumFunctionImplementation = quorumFunctionImplementation;
        return this;
    }

    public String toString() {
        return "QuorumConfig{name='" + this.name + '\'' + ", enabled=" + this.enabled + ", size=" + this.size + ", listenerConfigs=" + this.listenerConfigs + ", quorumFunctionClassName=" + this.quorumFunctionClassName + ", quorumFunctionImplementation=" + this.quorumFunctionImplementation + ", type=" + (Object)((Object)this.type) + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 42;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeBoolean(this.enabled);
        out.writeInt(this.size);
        SerializationUtil.writeNullableList(this.listenerConfigs, out);
        out.writeUTF(this.type.name());
        out.writeUTF(this.quorumFunctionClassName);
        out.writeObject(this.quorumFunctionImplementation);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.enabled = in.readBoolean();
        this.size = in.readInt();
        this.listenerConfigs = SerializationUtil.readNullableList(in);
        this.type = QuorumType.valueOf(in.readUTF());
        this.quorumFunctionClassName = in.readUTF();
        this.quorumFunctionImplementation = (QuorumFunction)in.readObject();
    }

    public static ProbabilisticQuorumConfigBuilder newProbabilisticQuorumConfigBuilder(String name, int size) {
        return new ProbabilisticQuorumConfigBuilder(name, size);
    }

    public static RecentlyActiveQuorumConfigBuilder newRecentlyActiveQuorumConfigBuilder(String name, int size, int toleranceMillis) {
        return new RecentlyActiveQuorumConfigBuilder(name, size, toleranceMillis);
    }
}

