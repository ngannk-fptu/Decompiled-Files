/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.config.QueueConfigReadOnly;
import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.merge.SplitBrainMergeTypeProvider;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueueConfig
implements SplitBrainMergeTypeProvider,
IdentifiedDataSerializable,
Versioned,
NamedConfig {
    public static final int DEFAULT_MAX_SIZE = 0;
    public static final int DEFAULT_SYNC_BACKUP_COUNT = 1;
    public static final int DEFAULT_ASYNC_BACKUP_COUNT = 0;
    public static final int DEFAULT_EMPTY_QUEUE_TTL = -1;
    private String name;
    private List<ItemListenerConfig> listenerConfigs;
    private int backupCount = 1;
    private int asyncBackupCount = 0;
    private int maxSize = 0;
    private int emptyQueueTtl = -1;
    private QueueStoreConfig queueStoreConfig;
    private boolean statisticsEnabled = true;
    private String quorumName;
    private MergePolicyConfig mergePolicyConfig = new MergePolicyConfig();
    private transient QueueConfigReadOnly readOnly;

    public QueueConfig() {
    }

    public QueueConfig(String name) {
        this.setName(name);
    }

    public QueueConfig(QueueConfig config) {
        this();
        this.name = config.name;
        this.backupCount = config.backupCount;
        this.asyncBackupCount = config.asyncBackupCount;
        this.maxSize = config.maxSize;
        this.emptyQueueTtl = config.emptyQueueTtl;
        this.statisticsEnabled = config.statisticsEnabled;
        this.quorumName = config.quorumName;
        this.mergePolicyConfig = config.mergePolicyConfig;
        this.queueStoreConfig = config.queueStoreConfig != null ? new QueueStoreConfig(config.queueStoreConfig) : null;
        this.listenerConfigs = new ArrayList<ItemListenerConfig>(config.getItemListenerConfigs());
    }

    public QueueConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new QueueConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public int getEmptyQueueTtl() {
        return this.emptyQueueTtl;
    }

    public QueueConfig setEmptyQueueTtl(int emptyQueueTtl) {
        this.emptyQueueTtl = emptyQueueTtl;
        return this;
    }

    public int getMaxSize() {
        return this.maxSize == 0 ? Integer.MAX_VALUE : this.maxSize;
    }

    public QueueConfig setMaxSize(int maxSize) {
        if (maxSize < 0) {
            throw new IllegalArgumentException("Size of the queue can not be a negative value!");
        }
        this.maxSize = maxSize;
        return this;
    }

    public int getTotalBackupCount() {
        return this.backupCount + this.asyncBackupCount;
    }

    public int getBackupCount() {
        return this.backupCount;
    }

    public QueueConfig setBackupCount(int backupCount) {
        this.backupCount = Preconditions.checkBackupCount(backupCount, this.asyncBackupCount);
        return this;
    }

    public int getAsyncBackupCount() {
        return this.asyncBackupCount;
    }

    public QueueConfig setAsyncBackupCount(int asyncBackupCount) {
        this.asyncBackupCount = Preconditions.checkAsyncBackupCount(this.backupCount, asyncBackupCount);
        return this;
    }

    public QueueStoreConfig getQueueStoreConfig() {
        return this.queueStoreConfig;
    }

    public QueueConfig setQueueStoreConfig(QueueStoreConfig queueStoreConfig) {
        this.queueStoreConfig = queueStoreConfig;
        return this;
    }

    public boolean isStatisticsEnabled() {
        return this.statisticsEnabled;
    }

    public QueueConfig setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public QueueConfig setName(String name) {
        this.name = name;
        return this;
    }

    public QueueConfig addItemListenerConfig(ItemListenerConfig listenerConfig) {
        this.getItemListenerConfigs().add(listenerConfig);
        return this;
    }

    public List<ItemListenerConfig> getItemListenerConfigs() {
        if (this.listenerConfigs == null) {
            this.listenerConfigs = new ArrayList<ItemListenerConfig>();
        }
        return this.listenerConfigs;
    }

    public QueueConfig setItemListenerConfigs(List<ItemListenerConfig> listenerConfigs) {
        this.listenerConfigs = listenerConfigs;
        return this;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public QueueConfig setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    public MergePolicyConfig getMergePolicyConfig() {
        return this.mergePolicyConfig;
    }

    public QueueConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        this.mergePolicyConfig = Preconditions.checkNotNull(mergePolicyConfig, "mergePolicyConfig cannot be null");
        return this;
    }

    public Class getProvidedMergeTypes() {
        return SplitBrainMergeTypes.QueueMergeTypes.class;
    }

    public String toString() {
        return "QueueConfig{name='" + this.name + '\'' + ", listenerConfigs=" + this.listenerConfigs + ", backupCount=" + this.backupCount + ", asyncBackupCount=" + this.asyncBackupCount + ", maxSize=" + this.maxSize + ", emptyQueueTtl=" + this.emptyQueueTtl + ", queueStoreConfig=" + this.queueStoreConfig + ", statisticsEnabled=" + this.statisticsEnabled + ", mergePolicyConfig=" + this.mergePolicyConfig + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 26;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        SerializationUtil.writeNullableList(this.listenerConfigs, out);
        out.writeInt(this.backupCount);
        out.writeInt(this.asyncBackupCount);
        out.writeInt(this.maxSize);
        out.writeInt(this.emptyQueueTtl);
        out.writeObject(this.queueStoreConfig);
        out.writeBoolean(this.statisticsEnabled);
        out.writeUTF(this.quorumName);
        out.writeObject(this.mergePolicyConfig);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.listenerConfigs = SerializationUtil.readNullableList(in);
        this.backupCount = in.readInt();
        this.asyncBackupCount = in.readInt();
        this.maxSize = in.readInt();
        this.emptyQueueTtl = in.readInt();
        this.queueStoreConfig = (QueueStoreConfig)in.readObject();
        this.statisticsEnabled = in.readBoolean();
        this.quorumName = in.readUTF();
        this.mergePolicyConfig = (MergePolicyConfig)in.readObject();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueueConfig)) {
            return false;
        }
        QueueConfig that = (QueueConfig)o;
        if (this.backupCount != that.backupCount) {
            return false;
        }
        if (this.asyncBackupCount != that.asyncBackupCount) {
            return false;
        }
        if (this.getMaxSize() != that.getMaxSize()) {
            return false;
        }
        if (this.emptyQueueTtl != that.emptyQueueTtl) {
            return false;
        }
        if (this.statisticsEnabled != that.statisticsEnabled) {
            return false;
        }
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (!this.getItemListenerConfigs().equals(that.getItemListenerConfigs())) {
            return false;
        }
        if (this.queueStoreConfig != null ? !this.queueStoreConfig.equals(that.queueStoreConfig) : that.queueStoreConfig != null) {
            return false;
        }
        if (this.quorumName != null ? !this.quorumName.equals(that.quorumName) : that.quorumName != null) {
            return false;
        }
        return this.mergePolicyConfig != null ? this.mergePolicyConfig.equals(that.mergePolicyConfig) : that.mergePolicyConfig == null;
    }

    public final int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.getItemListenerConfigs().hashCode();
        result = 31 * result + this.backupCount;
        result = 31 * result + this.asyncBackupCount;
        result = 31 * result + this.getMaxSize();
        result = 31 * result + this.emptyQueueTtl;
        result = 31 * result + (this.queueStoreConfig != null ? this.queueStoreConfig.hashCode() : 0);
        result = 31 * result + (this.statisticsEnabled ? 1 : 0);
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        result = 31 * result + (this.mergePolicyConfig != null ? this.mergePolicyConfig.hashCode() : 0);
        return result;
    }
}

