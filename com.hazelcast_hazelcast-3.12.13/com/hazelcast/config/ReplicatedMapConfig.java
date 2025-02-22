/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.config.ReplicatedMapConfigReadOnly;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.merge.PutIfAbsentMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypeProvider;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class ReplicatedMapConfig
implements SplitBrainMergeTypeProvider,
IdentifiedDataSerializable,
Versioned,
NamedConfig {
    public static final int DEFAULT_CONCURRENCY_LEVEL = 32;
    public static final int DEFAULT_REPLICATION_DELAY_MILLIS = 100;
    public static final InMemoryFormat DEFAULT_IN_MEMORY_FORMAT = InMemoryFormat.OBJECT;
    public static final boolean DEFAULT_ASNYC_FILLUP = true;
    public static final String DEFAULT_MERGE_POLICY = PutIfAbsentMergePolicy.class.getName();
    private String name;
    private transient int concurrencyLevel = 32;
    private transient long replicationDelayMillis = 100L;
    private InMemoryFormat inMemoryFormat = DEFAULT_IN_MEMORY_FORMAT;
    private transient ScheduledExecutorService replicatorExecutorService;
    private boolean asyncFillup = true;
    private boolean statisticsEnabled = true;
    private MergePolicyConfig mergePolicyConfig = new MergePolicyConfig();
    private List<ListenerConfig> listenerConfigs;
    private String quorumName;
    private volatile transient ReplicatedMapConfigReadOnly readOnly;

    public ReplicatedMapConfig() {
    }

    public ReplicatedMapConfig(String name) {
        this.setName(name);
    }

    public ReplicatedMapConfig(ReplicatedMapConfig replicatedMapConfig) {
        this.name = replicatedMapConfig.name;
        this.inMemoryFormat = replicatedMapConfig.inMemoryFormat;
        this.concurrencyLevel = replicatedMapConfig.concurrencyLevel;
        this.replicationDelayMillis = replicatedMapConfig.replicationDelayMillis;
        this.replicatorExecutorService = replicatedMapConfig.replicatorExecutorService;
        this.listenerConfigs = replicatedMapConfig.listenerConfigs == null ? null : new ArrayList<ListenerConfig>(replicatedMapConfig.getListenerConfigs());
        this.asyncFillup = replicatedMapConfig.asyncFillup;
        this.statisticsEnabled = replicatedMapConfig.statisticsEnabled;
        this.mergePolicyConfig = replicatedMapConfig.mergePolicyConfig;
        this.quorumName = replicatedMapConfig.quorumName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ReplicatedMapConfig setName(String name) {
        this.name = name;
        return this;
    }

    @Deprecated
    public long getReplicationDelayMillis() {
        return this.replicationDelayMillis;
    }

    @Deprecated
    public ReplicatedMapConfig setReplicationDelayMillis(long replicationDelayMillis) {
        this.replicationDelayMillis = replicationDelayMillis;
        return this;
    }

    @Deprecated
    public int getConcurrencyLevel() {
        return this.concurrencyLevel;
    }

    @Deprecated
    public ReplicatedMapConfig setConcurrencyLevel(int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }

    public InMemoryFormat getInMemoryFormat() {
        return this.inMemoryFormat;
    }

    public ReplicatedMapConfig setInMemoryFormat(InMemoryFormat inMemoryFormat) {
        this.inMemoryFormat = inMemoryFormat;
        return this;
    }

    @Deprecated
    public ScheduledExecutorService getReplicatorExecutorService() {
        return this.replicatorExecutorService;
    }

    @Deprecated
    public ReplicatedMapConfig setReplicatorExecutorService(ScheduledExecutorService replicatorExecutorService) {
        this.replicatorExecutorService = replicatorExecutorService;
        return this;
    }

    public List<ListenerConfig> getListenerConfigs() {
        if (this.listenerConfigs == null) {
            this.listenerConfigs = new ArrayList<ListenerConfig>();
        }
        return this.listenerConfigs;
    }

    public ReplicatedMapConfig setListenerConfigs(List<ListenerConfig> listenerConfigs) {
        this.listenerConfigs = listenerConfigs;
        return this;
    }

    public ReplicatedMapConfig addEntryListenerConfig(EntryListenerConfig listenerConfig) {
        this.getListenerConfigs().add(listenerConfig);
        return this;
    }

    public boolean isAsyncFillup() {
        return this.asyncFillup;
    }

    public void setAsyncFillup(boolean asyncFillup) {
        this.asyncFillup = asyncFillup;
    }

    public ReplicatedMapConfig getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new ReplicatedMapConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public boolean isStatisticsEnabled() {
        return this.statisticsEnabled;
    }

    public ReplicatedMapConfig setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
        return this;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public ReplicatedMapConfig setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    public String getMergePolicy() {
        return this.mergePolicyConfig.getPolicy();
    }

    public ReplicatedMapConfig setMergePolicy(String mergePolicy) {
        this.mergePolicyConfig.setPolicy(mergePolicy);
        return this;
    }

    public MergePolicyConfig getMergePolicyConfig() {
        return this.mergePolicyConfig;
    }

    public ReplicatedMapConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        this.mergePolicyConfig = Preconditions.checkNotNull(mergePolicyConfig, "mergePolicyConfig cannot be null!");
        return this;
    }

    public Class getProvidedMergeTypes() {
        return SplitBrainMergeTypes.ReplicatedMapMergeTypes.class;
    }

    public String toString() {
        return "ReplicatedMapConfig{name='" + this.name + '\'' + "', inMemoryFormat=" + (Object)((Object)this.inMemoryFormat) + '\'' + ", concurrencyLevel=" + this.concurrencyLevel + ", replicationDelayMillis=" + this.replicationDelayMillis + ", asyncFillup=" + this.asyncFillup + ", statisticsEnabled=" + this.statisticsEnabled + ", quorumName='" + this.quorumName + '\'' + ", mergePolicyConfig='" + this.mergePolicyConfig + '\'' + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 34;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeUTF(this.inMemoryFormat.name());
        out.writeBoolean(this.asyncFillup);
        out.writeBoolean(this.statisticsEnabled);
        SerializationUtil.writeNullableList(this.listenerConfigs, out);
        out.writeUTF(this.quorumName);
        out.writeObject(this.mergePolicyConfig);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.inMemoryFormat = InMemoryFormat.valueOf(in.readUTF());
        this.asyncFillup = in.readBoolean();
        this.statisticsEnabled = in.readBoolean();
        this.listenerConfigs = SerializationUtil.readNullableList(in);
        this.quorumName = in.readUTF();
        this.mergePolicyConfig = (MergePolicyConfig)in.readObject();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReplicatedMapConfig)) {
            return false;
        }
        ReplicatedMapConfig that = (ReplicatedMapConfig)o;
        if (this.asyncFillup != that.asyncFillup) {
            return false;
        }
        if (this.statisticsEnabled != that.statisticsEnabled) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        if (this.inMemoryFormat != that.inMemoryFormat) {
            return false;
        }
        if (this.quorumName != null ? !this.quorumName.equals(that.quorumName) : that.quorumName != null) {
            return false;
        }
        if (this.mergePolicyConfig != null ? !this.mergePolicyConfig.equals(that.mergePolicyConfig) : that.mergePolicyConfig != null) {
            return false;
        }
        return this.listenerConfigs != null ? this.listenerConfigs.equals(that.listenerConfigs) : that.listenerConfigs == null;
    }

    public final int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.inMemoryFormat != null ? this.inMemoryFormat.hashCode() : 0);
        result = 31 * result + (this.asyncFillup ? 1 : 0);
        result = 31 * result + (this.statisticsEnabled ? 1 : 0);
        result = 31 * result + (this.listenerConfigs != null ? this.listenerConfigs.hashCode() : 0);
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        result = 31 * result + (this.mergePolicyConfig != null ? this.mergePolicyConfig.hashCode() : 0);
        return result;
    }
}

