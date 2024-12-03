/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class DurableExecutorConfig
implements IdentifiedDataSerializable,
Versioned,
NamedConfig {
    public static final int DEFAULT_POOL_SIZE = 16;
    public static final int DEFAULT_RING_BUFFER_CAPACITY = 100;
    public static final int DEFAULT_DURABILITY = 1;
    private String name = "default";
    private int poolSize = 16;
    private int durability = 1;
    private int capacity = 100;
    private String quorumName;
    private transient DurableExecutorConfigReadOnly readOnly;

    public DurableExecutorConfig() {
    }

    public DurableExecutorConfig(String name) {
        this.name = name;
    }

    public DurableExecutorConfig(String name, int poolSize, int durability, int capacity) {
        this(name, poolSize, durability, capacity, null);
    }

    public DurableExecutorConfig(String name, int poolSize, int durability, int capacity, String quorumName) {
        this.name = name;
        this.poolSize = poolSize;
        this.durability = durability;
        this.capacity = capacity;
        this.quorumName = quorumName;
    }

    public DurableExecutorConfig(DurableExecutorConfig config) {
        this(config.getName(), config.getPoolSize(), config.getDurability(), config.getCapacity(), config.getQuorumName());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DurableExecutorConfig setName(String name) {
        this.name = name;
        return this;
    }

    public int getPoolSize() {
        return this.poolSize;
    }

    public DurableExecutorConfig setPoolSize(int poolSize) {
        this.poolSize = Preconditions.checkPositive(poolSize, "Pool size should be greater than 0");
        return this;
    }

    public int getDurability() {
        return this.durability;
    }

    public DurableExecutorConfig setDurability(int durability) {
        this.durability = Preconditions.checkNotNegative(durability, "durability can't be smaller than 0");
        return this;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public DurableExecutorConfig setCapacity(int capacity) {
        this.capacity = Preconditions.checkPositive(capacity, "Capacity should be greater than 0");
        return this;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public DurableExecutorConfig setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    public String toString() {
        return "ExecutorConfig{name='" + this.name + '\'' + ", poolSize=" + this.poolSize + ", capacity=" + this.capacity + ", quorumName=" + this.quorumName + '}';
    }

    DurableExecutorConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new DurableExecutorConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 31;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.poolSize);
        out.writeInt(this.durability);
        out.writeInt(this.capacity);
        out.writeUTF(this.quorumName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.poolSize = in.readInt();
        this.durability = in.readInt();
        this.capacity = in.readInt();
        this.quorumName = in.readUTF();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DurableExecutorConfig)) {
            return false;
        }
        DurableExecutorConfig that = (DurableExecutorConfig)o;
        if (this.poolSize != that.poolSize) {
            return false;
        }
        if (this.durability != that.durability) {
            return false;
        }
        if (this.capacity != that.capacity) {
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
        result = 31 * result + this.durability;
        result = 31 * result + this.capacity;
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        return result;
    }

    static class DurableExecutorConfigReadOnly
    extends DurableExecutorConfig {
        DurableExecutorConfigReadOnly(DurableExecutorConfig config) {
            super(config);
        }

        @Override
        public DurableExecutorConfig setName(String name) {
            throw new UnsupportedOperationException("This config is read-only durable executor: " + this.getName());
        }

        @Override
        public DurableExecutorConfig setPoolSize(int poolSize) {
            throw new UnsupportedOperationException("This config is read-only durable executor: " + this.getName());
        }

        @Override
        public DurableExecutorConfig setCapacity(int capacity) {
            throw new UnsupportedOperationException("This config is read-only durable executor: " + this.getName());
        }

        @Override
        public DurableExecutorConfig setDurability(int durability) {
            throw new UnsupportedOperationException("This config is read-only durable executor: " + this.getName());
        }

        @Override
        public DurableExecutorConfig setQuorumName(String quorumName) {
            throw new UnsupportedOperationException("This config is read-only durable executor: " + this.getName());
        }
    }
}

