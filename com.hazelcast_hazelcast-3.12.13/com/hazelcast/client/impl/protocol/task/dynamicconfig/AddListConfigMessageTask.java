/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddListConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.ListConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.List;

public class AddListConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddListConfigCodec.RequestParameters> {
    public AddListConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddListConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddListConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddListConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        ListConfig config = new ListConfig(((DynamicConfigAddListConfigCodec.RequestParameters)this.parameters).name);
        config.setAsyncBackupCount(((DynamicConfigAddListConfigCodec.RequestParameters)this.parameters).asyncBackupCount);
        config.setBackupCount(((DynamicConfigAddListConfigCodec.RequestParameters)this.parameters).backupCount);
        config.setMaxSize(((DynamicConfigAddListConfigCodec.RequestParameters)this.parameters).maxSize);
        config.setStatisticsEnabled(((DynamicConfigAddListConfigCodec.RequestParameters)this.parameters).statisticsEnabled);
        if (((DynamicConfigAddListConfigCodec.RequestParameters)this.parameters).listenerConfigs != null && !((DynamicConfigAddListConfigCodec.RequestParameters)this.parameters).listenerConfigs.isEmpty()) {
            List<ListenerConfig> itemListenerConfigs = this.adaptListenerConfigs(((DynamicConfigAddListConfigCodec.RequestParameters)this.parameters).listenerConfigs);
            config.setItemListenerConfigs(itemListenerConfigs);
        }
        MergePolicyConfig mergePolicyConfig = this.mergePolicyConfig(((DynamicConfigAddListConfigCodec.RequestParameters)this.parameters).mergePolicyExist, ((DynamicConfigAddListConfigCodec.RequestParameters)this.parameters).mergePolicy, ((DynamicConfigAddListConfigCodec.RequestParameters)this.parameters).mergeBatchSize);
        config.setMergePolicyConfig(mergePolicyConfig);
        return config;
    }

    @Override
    public String getMethodName() {
        return "addListConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        ListConfig listConfig = (ListConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getListConfigs(), listConfig.getName(), listConfig);
    }
}

