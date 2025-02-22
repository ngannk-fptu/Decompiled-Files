/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.NamedConfig;
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

public abstract class CollectionConfig<T extends CollectionConfig>
implements SplitBrainMergeTypeProvider,
IdentifiedDataSerializable,
Versioned,
NamedConfig {
    public static final int DEFAULT_MAX_SIZE = 0;
    public static final int DEFAULT_SYNC_BACKUP_COUNT = 1;
    public static final int DEFAULT_ASYNC_BACKUP_COUNT = 0;
    private String name;
    private List<ItemListenerConfig> listenerConfigs;
    private int backupCount = 1;
    private int asyncBackupCount = 0;
    private int maxSize = 0;
    private boolean statisticsEnabled = true;
    private String quorumName;
    private MergePolicyConfig mergePolicyConfig = new MergePolicyConfig();

    protected CollectionConfig() {
    }

    protected CollectionConfig(CollectionConfig config) {
        this.name = config.name;
        this.listenerConfigs = new ArrayList<ItemListenerConfig>(config.getItemListenerConfigs());
        this.backupCount = config.backupCount;
        this.asyncBackupCount = config.asyncBackupCount;
        this.maxSize = config.maxSize;
        this.statisticsEnabled = config.statisticsEnabled;
        this.quorumName = config.quorumName;
        this.mergePolicyConfig = config.mergePolicyConfig;
    }

    public abstract T getAsReadOnly();

    @Override
    public String getName() {
        return this.name;
    }

    public T setName(String name) {
        this.name = name;
        return (T)this;
    }

    public List<ItemListenerConfig> getItemListenerConfigs() {
        if (this.listenerConfigs == null) {
            this.listenerConfigs = new ArrayList<ItemListenerConfig>();
        }
        return this.listenerConfigs;
    }

    public T setItemListenerConfigs(List<ItemListenerConfig> listenerConfigs) {
        this.listenerConfigs = listenerConfigs;
        return (T)this;
    }

    public int getTotalBackupCount() {
        return this.backupCount + this.asyncBackupCount;
    }

    public int getBackupCount() {
        return this.backupCount;
    }

    public T setBackupCount(int backupCount) {
        this.backupCount = Preconditions.checkBackupCount(backupCount, this.asyncBackupCount);
        return (T)this;
    }

    public int getAsyncBackupCount() {
        return this.asyncBackupCount;
    }

    public T setAsyncBackupCount(int asyncBackupCount) {
        this.asyncBackupCount = Preconditions.checkAsyncBackupCount(asyncBackupCount, asyncBackupCount);
        return (T)this;
    }

    public int getMaxSize() {
        return this.maxSize == 0 ? Integer.MAX_VALUE : this.maxSize;
    }

    public T setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return (T)this;
    }

    public boolean isStatisticsEnabled() {
        return this.statisticsEnabled;
    }

    public T setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
        return (T)this;
    }

    public void addItemListenerConfig(ItemListenerConfig itemListenerConfig) {
        this.getItemListenerConfigs().add(itemListenerConfig);
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public T setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return (T)this;
    }

    public MergePolicyConfig getMergePolicyConfig() {
        return this.mergePolicyConfig;
    }

    public T setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        this.mergePolicyConfig = Preconditions.checkNotNull(mergePolicyConfig, "mergePolicyConfig cannot be null");
        return (T)this;
    }

    public Class getProvidedMergeTypes() {
        return SplitBrainMergeTypes.CollectionMergeTypes.class;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        SerializationUtil.writeNullableList(this.listenerConfigs, out);
        out.writeInt(this.backupCount);
        out.writeInt(this.asyncBackupCount);
        out.writeInt(this.maxSize);
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
        this.statisticsEnabled = in.readBoolean();
        this.quorumName = in.readUTF();
        this.mergePolicyConfig = (MergePolicyConfig)in.readObject();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CollectionConfig)) {
            return false;
        }
        CollectionConfig that = (CollectionConfig)o;
        if (this.backupCount != that.backupCount) {
            return false;
        }
        if (this.asyncBackupCount != that.asyncBackupCount) {
            return false;
        }
        if (this.getMaxSize() != that.getMaxSize()) {
            return false;
        }
        if (this.statisticsEnabled != that.statisticsEnabled) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        if (this.quorumName != null ? !this.quorumName.equals(that.quorumName) : that.quorumName != null) {
            return false;
        }
        if (this.mergePolicyConfig != null ? !this.mergePolicyConfig.equals(that.mergePolicyConfig) : that.mergePolicyConfig != null) {
            return false;
        }
        return this.getItemListenerConfigs().equals(that.getItemListenerConfigs());
    }

    public final int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + this.getItemListenerConfigs().hashCode();
        result = 31 * result + this.backupCount;
        result = 31 * result + this.asyncBackupCount;
        result = 31 * result + this.getMaxSize();
        result = 31 * result + (this.statisticsEnabled ? 1 : 0);
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        result = 31 * result + (this.mergePolicyConfig != null ? this.mergePolicyConfig.hashCode() : 0);
        return result;
    }

    protected String fieldsToString() {
        return "name='" + this.name + "', listenerConfigs=" + this.listenerConfigs + ", backupCount=" + this.backupCount + ", asyncBackupCount=" + this.asyncBackupCount + ", maxSize=" + this.maxSize + ", statisticsEnabled=" + this.statisticsEnabled + ", quorumName='" + this.quorumName + "', mergePolicyConfig='" + this.mergePolicyConfig + "'";
    }
}

