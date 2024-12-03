/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddMultiMapConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.ListenerConfigHolder;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class AddMultiMapConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddMultiMapConfigCodec.RequestParameters> {
    public AddMultiMapConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddMultiMapConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddMultiMapConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddMultiMapConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        MultiMapConfig multiMapConfig = new MultiMapConfig();
        multiMapConfig.setName(((DynamicConfigAddMultiMapConfigCodec.RequestParameters)this.parameters).name);
        multiMapConfig.setValueCollectionType(((DynamicConfigAddMultiMapConfigCodec.RequestParameters)this.parameters).collectionType);
        multiMapConfig.setAsyncBackupCount(((DynamicConfigAddMultiMapConfigCodec.RequestParameters)this.parameters).asyncBackupCount);
        multiMapConfig.setBackupCount(((DynamicConfigAddMultiMapConfigCodec.RequestParameters)this.parameters).backupCount);
        multiMapConfig.setBinary(((DynamicConfigAddMultiMapConfigCodec.RequestParameters)this.parameters).binary);
        multiMapConfig.setStatisticsEnabled(((DynamicConfigAddMultiMapConfigCodec.RequestParameters)this.parameters).statisticsEnabled);
        if (((DynamicConfigAddMultiMapConfigCodec.RequestParameters)this.parameters).listenerConfigs != null && !((DynamicConfigAddMultiMapConfigCodec.RequestParameters)this.parameters).listenerConfigs.isEmpty()) {
            for (ListenerConfigHolder configHolder : ((DynamicConfigAddMultiMapConfigCodec.RequestParameters)this.parameters).listenerConfigs) {
                EntryListenerConfig entryListenerConfig = (EntryListenerConfig)configHolder.asListenerConfig(this.serializationService);
                multiMapConfig.addEntryListenerConfig(entryListenerConfig);
            }
        }
        MergePolicyConfig mergePolicyConfig = this.mergePolicyConfig(((DynamicConfigAddMultiMapConfigCodec.RequestParameters)this.parameters).mergePolicyExist, ((DynamicConfigAddMultiMapConfigCodec.RequestParameters)this.parameters).mergePolicy, ((DynamicConfigAddMultiMapConfigCodec.RequestParameters)this.parameters).mergeBatchSize);
        multiMapConfig.setMergePolicyConfig(mergePolicyConfig);
        return multiMapConfig;
    }

    @Override
    public String getMethodName() {
        return "addMultiMapConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        MultiMapConfig multiMapConfig = (MultiMapConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getMultiMapConfigs(), multiMapConfig.getName(), multiMapConfig);
    }
}

