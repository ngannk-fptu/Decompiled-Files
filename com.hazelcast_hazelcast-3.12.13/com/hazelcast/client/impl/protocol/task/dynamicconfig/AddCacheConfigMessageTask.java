/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddCacheConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.CachePartitionLostListenerConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.ArrayList;
import java.util.List;

public class AddCacheConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddCacheConfigCodec.RequestParameters> {
    public AddCacheConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddCacheConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddCacheConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddCacheConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        CacheSimpleConfig config = new CacheSimpleConfig();
        config.setAsyncBackupCount(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).asyncBackupCount);
        config.setBackupCount(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).backupCount);
        config.setCacheEntryListeners(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).cacheEntryListeners);
        config.setCacheLoader(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).cacheLoader);
        config.setCacheLoaderFactory(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).cacheLoaderFactory);
        config.setCacheWriter(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).cacheWriter);
        config.setCacheWriterFactory(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).cacheWriterFactory);
        config.setDisablePerEntryInvalidationEvents(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).disablePerEntryInvalidationEvents);
        config.setEvictionConfig(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).evictionConfig.asEvictionConfg(this.serializationService));
        if (((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).expiryPolicyFactoryClassName != null) {
            config.setExpiryPolicyFactory(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).expiryPolicyFactoryClassName);
        } else if (((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).timedExpiryPolicyFactoryConfig != null) {
            CacheSimpleConfig.ExpiryPolicyFactoryConfig expiryPolicyFactoryConfig = new CacheSimpleConfig.ExpiryPolicyFactoryConfig(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).timedExpiryPolicyFactoryConfig);
            config.setExpiryPolicyFactoryConfig(expiryPolicyFactoryConfig);
        }
        config.setHotRestartConfig(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).hotRestartConfig);
        config.setInMemoryFormat(InMemoryFormat.valueOf(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).inMemoryFormat));
        config.setKeyType(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).keyType);
        config.setManagementEnabled(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).managementEnabled);
        config.setMergePolicy(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).mergePolicy);
        config.setName(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).name);
        if (((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).partitionLostListenerConfigs != null && !((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).partitionLostListenerConfigs.isEmpty()) {
            List<ListenerConfig> listenerConfigs = this.adaptListenerConfigs(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).partitionLostListenerConfigs);
            config.setPartitionLostListenerConfigs(listenerConfigs);
        } else {
            config.setPartitionLostListenerConfigs(new ArrayList<CachePartitionLostListenerConfig>());
        }
        config.setQuorumName(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).quorumName);
        config.setReadThrough(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).readThrough);
        config.setStatisticsEnabled(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).statisticsEnabled);
        config.setValueType(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).valueType);
        config.setWanReplicationRef(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).wanReplicationRef);
        config.setWriteThrough(((DynamicConfigAddCacheConfigCodec.RequestParameters)this.parameters).writeThrough);
        return config;
    }

    @Override
    public String getMethodName() {
        return "addCacheConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        CacheSimpleConfig cacheConfig = (CacheSimpleConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getCacheConfigs(), cacheConfig.getName(), cacheConfig);
    }
}

