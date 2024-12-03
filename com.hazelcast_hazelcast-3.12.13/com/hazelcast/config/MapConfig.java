/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.CacheDeserializedValues;
import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.HotRestartConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.config.MapConfigReadOnly;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.MapPartitionLostListenerConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.MetadataPolicy;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.PartitioningStrategyConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.map.eviction.MapEvictionPolicy;
import com.hazelcast.map.merge.PutIfAbsentMapMergePolicy;
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

public class MapConfig
implements SplitBrainMergeTypeProvider,
IdentifiedDataSerializable,
Versioned,
NamedConfig {
    public static final int MIN_BACKUP_COUNT = 0;
    public static final int DEFAULT_BACKUP_COUNT = 1;
    public static final int MAX_BACKUP_COUNT = 6;
    public static final int MIN_EVICTION_PERCENTAGE = 0;
    public static final int DEFAULT_EVICTION_PERCENTAGE = 25;
    public static final int MAX_EVICTION_PERCENTAGE = 100;
    public static final long DEFAULT_MIN_EVICTION_CHECK_MILLIS = 100L;
    public static final int DEFAULT_TTL_SECONDS = 0;
    public static final int DEFAULT_MAX_IDLE_SECONDS = 0;
    public static final EvictionPolicy DEFAULT_EVICTION_POLICY = EvictionPolicy.NONE;
    public static final String DEFAULT_MAP_MERGE_POLICY = PutIfAbsentMapMergePolicy.class.getName();
    public static final InMemoryFormat DEFAULT_IN_MEMORY_FORMAT = InMemoryFormat.BINARY;
    public static final CacheDeserializedValues DEFAULT_CACHED_DESERIALIZED_VALUES = CacheDeserializedValues.INDEX_ONLY;
    public static final MetadataPolicy DEFAULT_METADATA_POLICY = MetadataPolicy.CREATE_ON_UPDATE;
    private String name;
    private int backupCount = 1;
    private int asyncBackupCount = 0;
    private transient int evictionPercentage = 25;
    private transient long minEvictionCheckMillis = 100L;
    private int timeToLiveSeconds = 0;
    private int maxIdleSeconds = 0;
    private MaxSizeConfig maxSizeConfig = new MaxSizeConfig();
    private EvictionPolicy evictionPolicy = DEFAULT_EVICTION_POLICY;
    private MapEvictionPolicy mapEvictionPolicy;
    private MapStoreConfig mapStoreConfig = new MapStoreConfig().setEnabled(false);
    private NearCacheConfig nearCacheConfig;
    private boolean readBackupData;
    private CacheDeserializedValues cacheDeserializedValues = DEFAULT_CACHED_DESERIALIZED_VALUES;
    private MergePolicyConfig mergePolicyConfig = new MergePolicyConfig();
    private InMemoryFormat inMemoryFormat = DEFAULT_IN_MEMORY_FORMAT;
    private WanReplicationRef wanReplicationRef;
    private List<EntryListenerConfig> entryListenerConfigs;
    private List<MapPartitionLostListenerConfig> partitionLostListenerConfigs;
    private List<MapIndexConfig> mapIndexConfigs;
    private List<MapAttributeConfig> mapAttributeConfigs;
    private List<QueryCacheConfig> queryCacheConfigs;
    private boolean statisticsEnabled = true;
    private PartitioningStrategyConfig partitioningStrategyConfig;
    private String quorumName;
    private MetadataPolicy metadataPolicy = DEFAULT_METADATA_POLICY;
    private HotRestartConfig hotRestartConfig = new HotRestartConfig();
    private transient MapConfigReadOnly readOnly;
    private transient boolean optimizeQueryExplicitlyInvoked;
    private transient boolean setCacheDeserializedValuesExplicitlyInvoked;

    public MapConfig() {
    }

    public MapConfig(String name) {
        this.name = name;
    }

    public MapConfig(MapConfig config) {
        this.name = config.name;
        this.backupCount = config.backupCount;
        this.asyncBackupCount = config.asyncBackupCount;
        this.evictionPercentage = config.evictionPercentage;
        this.minEvictionCheckMillis = config.minEvictionCheckMillis;
        this.timeToLiveSeconds = config.timeToLiveSeconds;
        this.maxIdleSeconds = config.maxIdleSeconds;
        this.metadataPolicy = config.metadataPolicy;
        this.maxSizeConfig = config.maxSizeConfig != null ? new MaxSizeConfig(config.maxSizeConfig) : null;
        this.evictionPolicy = config.evictionPolicy;
        this.mapEvictionPolicy = config.mapEvictionPolicy;
        this.inMemoryFormat = config.inMemoryFormat;
        this.mapStoreConfig = config.mapStoreConfig != null ? new MapStoreConfig(config.mapStoreConfig) : null;
        this.nearCacheConfig = config.nearCacheConfig != null ? new NearCacheConfig(config.nearCacheConfig) : null;
        this.readBackupData = config.readBackupData;
        this.cacheDeserializedValues = config.cacheDeserializedValues;
        this.statisticsEnabled = config.statisticsEnabled;
        this.mergePolicyConfig = config.mergePolicyConfig;
        this.wanReplicationRef = config.wanReplicationRef != null ? new WanReplicationRef(config.wanReplicationRef) : null;
        this.entryListenerConfigs = new ArrayList<EntryListenerConfig>(config.getEntryListenerConfigs());
        this.partitionLostListenerConfigs = new ArrayList<MapPartitionLostListenerConfig>(config.getPartitionLostListenerConfigs());
        this.mapIndexConfigs = new ArrayList<MapIndexConfig>(config.getMapIndexConfigs());
        this.mapAttributeConfigs = new ArrayList<MapAttributeConfig>(config.getMapAttributeConfigs());
        this.queryCacheConfigs = new ArrayList<QueryCacheConfig>(config.getQueryCacheConfigs());
        this.partitioningStrategyConfig = config.partitioningStrategyConfig != null ? new PartitioningStrategyConfig(config.getPartitioningStrategyConfig()) : null;
        this.quorumName = config.quorumName;
        this.hotRestartConfig = new HotRestartConfig(config.hotRestartConfig);
    }

    public MapConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new MapConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public MapConfig setName(String name) {
        this.name = name;
        return this;
    }

    public InMemoryFormat getInMemoryFormat() {
        return this.inMemoryFormat;
    }

    public MapConfig setInMemoryFormat(InMemoryFormat inMemoryFormat) {
        this.inMemoryFormat = Preconditions.isNotNull(inMemoryFormat, "inMemoryFormat");
        return this;
    }

    public int getBackupCount() {
        return this.backupCount;
    }

    public MapConfig setBackupCount(int backupCount) {
        this.backupCount = Preconditions.checkBackupCount(backupCount, this.asyncBackupCount);
        return this;
    }

    public int getAsyncBackupCount() {
        return this.asyncBackupCount;
    }

    public MapConfig setAsyncBackupCount(int asyncBackupCount) {
        this.asyncBackupCount = Preconditions.checkAsyncBackupCount(this.backupCount, asyncBackupCount);
        return this;
    }

    public int getTotalBackupCount() {
        return this.backupCount + this.asyncBackupCount;
    }

    @Deprecated
    public int getEvictionPercentage() {
        return this.evictionPercentage;
    }

    public MapConfig setEvictionPercentage(int evictionPercentage) {
        if (evictionPercentage < 0) {
            throw new IllegalArgumentException("Eviction percentage must be greater than or equal to 0");
        }
        if (evictionPercentage > 100) {
            throw new IllegalArgumentException("Eviction percentage must be smaller than or equal to 100");
        }
        this.evictionPercentage = evictionPercentage;
        return this;
    }

    public long getMinEvictionCheckMillis() {
        return this.minEvictionCheckMillis;
    }

    public MapConfig setMinEvictionCheckMillis(long minEvictionCheckMillis) {
        if (minEvictionCheckMillis < 0L) {
            throw new IllegalArgumentException("Parameter minEvictionCheckMillis can not get a negative value");
        }
        this.minEvictionCheckMillis = minEvictionCheckMillis;
        return this;
    }

    public int getTimeToLiveSeconds() {
        return this.timeToLiveSeconds;
    }

    public MapConfig setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
        return this;
    }

    public int getMaxIdleSeconds() {
        return this.maxIdleSeconds;
    }

    public MapConfig setMaxIdleSeconds(int maxIdleSeconds) {
        this.maxIdleSeconds = maxIdleSeconds;
        return this;
    }

    public MaxSizeConfig getMaxSizeConfig() {
        return this.maxSizeConfig;
    }

    public MapConfig setMaxSizeConfig(MaxSizeConfig maxSizeConfig) {
        this.maxSizeConfig = maxSizeConfig;
        return this;
    }

    public EvictionPolicy getEvictionPolicy() {
        return this.evictionPolicy;
    }

    public MapConfig setEvictionPolicy(EvictionPolicy evictionPolicy) {
        this.evictionPolicy = Preconditions.checkNotNull(evictionPolicy, "evictionPolicy cannot be null");
        return this;
    }

    public MapEvictionPolicy getMapEvictionPolicy() {
        return this.mapEvictionPolicy;
    }

    public MapConfig setMapEvictionPolicy(MapEvictionPolicy mapEvictionPolicy) {
        this.mapEvictionPolicy = Preconditions.checkNotNull(mapEvictionPolicy, "mapEvictionPolicy cannot be null");
        return this;
    }

    public MapStoreConfig getMapStoreConfig() {
        return this.mapStoreConfig;
    }

    public MapConfig setMapStoreConfig(MapStoreConfig mapStoreConfig) {
        this.mapStoreConfig = mapStoreConfig;
        return this;
    }

    public NearCacheConfig getNearCacheConfig() {
        return this.nearCacheConfig;
    }

    public MapConfig setNearCacheConfig(NearCacheConfig nearCacheConfig) {
        this.nearCacheConfig = nearCacheConfig;
        return this;
    }

    public String getMergePolicy() {
        return this.mergePolicyConfig.getPolicy();
    }

    public MapConfig setMergePolicy(String mergePolicy) {
        this.mergePolicyConfig.setPolicy(mergePolicy);
        return this;
    }

    public MergePolicyConfig getMergePolicyConfig() {
        return this.mergePolicyConfig;
    }

    public MapConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        this.mergePolicyConfig = Preconditions.checkNotNull(mergePolicyConfig, "mergePolicyConfig cannot be null!");
        return this;
    }

    public Class getProvidedMergeTypes() {
        return SplitBrainMergeTypes.MapMergeTypes.class;
    }

    public boolean isStatisticsEnabled() {
        return this.statisticsEnabled;
    }

    public MapConfig setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
        return this;
    }

    public boolean isReadBackupData() {
        return this.readBackupData;
    }

    public MapConfig setReadBackupData(boolean readBackupData) {
        this.readBackupData = readBackupData;
        return this;
    }

    public WanReplicationRef getWanReplicationRef() {
        return this.wanReplicationRef;
    }

    public MapConfig setWanReplicationRef(WanReplicationRef wanReplicationRef) {
        this.wanReplicationRef = wanReplicationRef;
        return this;
    }

    public MapConfig addEntryListenerConfig(EntryListenerConfig listenerConfig) {
        this.getEntryListenerConfigs().add(listenerConfig);
        return this;
    }

    public List<EntryListenerConfig> getEntryListenerConfigs() {
        if (this.entryListenerConfigs == null) {
            this.entryListenerConfigs = new ArrayList<EntryListenerConfig>();
        }
        return this.entryListenerConfigs;
    }

    public MapConfig setEntryListenerConfigs(List<EntryListenerConfig> listenerConfigs) {
        this.entryListenerConfigs = listenerConfigs;
        return this;
    }

    public MapConfig addMapPartitionLostListenerConfig(MapPartitionLostListenerConfig listenerConfig) {
        this.getPartitionLostListenerConfigs().add(listenerConfig);
        return this;
    }

    public List<MapPartitionLostListenerConfig> getPartitionLostListenerConfigs() {
        if (this.partitionLostListenerConfigs == null) {
            this.partitionLostListenerConfigs = new ArrayList<MapPartitionLostListenerConfig>();
        }
        return this.partitionLostListenerConfigs;
    }

    public MapConfig setPartitionLostListenerConfigs(List<MapPartitionLostListenerConfig> listenerConfigs) {
        this.partitionLostListenerConfigs = listenerConfigs;
        return this;
    }

    public MapConfig addMapIndexConfig(MapIndexConfig mapIndexConfig) {
        this.getMapIndexConfigs().add(mapIndexConfig);
        return this;
    }

    public List<MapIndexConfig> getMapIndexConfigs() {
        if (this.mapIndexConfigs == null) {
            this.mapIndexConfigs = new ArrayList<MapIndexConfig>();
        }
        return this.mapIndexConfigs;
    }

    public MapConfig setMapIndexConfigs(List<MapIndexConfig> mapIndexConfigs) {
        this.mapIndexConfigs = mapIndexConfigs;
        return this;
    }

    public MapConfig addMapAttributeConfig(MapAttributeConfig mapAttributeConfig) {
        this.getMapAttributeConfigs().add(mapAttributeConfig);
        return this;
    }

    public List<MapAttributeConfig> getMapAttributeConfigs() {
        if (this.mapAttributeConfigs == null) {
            this.mapAttributeConfigs = new ArrayList<MapAttributeConfig>();
        }
        return this.mapAttributeConfigs;
    }

    public MapConfig setMapAttributeConfigs(List<MapAttributeConfig> mapAttributeConfigs) {
        this.mapAttributeConfigs = mapAttributeConfigs;
        return this;
    }

    public MetadataPolicy getMetadataPolicy() {
        return this.metadataPolicy;
    }

    public MapConfig setMetadataPolicy(MetadataPolicy metadataPolicy) {
        this.metadataPolicy = metadataPolicy;
        return this;
    }

    public MapConfig addQueryCacheConfig(QueryCacheConfig queryCacheConfig) {
        String queryCacheName = queryCacheConfig.getName();
        List<QueryCacheConfig> queryCacheConfigs = this.getQueryCacheConfigs();
        for (QueryCacheConfig cacheConfig : queryCacheConfigs) {
            Preconditions.checkFalse(cacheConfig.getName().equals(queryCacheName), "A query cache already exists with name = [" + queryCacheName + ']');
        }
        queryCacheConfigs.add(queryCacheConfig);
        return this;
    }

    public List<QueryCacheConfig> getQueryCacheConfigs() {
        if (this.queryCacheConfigs == null) {
            this.queryCacheConfigs = new ArrayList<QueryCacheConfig>();
        }
        return this.queryCacheConfigs;
    }

    public void setQueryCacheConfigs(List<QueryCacheConfig> queryCacheConfigs) {
        this.queryCacheConfigs = queryCacheConfigs;
    }

    public PartitioningStrategyConfig getPartitioningStrategyConfig() {
        return this.partitioningStrategyConfig;
    }

    public MapConfig setPartitioningStrategyConfig(PartitioningStrategyConfig partitioningStrategyConfig) {
        this.partitioningStrategyConfig = partitioningStrategyConfig;
        return this;
    }

    public boolean isNearCacheEnabled() {
        return this.nearCacheConfig != null;
    }

    public boolean isOptimizeQueries() {
        return this.cacheDeserializedValues == CacheDeserializedValues.ALWAYS;
    }

    public MapConfig setOptimizeQueries(boolean optimizeQueries) {
        this.validateSetOptimizeQueriesOption(optimizeQueries);
        if (optimizeQueries) {
            this.cacheDeserializedValues = CacheDeserializedValues.ALWAYS;
        }
        this.optimizeQueryExplicitlyInvoked = true;
        return this;
    }

    private void validateSetOptimizeQueriesOption(boolean optimizeQueries) {
        if (!this.setCacheDeserializedValuesExplicitlyInvoked) {
            return;
        }
        if (this.cacheDeserializedValues != CacheDeserializedValues.ALWAYS && optimizeQueries) {
            throw new ConfigurationException("Deprecated option 'optimize-queries' is being set to true, but 'cacheDeserializedValues' was set to NEVER or INDEX_ONLY. These are conflicting options. Please remove the `optimize-queries'");
        }
        if (this.cacheDeserializedValues == CacheDeserializedValues.ALWAYS && !optimizeQueries) {
            throw new ConfigurationException("Deprecated option 'optimize-queries' is being set to false, but 'cacheDeserializedValues' was set to ALWAYS. These are conflicting options. Please remove the `optimize-queries'");
        }
    }

    public MapConfig setCacheDeserializedValues(CacheDeserializedValues cacheDeserializedValues) {
        this.validateCacheDeserializedValuesOption(cacheDeserializedValues);
        this.cacheDeserializedValues = cacheDeserializedValues;
        this.setCacheDeserializedValuesExplicitlyInvoked = true;
        return this;
    }

    private void validateCacheDeserializedValuesOption(CacheDeserializedValues newValue) {
        boolean optimizeQueryFlag;
        if (!this.optimizeQueryExplicitlyInvoked) {
            return;
        }
        boolean bl = optimizeQueryFlag = this.cacheDeserializedValues == CacheDeserializedValues.ALWAYS;
        if (optimizeQueryFlag && newValue != CacheDeserializedValues.ALWAYS) {
            throw new ConfigurationException("Deprecated option 'optimize-queries' is set to `true`, but 'cacheDeserializedValues' is being set to NEVER or INDEX_ONLY. These are conflicting options. Please remove the `optimize-queries'");
        }
        if (!optimizeQueryFlag && newValue == CacheDeserializedValues.ALWAYS) {
            throw new ConfigurationException("Deprecated option 'optimize-queries' is set to `false`, but 'cacheDeserializedValues' is being set to ALWAYS. These are conflicting options. Please remove the `optimize-queries'");
        }
    }

    public HotRestartConfig getHotRestartConfig() {
        return this.hotRestartConfig;
    }

    public MapConfig setHotRestartConfig(HotRestartConfig hotRestartConfig) {
        this.hotRestartConfig = hotRestartConfig;
        return this;
    }

    public CacheDeserializedValues getCacheDeserializedValues() {
        return this.cacheDeserializedValues;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public MapConfig setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MapConfig)) {
            return false;
        }
        MapConfig that = (MapConfig)o;
        if (this.backupCount != that.backupCount) {
            return false;
        }
        if (this.asyncBackupCount != that.asyncBackupCount) {
            return false;
        }
        if (this.timeToLiveSeconds != that.timeToLiveSeconds) {
            return false;
        }
        if (this.maxIdleSeconds != that.maxIdleSeconds) {
            return false;
        }
        if (this.readBackupData != that.readBackupData) {
            return false;
        }
        if (this.statisticsEnabled != that.statisticsEnabled) {
            return false;
        }
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (this.maxSizeConfig != null ? !this.maxSizeConfig.equals(that.maxSizeConfig) : that.maxSizeConfig != null) {
            return false;
        }
        if (this.evictionPolicy != that.evictionPolicy) {
            return false;
        }
        if (this.mapEvictionPolicy != null ? !this.mapEvictionPolicy.equals(that.mapEvictionPolicy) : that.mapEvictionPolicy != null) {
            return false;
        }
        if (this.mapStoreConfig != null ? !this.mapStoreConfig.equals(that.mapStoreConfig) : that.mapStoreConfig != null) {
            return false;
        }
        if (this.nearCacheConfig != null ? !this.nearCacheConfig.equals(that.nearCacheConfig) : that.nearCacheConfig != null) {
            return false;
        }
        if (this.cacheDeserializedValues != that.cacheDeserializedValues) {
            return false;
        }
        if (this.mergePolicyConfig != null ? !this.mergePolicyConfig.equals(that.mergePolicyConfig) : that.mergePolicyConfig != null) {
            return false;
        }
        if (this.inMemoryFormat != that.inMemoryFormat) {
            return false;
        }
        if (this.metadataPolicy != that.metadataPolicy) {
            return false;
        }
        if (this.wanReplicationRef != null ? !this.wanReplicationRef.equals(that.wanReplicationRef) : that.wanReplicationRef != null) {
            return false;
        }
        if (!this.getEntryListenerConfigs().equals(that.getEntryListenerConfigs())) {
            return false;
        }
        if (!this.getPartitionLostListenerConfigs().equals(that.getPartitionLostListenerConfigs())) {
            return false;
        }
        if (!this.getMapIndexConfigs().equals(that.getMapIndexConfigs())) {
            return false;
        }
        if (!this.getMapAttributeConfigs().equals(that.getMapAttributeConfigs())) {
            return false;
        }
        if (!this.getQueryCacheConfigs().equals(that.getQueryCacheConfigs())) {
            return false;
        }
        if (this.partitioningStrategyConfig != null ? !this.partitioningStrategyConfig.equals(that.partitioningStrategyConfig) : that.partitioningStrategyConfig != null) {
            return false;
        }
        if (this.quorumName != null ? !this.quorumName.equals(that.quorumName) : that.quorumName != null) {
            return false;
        }
        return this.hotRestartConfig != null ? this.hotRestartConfig.equals(that.hotRestartConfig) : that.hotRestartConfig == null;
    }

    public final int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + this.backupCount;
        result = 31 * result + this.asyncBackupCount;
        result = 31 * result + this.timeToLiveSeconds;
        result = 31 * result + this.maxIdleSeconds;
        result = 31 * result + (this.maxSizeConfig != null ? this.maxSizeConfig.hashCode() : 0);
        result = 31 * result + (this.evictionPolicy != null ? this.evictionPolicy.hashCode() : 0);
        result = 31 * result + (this.mapEvictionPolicy != null ? this.mapEvictionPolicy.hashCode() : 0);
        result = 31 * result + (this.mapStoreConfig != null ? this.mapStoreConfig.hashCode() : 0);
        result = 31 * result + (this.nearCacheConfig != null ? this.nearCacheConfig.hashCode() : 0);
        result = 31 * result + (this.readBackupData ? 1 : 0);
        result = 31 * result + this.cacheDeserializedValues.hashCode();
        result = 31 * result + (this.mergePolicyConfig != null ? this.mergePolicyConfig.hashCode() : 0);
        result = 31 * result + this.inMemoryFormat.hashCode();
        result = 31 * result + this.metadataPolicy.hashCode();
        result = 31 * result + (this.wanReplicationRef != null ? this.wanReplicationRef.hashCode() : 0);
        result = 31 * result + this.getEntryListenerConfigs().hashCode();
        result = 31 * result + this.getMapIndexConfigs().hashCode();
        result = 31 * result + this.getMapAttributeConfigs().hashCode();
        result = 31 * result + this.getQueryCacheConfigs().hashCode();
        result = 31 * result + this.getPartitionLostListenerConfigs().hashCode();
        result = 31 * result + (this.statisticsEnabled ? 1 : 0);
        result = 31 * result + (this.partitioningStrategyConfig != null ? this.partitioningStrategyConfig.hashCode() : 0);
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        result = 31 * result + (this.hotRestartConfig != null ? this.hotRestartConfig.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "MapConfig{name='" + this.name + '\'' + ", inMemoryFormat=" + (Object)((Object)this.inMemoryFormat) + '\'' + ", metadataPolicy=" + (Object)((Object)this.metadataPolicy) + ", backupCount=" + this.backupCount + ", asyncBackupCount=" + this.asyncBackupCount + ", timeToLiveSeconds=" + this.timeToLiveSeconds + ", maxIdleSeconds=" + this.maxIdleSeconds + ", evictionPolicy='" + (Object)((Object)this.evictionPolicy) + '\'' + ", mapEvictionPolicy='" + this.mapEvictionPolicy + '\'' + ", evictionPercentage=" + this.evictionPercentage + ", minEvictionCheckMillis=" + this.minEvictionCheckMillis + ", maxSizeConfig=" + this.maxSizeConfig + ", readBackupData=" + this.readBackupData + ", hotRestart=" + this.hotRestartConfig + ", nearCacheConfig=" + this.nearCacheConfig + ", mapStoreConfig=" + this.mapStoreConfig + ", mergePolicyConfig=" + this.mergePolicyConfig + ", wanReplicationRef=" + this.wanReplicationRef + ", entryListenerConfigs=" + this.entryListenerConfigs + ", mapIndexConfigs=" + this.mapIndexConfigs + ", mapAttributeConfigs=" + this.mapAttributeConfigs + ", quorumName=" + this.quorumName + ", queryCacheConfigs=" + this.queryCacheConfigs + ", cacheDeserializedValues=" + (Object)((Object)this.cacheDeserializedValues) + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 10;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.backupCount);
        out.writeInt(this.asyncBackupCount);
        out.writeInt(this.timeToLiveSeconds);
        out.writeInt(this.maxIdleSeconds);
        out.writeObject(this.maxSizeConfig);
        out.writeUTF(this.evictionPolicy.name());
        out.writeObject(this.mapEvictionPolicy);
        out.writeObject(this.mapStoreConfig);
        out.writeObject(this.nearCacheConfig);
        out.writeBoolean(this.readBackupData);
        out.writeUTF(this.cacheDeserializedValues.name());
        out.writeObject(this.mergePolicyConfig);
        out.writeUTF(this.inMemoryFormat.name());
        out.writeObject(this.wanReplicationRef);
        SerializationUtil.writeNullableList(this.entryListenerConfigs, out);
        SerializationUtil.writeNullableList(this.partitionLostListenerConfigs, out);
        SerializationUtil.writeNullableList(this.mapIndexConfigs, out);
        SerializationUtil.writeNullableList(this.mapAttributeConfigs, out);
        SerializationUtil.writeNullableList(this.queryCacheConfigs, out);
        out.writeBoolean(this.statisticsEnabled);
        out.writeObject(this.partitioningStrategyConfig);
        out.writeUTF(this.quorumName);
        out.writeObject(this.hotRestartConfig);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_12)) {
            out.writeShort(this.metadataPolicy.getId());
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.backupCount = in.readInt();
        this.asyncBackupCount = in.readInt();
        this.timeToLiveSeconds = in.readInt();
        this.maxIdleSeconds = in.readInt();
        this.maxSizeConfig = (MaxSizeConfig)in.readObject();
        this.evictionPolicy = EvictionPolicy.valueOf(in.readUTF());
        this.mapEvictionPolicy = (MapEvictionPolicy)in.readObject();
        this.mapStoreConfig = (MapStoreConfig)in.readObject();
        this.nearCacheConfig = (NearCacheConfig)in.readObject();
        this.readBackupData = in.readBoolean();
        this.cacheDeserializedValues = CacheDeserializedValues.valueOf(in.readUTF());
        this.mergePolicyConfig = (MergePolicyConfig)in.readObject();
        this.inMemoryFormat = InMemoryFormat.valueOf(in.readUTF());
        this.wanReplicationRef = (WanReplicationRef)in.readObject();
        this.entryListenerConfigs = SerializationUtil.readNullableList(in);
        this.partitionLostListenerConfigs = SerializationUtil.readNullableList(in);
        this.mapIndexConfigs = SerializationUtil.readNullableList(in);
        this.mapAttributeConfigs = SerializationUtil.readNullableList(in);
        this.queryCacheConfigs = SerializationUtil.readNullableList(in);
        this.statisticsEnabled = in.readBoolean();
        this.partitioningStrategyConfig = (PartitioningStrategyConfig)in.readObject();
        this.quorumName = in.readUTF();
        this.hotRestartConfig = (HotRestartConfig)in.readObject();
        if (in.getVersion().isGreaterOrEqual(Versions.V3_12)) {
            this.metadataPolicy = MetadataPolicy.getById(in.readShort());
        }
    }
}

