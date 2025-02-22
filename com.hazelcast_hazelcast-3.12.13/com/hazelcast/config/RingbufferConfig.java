/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.config.RingbufferStoreConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.merge.SplitBrainMergeTypeProvider;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class RingbufferConfig
implements SplitBrainMergeTypeProvider,
IdentifiedDataSerializable,
Versioned,
NamedConfig {
    public static final int DEFAULT_CAPACITY = 10000;
    public static final int DEFAULT_SYNC_BACKUP_COUNT = 1;
    public static final int DEFAULT_ASYNC_BACKUP_COUNT = 0;
    public static final int DEFAULT_TTL_SECONDS = 0;
    public static final InMemoryFormat DEFAULT_IN_MEMORY_FORMAT = InMemoryFormat.BINARY;
    private String name;
    private int capacity = 10000;
    private int backupCount = 1;
    private int asyncBackupCount = 0;
    private int timeToLiveSeconds = 0;
    private InMemoryFormat inMemoryFormat = DEFAULT_IN_MEMORY_FORMAT;
    private RingbufferStoreConfig ringbufferStoreConfig = new RingbufferStoreConfig().setEnabled(false);
    private String quorumName;
    private MergePolicyConfig mergePolicyConfig = new MergePolicyConfig();

    public RingbufferConfig() {
    }

    public RingbufferConfig(String name) {
        this.name = Preconditions.checkNotNull(name, "name can't be null");
    }

    public RingbufferConfig(RingbufferConfig config) {
        Preconditions.checkNotNull(config, "config can't be null");
        this.name = config.name;
        this.capacity = config.capacity;
        this.backupCount = config.backupCount;
        this.asyncBackupCount = config.asyncBackupCount;
        this.timeToLiveSeconds = config.timeToLiveSeconds;
        this.inMemoryFormat = config.inMemoryFormat;
        if (config.ringbufferStoreConfig != null) {
            this.ringbufferStoreConfig = new RingbufferStoreConfig(config.ringbufferStoreConfig);
        }
        this.mergePolicyConfig = config.mergePolicyConfig;
        this.quorumName = config.quorumName;
    }

    public RingbufferConfig(String name, RingbufferConfig config) {
        this(config);
        this.name = Preconditions.checkNotNull(name, "name can't be null");
    }

    @Override
    public RingbufferConfig setName(String name) {
        this.name = Preconditions.checkHasText(name, "name must contain text");
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public RingbufferConfig setCapacity(int capacity) {
        this.capacity = Preconditions.checkPositive(capacity, "capacity can't be smaller than 1");
        return this;
    }

    public int getBackupCount() {
        return this.backupCount;
    }

    public RingbufferConfig setBackupCount(int backupCount) {
        this.backupCount = Preconditions.checkBackupCount(backupCount, this.asyncBackupCount);
        return this;
    }

    public int getAsyncBackupCount() {
        return this.asyncBackupCount;
    }

    public RingbufferConfig setAsyncBackupCount(int asyncBackupCount) {
        this.asyncBackupCount = Preconditions.checkAsyncBackupCount(this.backupCount, asyncBackupCount);
        return this;
    }

    public int getTotalBackupCount() {
        return this.backupCount + this.asyncBackupCount;
    }

    public int getTimeToLiveSeconds() {
        return this.timeToLiveSeconds;
    }

    public RingbufferConfig setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = Preconditions.checkNotNegative(timeToLiveSeconds, "timeToLiveSeconds can't be smaller than 0");
        return this;
    }

    public InMemoryFormat getInMemoryFormat() {
        return this.inMemoryFormat;
    }

    public RingbufferConfig setInMemoryFormat(InMemoryFormat inMemoryFormat) {
        Preconditions.checkNotNull(inMemoryFormat, "inMemoryFormat can't be null");
        Preconditions.checkFalse(inMemoryFormat == InMemoryFormat.NATIVE, "InMemoryFormat " + (Object)((Object)InMemoryFormat.NATIVE) + " is not supported");
        this.inMemoryFormat = inMemoryFormat;
        return this;
    }

    public RingbufferStoreConfig getRingbufferStoreConfig() {
        return this.ringbufferStoreConfig;
    }

    public RingbufferConfig setRingbufferStoreConfig(RingbufferStoreConfig ringbufferStoreConfig) {
        this.ringbufferStoreConfig = ringbufferStoreConfig;
        return this;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public RingbufferConfig setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    public MergePolicyConfig getMergePolicyConfig() {
        return this.mergePolicyConfig;
    }

    public RingbufferConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        this.mergePolicyConfig = mergePolicyConfig;
        return this;
    }

    public Class getProvidedMergeTypes() {
        return SplitBrainMergeTypes.RingbufferMergeTypes.class;
    }

    public String toString() {
        return "RingbufferConfig{name='" + this.name + '\'' + ", capacity=" + this.capacity + ", backupCount=" + this.backupCount + ", asyncBackupCount=" + this.asyncBackupCount + ", timeToLiveSeconds=" + this.timeToLiveSeconds + ", inMemoryFormat=" + (Object)((Object)this.inMemoryFormat) + ", ringbufferStoreConfig=" + this.ringbufferStoreConfig + ", quorumName=" + this.quorumName + ", mergePolicyConfig=" + this.mergePolicyConfig + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 35;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.capacity);
        out.writeInt(this.backupCount);
        out.writeInt(this.asyncBackupCount);
        out.writeInt(this.timeToLiveSeconds);
        out.writeUTF(this.inMemoryFormat.name());
        out.writeObject(this.ringbufferStoreConfig);
        out.writeUTF(this.quorumName);
        out.writeObject(this.mergePolicyConfig);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.capacity = in.readInt();
        this.backupCount = in.readInt();
        this.asyncBackupCount = in.readInt();
        this.timeToLiveSeconds = in.readInt();
        this.inMemoryFormat = InMemoryFormat.valueOf(in.readUTF());
        this.ringbufferStoreConfig = (RingbufferStoreConfig)in.readObject();
        this.quorumName = in.readUTF();
        this.mergePolicyConfig = (MergePolicyConfig)in.readObject();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RingbufferConfig)) {
            return false;
        }
        RingbufferConfig that = (RingbufferConfig)o;
        if (this.capacity != that.capacity) {
            return false;
        }
        if (this.backupCount != that.backupCount) {
            return false;
        }
        if (this.asyncBackupCount != that.asyncBackupCount) {
            return false;
        }
        if (this.timeToLiveSeconds != that.timeToLiveSeconds) {
            return false;
        }
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (this.inMemoryFormat != that.inMemoryFormat) {
            return false;
        }
        if (this.ringbufferStoreConfig != null ? !this.ringbufferStoreConfig.equals(that.ringbufferStoreConfig) : that.ringbufferStoreConfig != null) {
            return false;
        }
        if (this.quorumName != null ? !this.quorumName.equals(that.quorumName) : that.quorumName != null) {
            return false;
        }
        return this.mergePolicyConfig != null ? this.mergePolicyConfig.equals(that.mergePolicyConfig) : that.mergePolicyConfig == null;
    }

    public final int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.capacity;
        result = 31 * result + this.backupCount;
        result = 31 * result + this.asyncBackupCount;
        result = 31 * result + this.timeToLiveSeconds;
        result = 31 * result + (this.inMemoryFormat != null ? this.inMemoryFormat.hashCode() : 0);
        result = 31 * result + (this.ringbufferStoreConfig != null ? this.ringbufferStoreConfig.hashCode() : 0);
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        result = 31 * result + (this.mergePolicyConfig != null ? this.mergePolicyConfig.hashCode() : 0);
        return result;
    }

    public RingbufferConfig getAsReadOnly() {
        return new RingbufferConfigReadOnly(this);
    }

    private static class RingbufferConfigReadOnly
    extends RingbufferConfig {
        RingbufferConfigReadOnly(RingbufferConfig config) {
            super(config);
        }

        @Override
        public RingbufferStoreConfig getRingbufferStoreConfig() {
            RingbufferStoreConfig storeConfig = super.getRingbufferStoreConfig();
            if (storeConfig != null) {
                return storeConfig.getAsReadOnly();
            }
            return null;
        }

        @Override
        public RingbufferConfig setCapacity(int capacity) {
            throw this.throwReadOnly();
        }

        @Override
        public RingbufferConfig setAsyncBackupCount(int asyncBackupCount) {
            throw this.throwReadOnly();
        }

        @Override
        public RingbufferConfig setBackupCount(int backupCount) {
            throw this.throwReadOnly();
        }

        @Override
        public RingbufferConfig setTimeToLiveSeconds(int timeToLiveSeconds) {
            throw this.throwReadOnly();
        }

        @Override
        public RingbufferConfig setInMemoryFormat(InMemoryFormat inMemoryFormat) {
            throw this.throwReadOnly();
        }

        @Override
        public RingbufferConfig setRingbufferStoreConfig(RingbufferStoreConfig ringbufferStoreConfig) {
            throw this.throwReadOnly();
        }

        @Override
        public RingbufferConfig setQuorumName(String quorumName) {
            throw this.throwReadOnly();
        }

        @Override
        public RingbufferConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
            throw this.throwReadOnly();
        }

        private UnsupportedOperationException throwReadOnly() {
            throw new UnsupportedOperationException("This config is read-only");
        }
    }
}

