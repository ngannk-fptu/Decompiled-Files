/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.PartitioningStrategyConfigReadOnly;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class PartitioningStrategyConfig
implements IdentifiedDataSerializable {
    private String partitioningStrategyClass;
    private PartitioningStrategy partitioningStrategy;
    private transient PartitioningStrategyConfigReadOnly readOnly;

    public PartitioningStrategyConfig() {
    }

    public PartitioningStrategyConfig(PartitioningStrategyConfig config) {
        this.partitioningStrategyClass = config.getPartitioningStrategyClass();
        this.partitioningStrategy = config.getPartitioningStrategy();
    }

    public PartitioningStrategyConfig(String partitioningStrategyClass) {
        this.partitioningStrategyClass = partitioningStrategyClass;
    }

    public PartitioningStrategyConfig(PartitioningStrategy partitioningStrategy) {
        this.partitioningStrategy = partitioningStrategy;
    }

    public PartitioningStrategyConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new PartitioningStrategyConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public String getPartitioningStrategyClass() {
        return this.partitioningStrategyClass;
    }

    public PartitioningStrategyConfig setPartitioningStrategyClass(String partitionStrategyClass) {
        this.partitioningStrategyClass = partitionStrategyClass;
        return this;
    }

    public PartitioningStrategy getPartitioningStrategy() {
        return this.partitioningStrategy;
    }

    @Deprecated
    public PartitioningStrategyConfig setPartitionStrategy(PartitioningStrategy partitionStrategy) {
        this.partitioningStrategy = partitionStrategy;
        return this;
    }

    public PartitioningStrategyConfig setPartitioningStrategy(PartitioningStrategy partitionStrategy) {
        this.partitioningStrategy = partitionStrategy;
        return this;
    }

    public String toString() {
        return "PartitioningStrategyConfig{partitioningStrategyClass='" + this.partitioningStrategyClass + '\'' + ", partitioningStrategy=" + this.partitioningStrategy + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 20;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.partitioningStrategyClass);
        out.writeObject(this.partitioningStrategy);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.partitioningStrategyClass = in.readUTF();
        this.partitioningStrategy = (PartitioningStrategy)in.readObject();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PartitioningStrategyConfig that = (PartitioningStrategyConfig)o;
        if (this.partitioningStrategyClass != null ? !this.partitioningStrategyClass.equals(that.partitioningStrategyClass) : that.partitioningStrategyClass != null) {
            return false;
        }
        return this.partitioningStrategy != null ? this.partitioningStrategy.equals(that.partitioningStrategy) : that.partitioningStrategy == null;
    }

    public int hashCode() {
        int result = this.partitioningStrategyClass != null ? this.partitioningStrategyClass.hashCode() : 0;
        result = 31 * result + (this.partitioningStrategy != null ? this.partitioningStrategy.hashCode() : 0);
        return result;
    }
}

