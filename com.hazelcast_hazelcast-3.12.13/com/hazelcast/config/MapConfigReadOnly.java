/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.CacheDeserializedValues;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.EntryListenerConfigReadOnly;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.HotRestartConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.MapIndexConfigReadOnly;
import com.hazelcast.config.MapPartitionLostListenerConfig;
import com.hazelcast.config.MapPartitionLostListenerConfigReadOnly;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.PartitioningStrategyConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.config.QueryCacheConfigReadOnly;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.map.eviction.MapEvictionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapConfigReadOnly
extends MapConfig {
    MapConfigReadOnly(MapConfig config) {
        super(config);
    }

    @Override
    public MaxSizeConfig getMaxSizeConfig() {
        MaxSizeConfig maxSizeConfig = super.getMaxSizeConfig();
        if (maxSizeConfig == null) {
            return null;
        }
        return maxSizeConfig.getAsReadOnly();
    }

    @Override
    public WanReplicationRef getWanReplicationRef() {
        WanReplicationRef wanReplicationRef = super.getWanReplicationRef();
        if (wanReplicationRef == null) {
            return null;
        }
        return wanReplicationRef.getAsReadOnly();
    }

    @Override
    public List<EntryListenerConfig> getEntryListenerConfigs() {
        List<EntryListenerConfig> listenerConfigs = super.getEntryListenerConfigs();
        ArrayList<EntryListenerConfigReadOnly> readOnlyListenerConfigs = new ArrayList<EntryListenerConfigReadOnly>(listenerConfigs.size());
        for (EntryListenerConfig listenerConfig : listenerConfigs) {
            readOnlyListenerConfigs.add(listenerConfig.getAsReadOnly());
        }
        return Collections.unmodifiableList(readOnlyListenerConfigs);
    }

    @Override
    public List<MapPartitionLostListenerConfig> getPartitionLostListenerConfigs() {
        List<MapPartitionLostListenerConfig> listenerConfigs = super.getPartitionLostListenerConfigs();
        ArrayList<MapPartitionLostListenerConfigReadOnly> readOnlyListenerConfigs = new ArrayList<MapPartitionLostListenerConfigReadOnly>(listenerConfigs.size());
        for (MapPartitionLostListenerConfig listenerConfig : listenerConfigs) {
            readOnlyListenerConfigs.add(listenerConfig.getAsReadOnly());
        }
        return Collections.unmodifiableList(readOnlyListenerConfigs);
    }

    @Override
    public List<MapIndexConfig> getMapIndexConfigs() {
        List<MapIndexConfig> mapIndexConfigs = super.getMapIndexConfigs();
        ArrayList<MapIndexConfigReadOnly> readOnlyMapIndexConfigs = new ArrayList<MapIndexConfigReadOnly>(mapIndexConfigs.size());
        for (MapIndexConfig mapIndexConfig : mapIndexConfigs) {
            readOnlyMapIndexConfigs.add(mapIndexConfig.getAsReadOnly());
        }
        return Collections.unmodifiableList(readOnlyMapIndexConfigs);
    }

    @Override
    public PartitioningStrategyConfig getPartitioningStrategyConfig() {
        PartitioningStrategyConfig partitioningStrategyConfig = super.getPartitioningStrategyConfig();
        if (partitioningStrategyConfig == null) {
            return null;
        }
        return partitioningStrategyConfig.getAsReadOnly();
    }

    @Override
    public MapStoreConfig getMapStoreConfig() {
        MapStoreConfig mapStoreConfig = super.getMapStoreConfig();
        if (mapStoreConfig == null) {
            return null;
        }
        return mapStoreConfig.getAsReadOnly();
    }

    @Override
    public NearCacheConfig getNearCacheConfig() {
        NearCacheConfig nearCacheConfig = super.getNearCacheConfig();
        if (nearCacheConfig == null) {
            return null;
        }
        return nearCacheConfig.getAsReadOnly();
    }

    @Override
    public List<QueryCacheConfig> getQueryCacheConfigs() {
        List<QueryCacheConfig> queryCacheConfigs = super.getQueryCacheConfigs();
        ArrayList<QueryCacheConfigReadOnly> readOnlyOnes = new ArrayList<QueryCacheConfigReadOnly>(queryCacheConfigs.size());
        for (QueryCacheConfig config : queryCacheConfigs) {
            readOnlyOnes.add(config.getAsReadOnly());
        }
        return Collections.unmodifiableList(readOnlyOnes);
    }

    @Override
    public MapConfig setHotRestartConfig(HotRestartConfig hotRestartConfig) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setName(String name) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setInMemoryFormat(InMemoryFormat inMemoryFormat) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setBackupCount(int backupCount) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setAsyncBackupCount(int asyncBackupCount) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setEvictionPercentage(int evictionPercentage) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setMinEvictionCheckMillis(long checkIfEvictableAfterMillis) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setTimeToLiveSeconds(int timeToLiveSeconds) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setMaxIdleSeconds(int maxIdleSeconds) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setMaxSizeConfig(MaxSizeConfig maxSizeConfig) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setEvictionPolicy(EvictionPolicy evictionPolicy) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setMapEvictionPolicy(MapEvictionPolicy mapEvictionPolicy) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setMapStoreConfig(MapStoreConfig mapStoreConfig) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setNearCacheConfig(NearCacheConfig nearCacheConfig) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setMergePolicy(String mergePolicy) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setStatisticsEnabled(boolean statisticsEnabled) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setReadBackupData(boolean readBackupData) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setWanReplicationRef(WanReplicationRef wanReplicationRef) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig addEntryListenerConfig(EntryListenerConfig listenerConfig) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setEntryListenerConfigs(List<EntryListenerConfig> listenerConfigs) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig addMapIndexConfig(MapIndexConfig mapIndexConfig) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setMapIndexConfigs(List<MapIndexConfig> mapIndexConfigs) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setPartitioningStrategyConfig(PartitioningStrategyConfig partitioningStrategyConfig) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setOptimizeQueries(boolean optimizeQueries) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setPartitionLostListenerConfigs(List<MapPartitionLostListenerConfig> listenerConfigs) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setMapAttributeConfigs(List<MapAttributeConfig> mapAttributeConfigs) {
        throw this.throwReadOnly();
    }

    @Override
    public void setQueryCacheConfigs(List<QueryCacheConfig> queryCacheConfigs) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setCacheDeserializedValues(CacheDeserializedValues cacheDeserializedValues) {
        throw this.throwReadOnly();
    }

    @Override
    public MapConfig setQuorumName(String quorumName) {
        throw this.throwReadOnly();
    }

    private UnsupportedOperationException throwReadOnly() {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

