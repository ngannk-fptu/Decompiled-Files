/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.ExecutorConfigReadOnly;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import java.io.IOException;

public class ExecutorConfig
implements IdentifiedDataSerializable,
Versioned,
NamedConfig {
    public static final int DEFAULT_POOL_SIZE = 16;
    public static final int DEFAULT_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private String name = "default";
    private int poolSize = 16;
    private int queueCapacity = Integer.MAX_VALUE;
    private boolean statisticsEnabled = true;
    private String quorumName;
    private transient ExecutorConfigReadOnly readOnly;

    public ExecutorConfig() {
    }

    public ExecutorConfig(String name) {
        this.name = name;
    }

    public ExecutorConfig(String name, int poolSize) {
        this.name = name;
        this.poolSize = poolSize;
    }

    public ExecutorConfig(ExecutorConfig config) {
        this.name = config.name;
        this.poolSize = config.poolSize;
        this.queueCapacity = config.queueCapacity;
        this.statisticsEnabled = config.statisticsEnabled;
        this.quorumName = config.quorumName;
    }

    public ExecutorConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new ExecutorConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ExecutorConfig setName(String name) {
        this.name = name;
        return this;
    }

    public int getPoolSize() {
        return this.poolSize;
    }

    public ExecutorConfig setPoolSize(int poolSize) {
        if (poolSize <= 0) {
            throw new IllegalArgumentException("poolSize must be positive");
        }
        this.poolSize = poolSize;
        return this;
    }

    public int getQueueCapacity() {
        return this.queueCapacity;
    }

    public ExecutorConfig setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
        return this;
    }

    public boolean isStatisticsEnabled() {
        return this.statisticsEnabled;
    }

    public ExecutorConfig setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
        return this;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public ExecutorConfig setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    public String toString() {
        return "ExecutorConfig{name='" + this.name + '\'' + ", poolSize=" + this.poolSize + ", queueCapacity=" + this.queueCapacity + ", quorumName=" + this.quorumName + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 30;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.poolSize);
        out.writeInt(this.queueCapacity);
        out.writeBoolean(this.statisticsEnabled);
        out.writeUTF(this.quorumName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.poolSize = in.readInt();
        this.queueCapacity = in.readInt();
        this.statisticsEnabled = in.readBoolean();
        this.quorumName = in.readUTF();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExecutorConfig)) {
            return false;
        }
        ExecutorConfig that = (ExecutorConfig)o;
        if (this.poolSize != that.poolSize) {
            return false;
        }
        if (this.queueCapacity != that.queueCapacity) {
            return false;
        }
        if (this.statisticsEnabled != that.statisticsEnabled) {
            return false;
        }
        if (this.quorumName != null ? !this.quorumName.equals(that.quorumName) : that.quorumName != null) {
            return false;
        }
        return this.name.equals(that.name);
    }

    public final int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.poolSize;
        result = 31 * result + this.queueCapacity;
        result = 31 * result + (this.statisticsEnabled ? 1 : 0);
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        return result;
    }
}

