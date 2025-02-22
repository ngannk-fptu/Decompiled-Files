/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddMapConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.QueryCacheConfigHolder;
import com.hazelcast.config.CacheDeserializedValues;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.MetadataPolicy;
import com.hazelcast.config.PartitioningStrategyConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.map.eviction.MapEvictionPolicy;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.ArrayList;

public class AddMapConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddMapConfigCodec.RequestParameters> {
    public AddMapConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddMapConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddMapConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddMapConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        MapConfig config = new MapConfig(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).name);
        config.setAsyncBackupCount(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).asyncBackupCount);
        config.setBackupCount(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).backupCount);
        config.setCacheDeserializedValues(CacheDeserializedValues.valueOf(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).cacheDeserializedValues));
        config.setEvictionPolicy(EvictionPolicy.valueOf(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).evictionPolicy));
        if (((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).listenerConfigs != null && !((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).listenerConfigs.isEmpty()) {
            config.setEntryListenerConfigs(this.adaptListenerConfigs(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).listenerConfigs));
        }
        config.setHotRestartConfig(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).hotRestartConfig);
        config.setInMemoryFormat(InMemoryFormat.valueOf(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).inMemoryFormat));
        config.setMapAttributeConfigs(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).mapAttributeConfigs);
        config.setReadBackupData(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).readBackupData);
        config.setStatisticsEnabled(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).statisticsEnabled);
        if (((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).mapEvictionPolicy != null) {
            MapEvictionPolicy evictionPolicy = (MapEvictionPolicy)this.serializationService.toObject(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).mapEvictionPolicy);
            config.setMapEvictionPolicy(evictionPolicy);
        }
        config.setMapIndexConfigs(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).mapIndexConfigs);
        if (((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).mapStoreConfig != null) {
            config.setMapStoreConfig(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).mapStoreConfig.asMapStoreConfig(this.serializationService));
        }
        config.setTimeToLiveSeconds(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).timeToLiveSeconds);
        config.setMaxIdleSeconds(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).maxIdleSeconds);
        config.setMaxSizeConfig(new MaxSizeConfig(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).maxSizeConfigSize, MaxSizeConfig.MaxSizePolicy.valueOf(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).maxSizeConfigMaxSizePolicy)));
        if (((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).mergeBatchSizeExist) {
            MergePolicyConfig mergePolicyConfig = this.mergePolicyConfig(true, ((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).mergePolicy, ((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).mergeBatchSize);
            config.setMergePolicyConfig(mergePolicyConfig);
        }
        if (((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).nearCacheConfig != null) {
            config.setNearCacheConfig(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).nearCacheConfig.asNearCacheConfig(this.serializationService));
        }
        config.setPartitioningStrategyConfig(this.getPartitioningStrategyConfig());
        if (((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).partitionLostListenerConfigs != null && !((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).partitionLostListenerConfigs.isEmpty()) {
            config.setPartitionLostListenerConfigs(this.adaptListenerConfigs(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).partitionLostListenerConfigs));
        }
        config.setQuorumName(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).quorumName);
        if (((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).queryCacheConfigs != null && !((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).queryCacheConfigs.isEmpty()) {
            ArrayList<QueryCacheConfig> queryCacheConfigs = new ArrayList<QueryCacheConfig>(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).queryCacheConfigs.size());
            for (QueryCacheConfigHolder holder : ((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).queryCacheConfigs) {
                queryCacheConfigs.add(holder.asQueryCacheConfig(this.serializationService));
            }
            config.setQueryCacheConfigs(queryCacheConfigs);
        }
        config.setWanReplicationRef(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).wanReplicationRef);
        if (((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).metadataPolicyExist) {
            config.setMetadataPolicy(MetadataPolicy.getById(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).metadataPolicy));
        }
        return config;
    }

    private PartitioningStrategyConfig getPartitioningStrategyConfig() {
        if (((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).partitioningStrategyClassName != null) {
            return new PartitioningStrategyConfig(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).partitioningStrategyClassName);
        }
        if (((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).partitioningStrategyImplementation != null) {
            PartitioningStrategy partitioningStrategy = (PartitioningStrategy)this.serializationService.toObject(((DynamicConfigAddMapConfigCodec.RequestParameters)this.parameters).partitioningStrategyImplementation);
            return new PartitioningStrategyConfig(partitioningStrategy);
        }
        return null;
    }

    @Override
    public String getMethodName() {
        return "addMapConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        MapConfig mapConfig = (MapConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getMapConfigs(), mapConfig.getName(), mapConfig);
    }
}

