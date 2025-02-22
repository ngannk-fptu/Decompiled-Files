/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.MultiMapConfigReadOnly;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.merge.SplitBrainMergeTypeProvider;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultiMapConfig
implements SplitBrainMergeTypeProvider,
IdentifiedDataSerializable,
Versioned,
NamedConfig {
    public static final int DEFAULT_SYNC_BACKUP_COUNT = 1;
    public static final int DEFAULT_ASYNC_BACKUP_COUNT = 0;
    public static final ValueCollectionType DEFAULT_VALUE_COLLECTION_TYPE = ValueCollectionType.SET;
    private String name;
    private String valueCollectionType = DEFAULT_VALUE_COLLECTION_TYPE.toString();
    private List<EntryListenerConfig> listenerConfigs = new ArrayList<EntryListenerConfig>();
    private boolean binary = true;
    private int backupCount = 1;
    private int asyncBackupCount = 0;
    private boolean statisticsEnabled = true;
    private String quorumName;
    private MergePolicyConfig mergePolicyConfig = new MergePolicyConfig();
    private transient MultiMapConfigReadOnly readOnly;

    public MultiMapConfig() {
    }

    public MultiMapConfig(String name) {
        this.setName(name);
    }

    public MultiMapConfig(MultiMapConfig config) {
        this.name = config.getName();
        this.valueCollectionType = config.valueCollectionType;
        this.listenerConfigs.addAll(config.listenerConfigs);
        this.binary = config.binary;
        this.backupCount = config.backupCount;
        this.asyncBackupCount = config.asyncBackupCount;
        this.statisticsEnabled = config.statisticsEnabled;
        this.quorumName = config.quorumName;
        this.mergePolicyConfig = config.mergePolicyConfig;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public MultiMapConfig setName(String name) {
        this.name = name;
        return this;
    }

    public ValueCollectionType getValueCollectionType() {
        return ValueCollectionType.valueOf(this.valueCollectionType.toUpperCase(StringUtil.LOCALE_INTERNAL));
    }

    public MultiMapConfig setValueCollectionType(String valueCollectionType) {
        this.valueCollectionType = valueCollectionType;
        return this;
    }

    public MultiMapConfig setValueCollectionType(ValueCollectionType valueCollectionType) {
        this.valueCollectionType = valueCollectionType.toString();
        return this;
    }

    public MultiMapConfig addEntryListenerConfig(EntryListenerConfig listenerConfig) {
        this.getEntryListenerConfigs().add(listenerConfig);
        return this;
    }

    public List<EntryListenerConfig> getEntryListenerConfigs() {
        return this.listenerConfigs;
    }

    public MultiMapConfig setEntryListenerConfigs(List<EntryListenerConfig> listenerConfigs) {
        this.listenerConfigs = listenerConfigs;
        return this;
    }

    public boolean isBinary() {
        return this.binary;
    }

    public MultiMapConfig setBinary(boolean binary) {
        this.binary = binary;
        return this;
    }

    @Deprecated
    public int getSyncBackupCount() {
        return this.backupCount;
    }

    @Deprecated
    public MultiMapConfig setSyncBackupCount(int syncBackupCount) {
        this.backupCount = syncBackupCount;
        return this;
    }

    public int getBackupCount() {
        return this.backupCount;
    }

    public MultiMapConfig setBackupCount(int backupCount) {
        this.backupCount = Preconditions.checkBackupCount(backupCount, this.asyncBackupCount);
        return this;
    }

    public int getAsyncBackupCount() {
        return this.asyncBackupCount;
    }

    public MultiMapConfig setAsyncBackupCount(int asyncBackupCount) {
        this.asyncBackupCount = Preconditions.checkAsyncBackupCount(this.backupCount, asyncBackupCount);
        return this;
    }

    public int getTotalBackupCount() {
        return this.backupCount + this.asyncBackupCount;
    }

    public boolean isStatisticsEnabled() {
        return this.statisticsEnabled;
    }

    public MultiMapConfig setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
        return this;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public MultiMapConfig setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    public MergePolicyConfig getMergePolicyConfig() {
        return this.mergePolicyConfig;
    }

    public MultiMapConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        this.mergePolicyConfig = Preconditions.checkNotNull(mergePolicyConfig, "mergePolicyConfig cannot be null");
        return this;
    }

    public Class getProvidedMergeTypes() {
        return SplitBrainMergeTypes.MultiMapMergeTypes.class;
    }

    public String toString() {
        return "MultiMapConfig{name='" + this.name + '\'' + ", valueCollectionType='" + this.valueCollectionType + '\'' + ", listenerConfigs=" + this.listenerConfigs + ", binary=" + this.binary + ", backupCount=" + this.backupCount + ", asyncBackupCount=" + this.asyncBackupCount + ", quorumName=" + this.quorumName + ", mergePolicyConfig=" + this.mergePolicyConfig + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeUTF(this.valueCollectionType);
        if (this.listenerConfigs == null || this.listenerConfigs.isEmpty()) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeInt(this.listenerConfigs.size());
            for (ListenerConfig listenerConfig : this.listenerConfigs) {
                out.writeObject(listenerConfig);
            }
        }
        out.writeBoolean(this.binary);
        out.writeInt(this.backupCount);
        out.writeInt(this.asyncBackupCount);
        out.writeBoolean(this.statisticsEnabled);
        out.writeUTF(this.quorumName);
        out.writeObject(this.mergePolicyConfig);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.valueCollectionType = in.readUTF();
        boolean hasListenerConfig = in.readBoolean();
        if (hasListenerConfig) {
            int configSize = in.readInt();
            this.listenerConfigs = new ArrayList<EntryListenerConfig>(configSize);
            for (int i = 0; i < configSize; ++i) {
                EntryListenerConfig listenerConfig = (EntryListenerConfig)in.readObject();
                this.listenerConfigs.add(listenerConfig);
            }
        }
        this.binary = in.readBoolean();
        this.backupCount = in.readInt();
        this.asyncBackupCount = in.readInt();
        this.statisticsEnabled = in.readBoolean();
        this.quorumName = in.readUTF();
        this.mergePolicyConfig = (MergePolicyConfig)in.readObject();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultiMapConfig)) {
            return false;
        }
        MultiMapConfig that = (MultiMapConfig)o;
        if (this.binary != that.binary) {
            return false;
        }
        if (this.backupCount != that.backupCount) {
            return false;
        }
        if (this.asyncBackupCount != that.asyncBackupCount) {
            return false;
        }
        if (this.statisticsEnabled != that.statisticsEnabled) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        if (this.valueCollectionType != null ? !this.valueCollectionType.equals(that.valueCollectionType) : that.valueCollectionType != null) {
            return false;
        }
        if (this.listenerConfigs != null ? !this.listenerConfigs.equals(that.listenerConfigs) : that.listenerConfigs != null) {
            return false;
        }
        if (this.quorumName != null ? !this.quorumName.equals(that.quorumName) : that.quorumName != null) {
            return false;
        }
        return this.mergePolicyConfig != null ? this.mergePolicyConfig.equals(that.mergePolicyConfig) : that.mergePolicyConfig == null;
    }

    public final int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.valueCollectionType != null ? this.valueCollectionType.hashCode() : 0);
        result = 31 * result + (this.listenerConfigs != null ? this.listenerConfigs.hashCode() : 0);
        result = 31 * result + (this.binary ? 1 : 0);
        result = 31 * result + this.backupCount;
        result = 31 * result + this.asyncBackupCount;
        result = 31 * result + (this.statisticsEnabled ? 1 : 0);
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        result = 31 * result + (this.mergePolicyConfig != null ? this.mergePolicyConfig.hashCode() : 0);
        return result;
    }

    public MultiMapConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new MultiMapConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public static enum ValueCollectionType {
        SET,
        LIST;

    }
}

