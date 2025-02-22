/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.EvictionConfigReadOnly;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.internal.eviction.EvictionConfiguration;
import com.hazelcast.internal.eviction.EvictionPolicyComparator;
import com.hazelcast.internal.eviction.EvictionPolicyType;
import com.hazelcast.internal.eviction.EvictionStrategyType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.io.Serializable;

@BinaryInterface
public class EvictionConfig
implements EvictionConfiguration,
DataSerializable,
Serializable {
    public static final int DEFAULT_MAX_ENTRY_COUNT = 10000;
    public static final int DEFAULT_MAX_ENTRY_COUNT_FOR_ON_HEAP_MAP = Integer.MAX_VALUE;
    public static final MaxSizePolicy DEFAULT_MAX_SIZE_POLICY = MaxSizePolicy.ENTRY_COUNT;
    public static final EvictionPolicy DEFAULT_EVICTION_POLICY = EvictionPolicy.LRU;
    protected int size = 10000;
    protected MaxSizePolicy maxSizePolicy = DEFAULT_MAX_SIZE_POLICY;
    protected EvictionPolicy evictionPolicy = DEFAULT_EVICTION_POLICY;
    protected String comparatorClassName;
    protected EvictionPolicyComparator comparator;
    protected EvictionConfig readOnly;
    boolean sizeConfigured;

    public EvictionConfig() {
    }

    public EvictionConfig(int size, MaxSizePolicy maxSizePolicy, EvictionPolicy evictionPolicy) {
        this.sizeConfigured = true;
        this.size = Preconditions.checkPositive(size, "Size must be positive number!");
        this.maxSizePolicy = Preconditions.checkNotNull(maxSizePolicy, "Max-Size policy cannot be null!");
        this.evictionPolicy = Preconditions.checkNotNull(evictionPolicy, "Eviction policy cannot be null!");
    }

    public EvictionConfig(int size, MaxSizePolicy maxSizePolicy, String comparatorClassName) {
        this.sizeConfigured = true;
        this.size = Preconditions.checkPositive(size, "Size must be positive number!");
        this.maxSizePolicy = Preconditions.checkNotNull(maxSizePolicy, "Max-Size policy cannot be null!");
        this.comparatorClassName = Preconditions.checkNotNull(comparatorClassName, "Comparator classname cannot be null!");
    }

    public EvictionConfig(int size, MaxSizePolicy maxSizePolicy, EvictionPolicyComparator comparator) {
        this.sizeConfigured = true;
        this.size = Preconditions.checkPositive(size, "Size must be positive number!");
        this.maxSizePolicy = Preconditions.checkNotNull(maxSizePolicy, "Max-Size policy cannot be null!");
        this.comparator = Preconditions.checkNotNull(comparator, "Comparator cannot be null!");
    }

    public EvictionConfig(EvictionConfig config) {
        this.sizeConfigured = true;
        this.size = Preconditions.checkPositive(config.size, "Size must be positive number!");
        this.maxSizePolicy = Preconditions.checkNotNull(config.maxSizePolicy, "Max-Size policy cannot be null!");
        if (config.evictionPolicy != null) {
            this.evictionPolicy = config.evictionPolicy;
        }
        if (config.comparatorClassName != null) {
            this.comparatorClassName = config.comparatorClassName;
        }
        if (config.comparator != null) {
            this.comparator = config.comparator;
        }
    }

    public EvictionConfig getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new EvictionConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public int getSize() {
        return this.size;
    }

    public EvictionConfig setSize(int size) {
        this.sizeConfigured = true;
        this.size = Preconditions.checkPositive(size, "size must be positive number!");
        return this;
    }

    public MaxSizePolicy getMaximumSizePolicy() {
        return this.maxSizePolicy;
    }

    public EvictionConfig setMaximumSizePolicy(MaxSizePolicy maxSizePolicy) {
        this.maxSizePolicy = Preconditions.checkNotNull(maxSizePolicy, "maxSizePolicy cannot be null!");
        return this;
    }

    @Override
    public EvictionPolicy getEvictionPolicy() {
        return this.evictionPolicy;
    }

    public EvictionConfig setEvictionPolicy(EvictionPolicy evictionPolicy) {
        this.evictionPolicy = Preconditions.checkNotNull(evictionPolicy, "Eviction policy cannot be null!");
        return this;
    }

    @Override
    public EvictionStrategyType getEvictionStrategyType() {
        return EvictionStrategyType.DEFAULT_EVICTION_STRATEGY;
    }

    @Override
    @Deprecated
    public EvictionPolicyType getEvictionPolicyType() {
        switch (this.evictionPolicy) {
            case LFU: {
                return EvictionPolicyType.LFU;
            }
            case LRU: {
                return EvictionPolicyType.LRU;
            }
            case RANDOM: {
                return EvictionPolicyType.RANDOM;
            }
            case NONE: {
                return EvictionPolicyType.NONE;
            }
        }
        return null;
    }

    @Override
    public String getComparatorClassName() {
        return this.comparatorClassName;
    }

    public EvictionConfig setComparatorClassName(String comparatorClassName) {
        this.comparatorClassName = Preconditions.checkNotNull(comparatorClassName, "Eviction policy comparator class name cannot be null!");
        return this;
    }

    @Override
    public EvictionPolicyComparator getComparator() {
        return this.comparator;
    }

    public EvictionConfig setComparator(EvictionPolicyComparator comparator) {
        this.comparator = Preconditions.checkNotNull(comparator, "Eviction policy comparator cannot be null!");
        return this;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.size);
        out.writeUTF(this.maxSizePolicy.toString());
        out.writeUTF(this.evictionPolicy.toString());
        out.writeUTF(this.comparatorClassName);
        out.writeObject(this.comparator);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.size = in.readInt();
        this.maxSizePolicy = MaxSizePolicy.valueOf(in.readUTF());
        this.evictionPolicy = EvictionPolicy.valueOf(in.readUTF());
        this.comparatorClassName = in.readUTF();
        this.comparator = (EvictionPolicyComparator)in.readObject();
    }

    public String toString() {
        return "EvictionConfig{size=" + this.size + ", maxSizePolicy=" + (Object)((Object)this.maxSizePolicy) + ", evictionPolicy=" + (Object)((Object)this.evictionPolicy) + ", comparatorClassName=" + this.comparatorClassName + ", comparator=" + this.comparator + '}';
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EvictionConfig)) {
            return false;
        }
        EvictionConfig that = (EvictionConfig)o;
        if (this.size != that.size) {
            return false;
        }
        if (this.maxSizePolicy != that.maxSizePolicy) {
            return false;
        }
        if (this.evictionPolicy != that.evictionPolicy) {
            return false;
        }
        if (this.comparatorClassName != null ? !this.comparatorClassName.equals(that.comparatorClassName) : that.comparatorClassName != null) {
            return false;
        }
        return this.comparator != null ? this.comparator.equals(that.comparator) : that.comparator == null;
    }

    public final int hashCode() {
        int result = this.size;
        result = 31 * result + (this.maxSizePolicy != null ? this.maxSizePolicy.hashCode() : 0);
        result = 31 * result + (this.evictionPolicy != null ? this.evictionPolicy.hashCode() : 0);
        result = 31 * result + (this.comparatorClassName != null ? this.comparatorClassName.hashCode() : 0);
        result = 31 * result + (this.comparator != null ? this.comparator.hashCode() : 0);
        return result;
    }

    public static enum MaxSizePolicy {
        ENTRY_COUNT,
        USED_NATIVE_MEMORY_SIZE,
        USED_NATIVE_MEMORY_PERCENTAGE,
        FREE_NATIVE_MEMORY_SIZE,
        FREE_NATIVE_MEMORY_PERCENTAGE;

    }
}

