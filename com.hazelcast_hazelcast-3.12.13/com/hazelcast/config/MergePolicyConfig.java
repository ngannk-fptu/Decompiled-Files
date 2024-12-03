/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.merge.PutIfAbsentMergePolicy;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class MergePolicyConfig
implements IdentifiedDataSerializable {
    public static final String DEFAULT_MERGE_POLICY = PutIfAbsentMergePolicy.class.getName();
    public static final int DEFAULT_BATCH_SIZE = 100;
    private String policy = DEFAULT_MERGE_POLICY;
    private int batchSize = 100;
    private MergePolicyConfig readOnly;

    public MergePolicyConfig() {
    }

    public MergePolicyConfig(String policy, int batchSize) {
        this.setPolicy(policy);
        this.setBatchSize(batchSize);
    }

    public MergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        this.policy = mergePolicyConfig.policy;
        this.batchSize = mergePolicyConfig.batchSize;
    }

    public String getPolicy() {
        return this.policy;
    }

    public MergePolicyConfig setPolicy(String policy) {
        this.policy = Preconditions.checkHasText(policy, "Merge policy must contain text!");
        return this;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public MergePolicyConfig setBatchSize(int batchSize) {
        this.batchSize = Preconditions.checkPositive(batchSize, "batchSize must be a positive number!");
        return this;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 51;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.policy);
        out.writeInt(this.batchSize);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.policy = in.readUTF();
        this.batchSize = in.readInt();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MergePolicyConfig)) {
            return false;
        }
        MergePolicyConfig that = (MergePolicyConfig)o;
        if (this.batchSize != that.batchSize) {
            return false;
        }
        return this.policy != null ? this.policy.equals(that.policy) : that.policy == null;
    }

    public final int hashCode() {
        int result = this.policy != null ? this.policy.hashCode() : 0;
        result = 31 * result + this.batchSize;
        return result;
    }

    public String toString() {
        return "MergePolicyConfig{policy='" + this.policy + '\'' + ", batchSize=" + this.batchSize + '}';
    }

    public MergePolicyConfig getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new MergePolicyConfigReadOnly(this);
        }
        return this.readOnly;
    }

    private static class MergePolicyConfigReadOnly
    extends MergePolicyConfig {
        MergePolicyConfigReadOnly(MergePolicyConfig mergePolicyConfig) {
            super(mergePolicyConfig);
        }

        @Override
        public MergePolicyConfig setPolicy(String policy) {
            throw new UnsupportedOperationException("This is a read-only configuration");
        }

        @Override
        public MergePolicyConfig setBatchSize(int batchSize) {
            throw new UnsupportedOperationException("This is a read-only configuration");
        }
    }
}

