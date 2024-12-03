/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.CachePartitionLostListenerConfig;
import com.hazelcast.config.CacheSimpleConfigReadOnly;
import com.hazelcast.config.CacheSimpleEntryListenerConfig;
import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.HotRestartConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.merge.PutIfAbsentMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypeProvider;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheSimpleConfig
implements SplitBrainMergeTypeProvider,
IdentifiedDataSerializable,
NamedConfig {
    public static final int MIN_BACKUP_COUNT = 0;
    public static final int MAX_BACKUP_COUNT = 6;
    public static final int DEFAULT_BACKUP_COUNT = 1;
    public static final InMemoryFormat DEFAULT_IN_MEMORY_FORMAT = InMemoryFormat.BINARY;
    public static final String DEFAULT_CACHE_MERGE_POLICY = PutIfAbsentMergePolicy.class.getName();
    private String name;
    private String keyType;
    private String valueType;
    private boolean statisticsEnabled;
    private boolean managementEnabled;
    private boolean readThrough;
    private boolean writeThrough;
    private String cacheLoaderFactory;
    private String cacheWriterFactory;
    private String cacheLoader;
    private String cacheWriter;
    private ExpiryPolicyFactoryConfig expiryPolicyFactoryConfig;
    private List<CacheSimpleEntryListenerConfig> cacheEntryListeners;
    private int asyncBackupCount = 0;
    private int backupCount = 1;
    private InMemoryFormat inMemoryFormat = DEFAULT_IN_MEMORY_FORMAT;
    private EvictionConfig evictionConfig = new EvictionConfig();
    private WanReplicationRef wanReplicationRef;
    private transient CacheSimpleConfig readOnly;
    private String quorumName;
    private List<CachePartitionLostListenerConfig> partitionLostListenerConfigs;
    private String mergePolicy = DEFAULT_CACHE_MERGE_POLICY;
    private HotRestartConfig hotRestartConfig = new HotRestartConfig();
    private boolean disablePerEntryInvalidationEvents;

    public CacheSimpleConfig(CacheSimpleConfig cacheSimpleConfig) {
        this.name = cacheSimpleConfig.name;
        this.keyType = cacheSimpleConfig.keyType;
        this.valueType = cacheSimpleConfig.valueType;
        this.statisticsEnabled = cacheSimpleConfig.statisticsEnabled;
        this.managementEnabled = cacheSimpleConfig.managementEnabled;
        this.readThrough = cacheSimpleConfig.readThrough;
        this.writeThrough = cacheSimpleConfig.writeThrough;
        this.cacheLoaderFactory = cacheSimpleConfig.cacheLoaderFactory;
        this.cacheWriterFactory = cacheSimpleConfig.cacheWriterFactory;
        this.expiryPolicyFactoryConfig = cacheSimpleConfig.expiryPolicyFactoryConfig;
        this.cacheEntryListeners = cacheSimpleConfig.cacheEntryListeners == null ? null : new ArrayList<CacheSimpleEntryListenerConfig>(cacheSimpleConfig.cacheEntryListeners);
        this.asyncBackupCount = cacheSimpleConfig.asyncBackupCount;
        this.backupCount = cacheSimpleConfig.backupCount;
        this.inMemoryFormat = cacheSimpleConfig.inMemoryFormat;
        if (cacheSimpleConfig.evictionConfig != null) {
            this.evictionConfig = cacheSimpleConfig.evictionConfig;
        }
        this.wanReplicationRef = cacheSimpleConfig.wanReplicationRef;
        this.quorumName = cacheSimpleConfig.quorumName;
        this.mergePolicy = cacheSimpleConfig.mergePolicy;
        this.partitionLostListenerConfigs = cacheSimpleConfig.partitionLostListenerConfigs == null ? null : new ArrayList<CachePartitionLostListenerConfig>(cacheSimpleConfig.partitionLostListenerConfigs);
        this.hotRestartConfig = new HotRestartConfig(cacheSimpleConfig.hotRestartConfig);
        this.disablePerEntryInvalidationEvents = cacheSimpleConfig.disablePerEntryInvalidationEvents;
    }

    public CacheSimpleConfig() {
    }

    public CacheSimpleConfig getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new CacheSimpleConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public CacheSimpleConfig setName(String name) {
        this.name = name;
        return this;
    }

    public String getKeyType() {
        return this.keyType;
    }

    public CacheSimpleConfig setKeyType(String keyType) {
        this.keyType = keyType;
        return this;
    }

    public String getValueType() {
        return this.valueType;
    }

    public CacheSimpleConfig setValueType(String valueType) {
        this.valueType = valueType;
        return this;
    }

    public boolean isStatisticsEnabled() {
        return this.statisticsEnabled;
    }

    public CacheSimpleConfig setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
        return this;
    }

    public boolean isManagementEnabled() {
        return this.managementEnabled;
    }

    public CacheSimpleConfig setManagementEnabled(boolean managementEnabled) {
        this.managementEnabled = managementEnabled;
        return this;
    }

    public boolean isReadThrough() {
        return this.readThrough;
    }

    public CacheSimpleConfig setReadThrough(boolean readThrough) {
        this.readThrough = readThrough;
        return this;
    }

    public boolean isWriteThrough() {
        return this.writeThrough;
    }

    public CacheSimpleConfig setWriteThrough(boolean writeThrough) {
        this.writeThrough = writeThrough;
        return this;
    }

    public String getCacheLoaderFactory() {
        return this.cacheLoaderFactory;
    }

    public CacheSimpleConfig setCacheLoaderFactory(String cacheLoaderFactory) {
        if (this.cacheLoader != null && cacheLoaderFactory != null) {
            throw new IllegalStateException("Cannot set cacheLoaderFactory to '" + cacheLoaderFactory + "', because cacheLoader is already set to '" + this.cacheLoader + "'.");
        }
        this.cacheLoaderFactory = cacheLoaderFactory;
        return this;
    }

    public String getCacheLoader() {
        return this.cacheLoader;
    }

    public CacheSimpleConfig setCacheLoader(String cacheLoader) {
        if (cacheLoader != null && this.cacheLoaderFactory != null) {
            throw new IllegalStateException("Cannot set cacheLoader to '" + cacheLoader + "', because cacheLoaderFactory is already set to '" + this.cacheLoaderFactory + "'.");
        }
        this.cacheLoader = cacheLoader;
        return this;
    }

    public String getCacheWriterFactory() {
        return this.cacheWriterFactory;
    }

    public CacheSimpleConfig setCacheWriterFactory(String cacheWriterFactory) {
        if (this.cacheWriter != null && cacheWriterFactory != null) {
            throw new IllegalStateException("Cannot set cacheWriterFactory to '" + cacheWriterFactory + "', because cacheWriter is already set to '" + this.cacheWriter + "'.");
        }
        this.cacheWriterFactory = cacheWriterFactory;
        return this;
    }

    public String getCacheWriter() {
        return this.cacheWriter;
    }

    public CacheSimpleConfig setCacheWriter(String cacheWriter) {
        if (cacheWriter != null && this.cacheWriterFactory != null) {
            throw new IllegalStateException("Cannot set cacheWriter to '" + cacheWriter + "', because cacheWriterFactory is already set to '" + this.cacheWriterFactory + "'.");
        }
        this.cacheWriter = cacheWriter;
        return this;
    }

    public ExpiryPolicyFactoryConfig getExpiryPolicyFactoryConfig() {
        return this.expiryPolicyFactoryConfig;
    }

    public CacheSimpleConfig setExpiryPolicyFactoryConfig(ExpiryPolicyFactoryConfig expiryPolicyFactoryConfig) {
        this.expiryPolicyFactoryConfig = expiryPolicyFactoryConfig;
        return this;
    }

    public CacheSimpleConfig setExpiryPolicyFactory(String className) {
        this.expiryPolicyFactoryConfig = new ExpiryPolicyFactoryConfig(className);
        return this;
    }

    public CacheSimpleConfig addEntryListenerConfig(CacheSimpleEntryListenerConfig listenerConfig) {
        this.getCacheEntryListeners().add(listenerConfig);
        return this;
    }

    public List<CacheSimpleEntryListenerConfig> getCacheEntryListeners() {
        if (this.cacheEntryListeners == null) {
            this.cacheEntryListeners = new ArrayList<CacheSimpleEntryListenerConfig>();
        }
        return this.cacheEntryListeners;
    }

    public CacheSimpleConfig setCacheEntryListeners(List<CacheSimpleEntryListenerConfig> cacheEntryListeners) {
        this.cacheEntryListeners = cacheEntryListeners;
        return this;
    }

    public int getAsyncBackupCount() {
        return this.asyncBackupCount;
    }

    public CacheSimpleConfig setAsyncBackupCount(int asyncBackupCount) {
        this.asyncBackupCount = Preconditions.checkAsyncBackupCount(this.backupCount, asyncBackupCount);
        return this;
    }

    public int getBackupCount() {
        return this.backupCount;
    }

    public CacheSimpleConfig setBackupCount(int backupCount) {
        this.backupCount = Preconditions.checkBackupCount(backupCount, this.asyncBackupCount);
        return this;
    }

    public InMemoryFormat getInMemoryFormat() {
        return this.inMemoryFormat;
    }

    public CacheSimpleConfig setInMemoryFormat(InMemoryFormat inMemoryFormat) {
        this.inMemoryFormat = Preconditions.isNotNull(inMemoryFormat, "In-Memory format cannot be null!");
        return this;
    }

    public EvictionConfig getEvictionConfig() {
        return this.evictionConfig;
    }

    public CacheSimpleConfig setEvictionConfig(EvictionConfig evictionConfig) {
        this.evictionConfig = Preconditions.isNotNull(evictionConfig, "evictionConfig");
        return this;
    }

    public WanReplicationRef getWanReplicationRef() {
        return this.wanReplicationRef;
    }

    public void setWanReplicationRef(WanReplicationRef wanReplicationRef) {
        this.wanReplicationRef = wanReplicationRef;
    }

    public List<CachePartitionLostListenerConfig> getPartitionLostListenerConfigs() {
        if (this.partitionLostListenerConfigs == null) {
            this.partitionLostListenerConfigs = new ArrayList<CachePartitionLostListenerConfig>();
        }
        return this.partitionLostListenerConfigs;
    }

    public CacheSimpleConfig setPartitionLostListenerConfigs(List<CachePartitionLostListenerConfig> partitionLostListenerConfigs) {
        this.partitionLostListenerConfigs = partitionLostListenerConfigs;
        return this;
    }

    public CacheSimpleConfig addCachePartitionLostListenerConfig(CachePartitionLostListenerConfig listenerConfig) {
        this.getPartitionLostListenerConfigs().add(listenerConfig);
        return this;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public CacheSimpleConfig setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    public String getMergePolicy() {
        return this.mergePolicy;
    }

    public void setMergePolicy(String mergePolicy) {
        this.mergePolicy = mergePolicy;
    }

    public Class getProvidedMergeTypes() {
        return SplitBrainMergeTypes.CacheMergeTypes.class;
    }

    public HotRestartConfig getHotRestartConfig() {
        return this.hotRestartConfig;
    }

    public CacheSimpleConfig setHotRestartConfig(HotRestartConfig hotRestartConfig) {
        this.hotRestartConfig = hotRestartConfig;
        return this;
    }

    public boolean isDisablePerEntryInvalidationEvents() {
        return this.disablePerEntryInvalidationEvents;
    }

    public void setDisablePerEntryInvalidationEvents(boolean disablePerEntryInvalidationEvents) {
        this.disablePerEntryInvalidationEvents = disablePerEntryInvalidationEvents;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 38;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeUTF(this.keyType);
        out.writeUTF(this.valueType);
        out.writeBoolean(this.statisticsEnabled);
        out.writeBoolean(this.managementEnabled);
        out.writeBoolean(this.readThrough);
        out.writeBoolean(this.writeThrough);
        out.writeBoolean(this.disablePerEntryInvalidationEvents);
        out.writeUTF(this.cacheLoaderFactory);
        out.writeUTF(this.cacheWriterFactory);
        out.writeUTF(this.cacheLoader);
        out.writeUTF(this.cacheWriter);
        out.writeObject(this.expiryPolicyFactoryConfig);
        SerializationUtil.writeNullableList(this.cacheEntryListeners, out);
        out.writeInt(this.asyncBackupCount);
        out.writeInt(this.backupCount);
        out.writeUTF(this.inMemoryFormat.name());
        out.writeObject(this.evictionConfig);
        out.writeObject(this.wanReplicationRef);
        out.writeUTF(this.quorumName);
        SerializationUtil.writeNullableList(this.partitionLostListenerConfigs, out);
        out.writeUTF(this.mergePolicy);
        out.writeObject(this.hotRestartConfig);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.keyType = in.readUTF();
        this.valueType = in.readUTF();
        this.statisticsEnabled = in.readBoolean();
        this.managementEnabled = in.readBoolean();
        this.readThrough = in.readBoolean();
        this.writeThrough = in.readBoolean();
        this.disablePerEntryInvalidationEvents = in.readBoolean();
        this.cacheLoaderFactory = in.readUTF();
        this.cacheWriterFactory = in.readUTF();
        this.cacheLoader = in.readUTF();
        this.cacheWriter = in.readUTF();
        this.expiryPolicyFactoryConfig = (ExpiryPolicyFactoryConfig)in.readObject();
        this.cacheEntryListeners = SerializationUtil.readNullableList(in);
        this.asyncBackupCount = in.readInt();
        this.backupCount = in.readInt();
        this.inMemoryFormat = InMemoryFormat.valueOf(in.readUTF());
        this.evictionConfig = (EvictionConfig)in.readObject();
        this.wanReplicationRef = (WanReplicationRef)in.readObject();
        this.quorumName = in.readUTF();
        this.partitionLostListenerConfigs = SerializationUtil.readNullableList(in);
        this.mergePolicy = in.readUTF();
        this.hotRestartConfig = (HotRestartConfig)in.readObject();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CacheSimpleConfig)) {
            return false;
        }
        CacheSimpleConfig that = (CacheSimpleConfig)o;
        if (this.statisticsEnabled != that.statisticsEnabled) {
            return false;
        }
        if (this.managementEnabled != that.managementEnabled) {
            return false;
        }
        if (this.readThrough != that.readThrough) {
            return false;
        }
        if (this.writeThrough != that.writeThrough) {
            return false;
        }
        if (this.asyncBackupCount != that.asyncBackupCount) {
            return false;
        }
        if (this.backupCount != that.backupCount) {
            return false;
        }
        if (this.disablePerEntryInvalidationEvents != that.disablePerEntryInvalidationEvents) {
            return false;
        }
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (this.keyType != null ? !this.keyType.equals(that.keyType) : that.keyType != null) {
            return false;
        }
        if (this.valueType != null ? !this.valueType.equals(that.valueType) : that.valueType != null) {
            return false;
        }
        if (this.cacheLoaderFactory != null ? !this.cacheLoaderFactory.equals(that.cacheLoaderFactory) : that.cacheLoaderFactory != null) {
            return false;
        }
        if (this.cacheWriterFactory != null ? !this.cacheWriterFactory.equals(that.cacheWriterFactory) : that.cacheWriterFactory != null) {
            return false;
        }
        if (this.cacheLoader != null ? !this.cacheLoader.equals(that.cacheLoader) : that.cacheLoader != null) {
            return false;
        }
        if (this.cacheWriter != null ? !this.cacheWriter.equals(that.cacheWriter) : that.cacheWriter != null) {
            return false;
        }
        if (this.expiryPolicyFactoryConfig != null ? !this.expiryPolicyFactoryConfig.equals(that.expiryPolicyFactoryConfig) : that.expiryPolicyFactoryConfig != null) {
            return false;
        }
        if (this.cacheEntryListeners != null ? !this.cacheEntryListeners.equals(that.cacheEntryListeners) : that.cacheEntryListeners != null) {
            return false;
        }
        if (this.inMemoryFormat != that.inMemoryFormat) {
            return false;
        }
        if (this.evictionConfig != null ? !this.evictionConfig.equals(that.evictionConfig) : that.evictionConfig != null) {
            return false;
        }
        if (this.wanReplicationRef != null ? !this.wanReplicationRef.equals(that.wanReplicationRef) : that.wanReplicationRef != null) {
            return false;
        }
        if (this.quorumName != null ? !this.quorumName.equals(that.quorumName) : that.quorumName != null) {
            return false;
        }
        if (this.partitionLostListenerConfigs != null ? !this.partitionLostListenerConfigs.equals(that.partitionLostListenerConfigs) : that.partitionLostListenerConfigs != null) {
            return false;
        }
        if (this.mergePolicy != null ? !this.mergePolicy.equals(that.mergePolicy) : that.mergePolicy != null) {
            return false;
        }
        return this.hotRestartConfig != null ? this.hotRestartConfig.equals(that.hotRestartConfig) : that.hotRestartConfig == null;
    }

    public final int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + (this.keyType != null ? this.keyType.hashCode() : 0);
        result = 31 * result + (this.valueType != null ? this.valueType.hashCode() : 0);
        result = 31 * result + (this.statisticsEnabled ? 1 : 0);
        result = 31 * result + (this.managementEnabled ? 1 : 0);
        result = 31 * result + (this.readThrough ? 1 : 0);
        result = 31 * result + (this.writeThrough ? 1 : 0);
        result = 31 * result + (this.cacheLoaderFactory != null ? this.cacheLoaderFactory.hashCode() : 0);
        result = 31 * result + (this.cacheWriterFactory != null ? this.cacheWriterFactory.hashCode() : 0);
        result = 31 * result + (this.cacheLoader != null ? this.cacheLoader.hashCode() : 0);
        result = 31 * result + (this.cacheWriter != null ? this.cacheWriter.hashCode() : 0);
        result = 31 * result + (this.expiryPolicyFactoryConfig != null ? this.expiryPolicyFactoryConfig.hashCode() : 0);
        result = 31 * result + (this.cacheEntryListeners != null ? this.cacheEntryListeners.hashCode() : 0);
        result = 31 * result + this.asyncBackupCount;
        result = 31 * result + this.backupCount;
        result = 31 * result + (this.inMemoryFormat != null ? this.inMemoryFormat.hashCode() : 0);
        result = 31 * result + (this.evictionConfig != null ? this.evictionConfig.hashCode() : 0);
        result = 31 * result + (this.wanReplicationRef != null ? this.wanReplicationRef.hashCode() : 0);
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        result = 31 * result + (this.partitionLostListenerConfigs != null ? this.partitionLostListenerConfigs.hashCode() : 0);
        result = 31 * result + (this.mergePolicy != null ? this.mergePolicy.hashCode() : 0);
        result = 31 * result + (this.hotRestartConfig != null ? this.hotRestartConfig.hashCode() : 0);
        result = 31 * result + (this.disablePerEntryInvalidationEvents ? 1 : 0);
        return result;
    }

    public String toString() {
        return "CacheSimpleConfig{name='" + this.name + '\'' + ", asyncBackupCount=" + this.asyncBackupCount + ", backupCount=" + this.backupCount + ", inMemoryFormat=" + (Object)((Object)this.inMemoryFormat) + ", keyType=" + this.keyType + ", valueType=" + this.valueType + ", statisticsEnabled=" + this.statisticsEnabled + ", managementEnabled=" + this.managementEnabled + ", readThrough=" + this.readThrough + ", writeThrough=" + this.writeThrough + ", cacheLoaderFactory='" + this.cacheLoaderFactory + '\'' + ", cacheWriterFactory='" + this.cacheWriterFactory + '\'' + ", cacheLoader='" + this.cacheLoader + '\'' + ", cacheWriter='" + this.cacheWriter + '\'' + ", expiryPolicyFactoryConfig=" + this.expiryPolicyFactoryConfig + ", cacheEntryListeners=" + this.cacheEntryListeners + ", evictionConfig=" + this.evictionConfig + ", wanReplicationRef=" + this.wanReplicationRef + ", quorumName=" + this.quorumName + ", partitionLostListenerConfigs=" + this.partitionLostListenerConfigs + ", mergePolicy=" + this.mergePolicy + ", hotRestartConfig=" + this.hotRestartConfig + '}';
    }

    public static class ExpiryPolicyFactoryConfig
    implements IdentifiedDataSerializable {
        private String className;
        private TimedExpiryPolicyFactoryConfig timedExpiryPolicyFactoryConfig;

        public ExpiryPolicyFactoryConfig() {
        }

        public ExpiryPolicyFactoryConfig(String className) {
            this.className = className;
            this.timedExpiryPolicyFactoryConfig = null;
        }

        public ExpiryPolicyFactoryConfig(TimedExpiryPolicyFactoryConfig timedExpiryPolicyFactoryConfig) {
            this.className = null;
            this.timedExpiryPolicyFactoryConfig = timedExpiryPolicyFactoryConfig;
        }

        public String getClassName() {
            return this.className;
        }

        public TimedExpiryPolicyFactoryConfig getTimedExpiryPolicyFactoryConfig() {
            return this.timedExpiryPolicyFactoryConfig;
        }

        @Override
        public int getFactoryId() {
            return ConfigDataSerializerHook.F_ID;
        }

        @Override
        public int getId() {
            return 39;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeUTF(this.className);
            out.writeObject(this.timedExpiryPolicyFactoryConfig);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            this.className = in.readUTF();
            this.timedExpiryPolicyFactoryConfig = (TimedExpiryPolicyFactoryConfig)in.readObject();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ExpiryPolicyFactoryConfig that = (ExpiryPolicyFactoryConfig)o;
            if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
                return false;
            }
            return this.timedExpiryPolicyFactoryConfig != null ? this.timedExpiryPolicyFactoryConfig.equals(that.timedExpiryPolicyFactoryConfig) : that.timedExpiryPolicyFactoryConfig == null;
        }

        public int hashCode() {
            int result = this.className != null ? this.className.hashCode() : 0;
            result = 31 * result + (this.timedExpiryPolicyFactoryConfig != null ? this.timedExpiryPolicyFactoryConfig.hashCode() : 0);
            return result;
        }

        public String toString() {
            return "ExpiryPolicyFactoryConfig{className='" + this.className + '\'' + ", timedExpiryPolicyFactoryConfig=" + this.timedExpiryPolicyFactoryConfig + '}';
        }

        public static class DurationConfig
        implements IdentifiedDataSerializable {
            private long durationAmount;
            private TimeUnit timeUnit;

            public DurationConfig() {
            }

            public DurationConfig(long durationAmount, TimeUnit timeUnit) {
                this.durationAmount = durationAmount;
                this.timeUnit = timeUnit;
            }

            public long getDurationAmount() {
                return this.durationAmount;
            }

            public TimeUnit getTimeUnit() {
                return this.timeUnit;
            }

            @Override
            public int getFactoryId() {
                return ConfigDataSerializerHook.F_ID;
            }

            @Override
            public int getId() {
                return 41;
            }

            @Override
            public void writeData(ObjectDataOutput out) throws IOException {
                out.writeLong(this.durationAmount);
                out.writeUTF(this.timeUnit.name());
            }

            @Override
            public void readData(ObjectDataInput in) throws IOException {
                this.durationAmount = in.readLong();
                this.timeUnit = TimeUnit.valueOf(in.readUTF());
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass()) {
                    return false;
                }
                DurationConfig that = (DurationConfig)o;
                if (this.durationAmount != that.durationAmount) {
                    return false;
                }
                return this.timeUnit == that.timeUnit;
            }

            public int hashCode() {
                int result = (int)(this.durationAmount ^ this.durationAmount >>> 32);
                result = 31 * result + (this.timeUnit != null ? this.timeUnit.hashCode() : 0);
                return result;
            }

            public String toString() {
                return "DurationConfig{durationAmount=" + this.durationAmount + ", timeUnit" + (Object)((Object)this.timeUnit) + '}';
            }
        }

        public static class TimedExpiryPolicyFactoryConfig
        implements IdentifiedDataSerializable {
            private ExpiryPolicyType expiryPolicyType;
            private DurationConfig durationConfig;

            public TimedExpiryPolicyFactoryConfig() {
            }

            public TimedExpiryPolicyFactoryConfig(ExpiryPolicyType expiryPolicyType, DurationConfig durationConfig) {
                this.expiryPolicyType = expiryPolicyType;
                this.durationConfig = durationConfig;
            }

            public ExpiryPolicyType getExpiryPolicyType() {
                return this.expiryPolicyType;
            }

            public DurationConfig getDurationConfig() {
                return this.durationConfig;
            }

            @Override
            public int getFactoryId() {
                return ConfigDataSerializerHook.F_ID;
            }

            @Override
            public int getId() {
                return 40;
            }

            @Override
            public void writeData(ObjectDataOutput out) throws IOException {
                out.writeUTF(this.expiryPolicyType.name());
                out.writeObject(this.durationConfig);
            }

            @Override
            public void readData(ObjectDataInput in) throws IOException {
                this.expiryPolicyType = ExpiryPolicyType.valueOf(in.readUTF());
                this.durationConfig = (DurationConfig)in.readObject();
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass()) {
                    return false;
                }
                TimedExpiryPolicyFactoryConfig that = (TimedExpiryPolicyFactoryConfig)o;
                if (this.expiryPolicyType != that.expiryPolicyType) {
                    return false;
                }
                return this.durationConfig != null ? this.durationConfig.equals(that.durationConfig) : that.durationConfig == null;
            }

            public int hashCode() {
                int result = this.expiryPolicyType != null ? this.expiryPolicyType.hashCode() : 0;
                result = 31 * result + (this.durationConfig != null ? this.durationConfig.hashCode() : 0);
                return result;
            }

            public String toString() {
                return "TimedExpiryPolicyFactoryConfig{expiryPolicyType=" + (Object)((Object)this.expiryPolicyType) + ", durationConfig=" + this.durationConfig + '}';
            }

            public static enum ExpiryPolicyType {
                CREATED,
                MODIFIED,
                ACCESSED,
                TOUCHED,
                ETERNAL;

            }
        }
    }
}

