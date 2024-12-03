/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.merge.SplitBrainMergeTypeProvider;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class ScheduledExecutorConfig
implements SplitBrainMergeTypeProvider,
IdentifiedDataSerializable,
Versioned,
NamedConfig {
    private static final int DEFAULT_POOL_SIZE = 16;
    private static final int DEFAULT_CAPACITY = 100;
    private static final int DEFAULT_DURABILITY = 1;
    private String name = "default";
    private int durability = 1;
    private int capacity = 100;
    private int poolSize = 16;
    private String quorumName;
    private transient ScheduledExecutorConfigReadOnly readOnly;
    private MergePolicyConfig mergePolicyConfig = new MergePolicyConfig();

    public ScheduledExecutorConfig() {
    }

    public ScheduledExecutorConfig(String name) {
        this.name = name;
    }

    public ScheduledExecutorConfig(String name, int durability, int capacity, int poolSize) {
        this(name, durability, capacity, poolSize, null, new MergePolicyConfig());
    }

    public ScheduledExecutorConfig(String name, int durability, int capacity, int poolSize, String quorumName, MergePolicyConfig mergePolicyConfig) {
        this.name = name;
        this.durability = durability;
        this.poolSize = poolSize;
        this.capacity = capacity;
        this.quorumName = quorumName;
        this.mergePolicyConfig = mergePolicyConfig;
    }

    public ScheduledExecutorConfig(ScheduledExecutorConfig config) {
        this(config.getName(), config.getDurability(), config.getCapacity(), config.getPoolSize(), config.getQuorumName(), config.getMergePolicyConfig());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ScheduledExecutorConfig setName(String name) {
        this.name = name;
        return this;
    }

    public int getPoolSize() {
        return this.poolSize;
    }

    public ScheduledExecutorConfig setPoolSize(int poolSize) {
        this.poolSize = Preconditions.checkPositive(poolSize, "Pool size should be greater than 0");
        return this;
    }

    public int getDurability() {
        return this.durability;
    }

    public ScheduledExecutorConfig setDurability(int durability) {
        this.durability = Preconditions.checkNotNegative(durability, "durability can't be smaller than 0");
        return this;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public ScheduledExecutorConfig setCapacity(int capacity) {
        this.capacity = Preconditions.checkNotNegative(capacity, "capacity can't be smaller than 0");
        return this;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public ScheduledExecutorConfig setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    public MergePolicyConfig getMergePolicyConfig() {
        return this.mergePolicyConfig;
    }

    public ScheduledExecutorConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        this.mergePolicyConfig = Preconditions.checkNotNull(mergePolicyConfig, "mergePolicyConfig cannot be null");
        return this;
    }

    public Class getProvidedMergeTypes() {
        return SplitBrainMergeTypes.ScheduledExecutorMergeTypes.class;
    }

    public String toString() {
        return "ScheduledExecutorConfig{name='" + this.name + '\'' + ", durability=" + this.durability + ", poolSize=" + this.poolSize + ", capacity=" + this.capacity + ", quorumName=" + this.quorumName + ", mergePolicyConfig=" + this.mergePolicyConfig + '}';
    }

    ScheduledExecutorConfig getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new ScheduledExecutorConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 32;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.durability);
        out.writeInt(this.capacity);
        out.writeInt(this.poolSize);
        out.writeUTF(this.quorumName);
        out.writeObject(this.mergePolicyConfig);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.durability = in.readInt();
        this.capacity = in.readInt();
        this.poolSize = in.readInt();
        this.quorumName = in.readUTF();
        this.mergePolicyConfig = (MergePolicyConfig)in.readObject();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduledExecutorConfig)) {
            return false;
        }
        ScheduledExecutorConfig that = (ScheduledExecutorConfig)o;
        if (this.durability != that.durability) {
            return false;
        }
        if (this.capacity != that.capacity) {
            return false;
        }
        if (this.poolSize != that.poolSize) {
            return false;
        }
        if (this.quorumName != null ? !this.quorumName.equals(that.quorumName) : that.quorumName != null) {
            return false;
        }
        if (this.mergePolicyConfig != null ? !this.mergePolicyConfig.equals(that.mergePolicyConfig) : that.mergePolicyConfig != null) {
            return false;
        }
        return this.name.equals(that.name);
    }

    public final int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.durability;
        result = 31 * result + this.capacity;
        result = 31 * result + this.poolSize;
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        result = 31 * result + (this.mergePolicyConfig != null ? this.mergePolicyConfig.hashCode() : 0);
        return result;
    }

    static class ScheduledExecutorConfigReadOnly
    extends ScheduledExecutorConfig {
        ScheduledExecutorConfigReadOnly(ScheduledExecutorConfig config) {
            super(config);
        }

        @Override
        public ScheduledExecutorConfig setName(String name) {
            throw new UnsupportedOperationException("This config is read-only scheduled executor: " + this.getName());
        }

        @Override
        public ScheduledExecutorConfig setDurability(int durability) {
            throw new UnsupportedOperationException("This config is read-only scheduled executor: " + this.getName());
        }

        @Override
        public ScheduledExecutorConfig setPoolSize(int poolSize) {
            throw new UnsupportedOperationException("This config is read-only scheduled executor: " + this.getName());
        }

        @Override
        public ScheduledExecutorConfig setCapacity(int capacity) {
            throw new UnsupportedOperationException("This config is read-only scheduled executor: " + this.getName());
        }

        @Override
        public ScheduledExecutorConfig setQuorumName(String quorumName) {
            throw new UnsupportedOperationException("This config is read-only scheduled executor: " + this.getName());
        }

        @Override
        public ScheduledExecutorConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
            throw new UnsupportedOperationException("This config is read-only scheduled executor: " + this.getName());
        }
    }
}

