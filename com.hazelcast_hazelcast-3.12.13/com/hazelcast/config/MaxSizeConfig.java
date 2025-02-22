/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.MaxSizeConfigReadOnly;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;
import java.io.Serializable;

@BinaryInterface
public class MaxSizeConfig
implements DataSerializable,
Serializable {
    public static final int DEFAULT_MAX_SIZE = Integer.MAX_VALUE;
    private MaxSizeConfigReadOnly readOnly;
    private MaxSizePolicy maxSizePolicy = MaxSizePolicy.PER_NODE;
    private int size = Integer.MAX_VALUE;

    public MaxSizeConfig() {
    }

    public MaxSizeConfig(int size, MaxSizePolicy maxSizePolicy) {
        this.setSize(size);
        this.maxSizePolicy = maxSizePolicy;
    }

    public MaxSizeConfig(MaxSizeConfig config) {
        this.size = config.size;
        this.maxSizePolicy = config.maxSizePolicy;
    }

    public MaxSizeConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new MaxSizeConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public int getSize() {
        return this.size;
    }

    public MaxSizeConfig setSize(int size) {
        if (size > 0) {
            this.size = size;
        }
        return this;
    }

    public MaxSizePolicy getMaxSizePolicy() {
        return this.maxSizePolicy;
    }

    public MaxSizeConfig setMaxSizePolicy(MaxSizePolicy maxSizePolicy) {
        this.maxSizePolicy = maxSizePolicy;
        return this;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.maxSizePolicy.toString());
        out.writeInt(this.size);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.maxSizePolicy = MaxSizePolicy.valueOf(in.readUTF());
        this.size = in.readInt();
    }

    public String toString() {
        return "MaxSizeConfig{maxSizePolicy='" + (Object)((Object)this.maxSizePolicy) + '\'' + ", size=" + this.size + '}';
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MaxSizeConfig)) {
            return false;
        }
        MaxSizeConfig that = (MaxSizeConfig)o;
        if (this.size != that.size) {
            return false;
        }
        return this.maxSizePolicy == that.maxSizePolicy;
    }

    public final int hashCode() {
        int result = this.maxSizePolicy != null ? this.maxSizePolicy.hashCode() : 0;
        result = 31 * result + this.size;
        return result;
    }

    public static enum MaxSizePolicy {
        PER_NODE,
        PER_PARTITION,
        USED_HEAP_PERCENTAGE,
        USED_HEAP_SIZE,
        FREE_HEAP_PERCENTAGE,
        FREE_HEAP_SIZE,
        USED_NATIVE_MEMORY_SIZE,
        USED_NATIVE_MEMORY_PERCENTAGE,
        FREE_NATIVE_MEMORY_SIZE,
        FREE_NATIVE_MEMORY_PERCENTAGE;

    }
}

