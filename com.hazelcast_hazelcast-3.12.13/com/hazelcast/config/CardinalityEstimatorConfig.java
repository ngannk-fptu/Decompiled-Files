/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.merge.HyperLogLogMergePolicy;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.Arrays;

public class CardinalityEstimatorConfig
implements IdentifiedDataSerializable,
Versioned,
NamedConfig {
    public static final int DEFAULT_SYNC_BACKUP_COUNT = 1;
    public static final int DEFAULT_ASYNC_BACKUP_COUNT = 0;
    public static final MergePolicyConfig DEFAULT_MERGE_POLICY_CONFIG = new MergePolicyConfig(HyperLogLogMergePolicy.class.getSimpleName(), 100);
    private static final String[] ALLOWED_POLICIES = new String[]{"com.hazelcast.spi.merge.DiscardMergePolicy", "DiscardMergePolicy", "com.hazelcast.spi.merge.HyperLogLogMergePolicy", "HyperLogLogMergePolicy", "com.hazelcast.spi.merge.PassThroughMergePolicy", "PassThroughMergePolicy", "com.hazelcast.spi.merge.PutIfAbsentMergePolicy", "PutIfAbsentMergePolicy"};
    private String name = "default";
    private int backupCount = 1;
    private int asyncBackupCount = 0;
    private String quorumName;
    private MergePolicyConfig mergePolicyConfig = DEFAULT_MERGE_POLICY_CONFIG;
    private transient CardinalityEstimatorConfigReadOnly readOnly;

    public CardinalityEstimatorConfig() {
    }

    public CardinalityEstimatorConfig(String name) {
        this.name = name;
    }

    public CardinalityEstimatorConfig(String name, int backupCount, int asyncBackupCount) {
        this(name, backupCount, asyncBackupCount, DEFAULT_MERGE_POLICY_CONFIG);
    }

    public CardinalityEstimatorConfig(String name, int backupCount, int asyncBackupCount, MergePolicyConfig mergePolicyConfig) {
        this(name, backupCount, asyncBackupCount, "", mergePolicyConfig);
    }

    public CardinalityEstimatorConfig(String name, int backupCount, int asyncBackupCount, String quorumName, MergePolicyConfig mergePolicyConfig) {
        this.name = name;
        this.backupCount = Preconditions.checkBackupCount(backupCount, asyncBackupCount);
        this.asyncBackupCount = Preconditions.checkAsyncBackupCount(backupCount, asyncBackupCount);
        this.quorumName = quorumName;
        this.mergePolicyConfig = mergePolicyConfig;
        this.validate();
    }

    public CardinalityEstimatorConfig(CardinalityEstimatorConfig config) {
        this(config.getName(), config.getBackupCount(), config.getAsyncBackupCount(), config.getQuorumName(), config.getMergePolicyConfig());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public CardinalityEstimatorConfig setName(String name) {
        Preconditions.checkNotNull(name);
        this.name = name;
        return this;
    }

    public MergePolicyConfig getMergePolicyConfig() {
        return this.mergePolicyConfig;
    }

    public CardinalityEstimatorConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        this.mergePolicyConfig = Preconditions.checkNotNull(mergePolicyConfig, "mergePolicyConfig cannot be null");
        this.validate();
        return this;
    }

    public int getBackupCount() {
        return this.backupCount;
    }

    public CardinalityEstimatorConfig setBackupCount(int backupCount) {
        this.backupCount = Preconditions.checkBackupCount(backupCount, this.asyncBackupCount);
        return this;
    }

    public int getAsyncBackupCount() {
        return this.asyncBackupCount;
    }

    public CardinalityEstimatorConfig setAsyncBackupCount(int asyncBackupCount) {
        this.asyncBackupCount = Preconditions.checkAsyncBackupCount(this.backupCount, asyncBackupCount);
        return this;
    }

    public int getTotalBackupCount() {
        return this.backupCount + this.asyncBackupCount;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public CardinalityEstimatorConfig setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    public String toString() {
        return "CardinalityEstimatorConfig{name='" + this.name + '\'' + ", backupCount=" + this.backupCount + ", asyncBackupCount=" + this.asyncBackupCount + ", readOnly=" + this.readOnly + ", quorumName=" + this.quorumName + ", mergePolicyConfig=" + this.mergePolicyConfig + '}';
    }

    CardinalityEstimatorConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new CardinalityEstimatorConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 37;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.backupCount);
        out.writeInt(this.asyncBackupCount);
        out.writeUTF(this.quorumName);
        out.writeObject(this.mergePolicyConfig);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.backupCount = in.readInt();
        this.asyncBackupCount = in.readInt();
        this.quorumName = in.readUTF();
        this.mergePolicyConfig = (MergePolicyConfig)in.readObject();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CardinalityEstimatorConfig)) {
            return false;
        }
        CardinalityEstimatorConfig that = (CardinalityEstimatorConfig)o;
        if (this.backupCount != that.backupCount) {
            return false;
        }
        if (this.asyncBackupCount != that.asyncBackupCount) {
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
        result = 31 * result + this.backupCount;
        result = 31 * result + this.asyncBackupCount;
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        result = 31 * result + (this.mergePolicyConfig != null ? this.mergePolicyConfig.hashCode() : 0);
        return result;
    }

    public final void validate() {
        if (!Arrays.asList(ALLOWED_POLICIES).contains(this.mergePolicyConfig.getPolicy())) {
            throw new InvalidConfigurationException(String.format("Policy %s is not allowed as a merge-policy for CardinalityEstimator.", this.mergePolicyConfig.getPolicy()));
        }
    }

    static class CardinalityEstimatorConfigReadOnly
    extends CardinalityEstimatorConfig {
        CardinalityEstimatorConfigReadOnly(CardinalityEstimatorConfig config) {
            super(config);
        }

        @Override
        public CardinalityEstimatorConfig setName(String name) {
            throw new UnsupportedOperationException("This config is read-only cardinality estimator: " + this.getName());
        }

        @Override
        public CardinalityEstimatorConfig setBackupCount(int backupCount) {
            throw new UnsupportedOperationException("This config is read-only cardinality estimator: " + this.getName());
        }

        @Override
        public CardinalityEstimatorConfig setAsyncBackupCount(int asyncBackupCount) {
            throw new UnsupportedOperationException("This config is read-only cardinality estimator: " + this.getName());
        }

        @Override
        public CardinalityEstimatorConfig setQuorumName(String quorumName) {
            throw new UnsupportedOperationException("This config is read-only cardinality estimator: " + this.getName());
        }

        @Override
        public CardinalityEstimatorConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
            throw new UnsupportedOperationException("This config is read-only cardinality estimator: " + this.getName());
        }
    }
}

