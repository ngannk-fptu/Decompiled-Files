/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddQueueConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.List;

public class AddQueueConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddQueueConfigCodec.RequestParameters> {
    public AddQueueConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddQueueConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddQueueConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddQueueConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        QueueConfig config = new QueueConfig(((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).name);
        config.setAsyncBackupCount(((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).asyncBackupCount);
        config.setBackupCount(((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).backupCount);
        config.setEmptyQueueTtl(((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).emptyQueueTtl);
        config.setMaxSize(((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).maxSize);
        config.setQuorumName(((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).quorumName);
        config.setStatisticsEnabled(((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).statisticsEnabled);
        if (((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).queueStoreConfig != null) {
            QueueStoreConfig storeConfig = ((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).queueStoreConfig.asQueueStoreConfig(this.serializationService);
            config.setQueueStoreConfig(storeConfig);
        }
        if (((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).listenerConfigs != null && !((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).listenerConfigs.isEmpty()) {
            List<ListenerConfig> itemListenerConfigs = this.adaptListenerConfigs(((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).listenerConfigs);
            config.setItemListenerConfigs(itemListenerConfigs);
        }
        MergePolicyConfig mergePolicyConfig = this.mergePolicyConfig(((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).mergePolicyExist, ((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).mergePolicy, ((DynamicConfigAddQueueConfigCodec.RequestParameters)this.parameters).mergeBatchSize);
        config.setMergePolicyConfig(mergePolicyConfig);
        return config;
    }

    @Override
    public String getMethodName() {
        return "addQueueConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        QueueConfig queueConfig = (QueueConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getQueueConfigs(), queueConfig.getName(), queueConfig);
    }
}

