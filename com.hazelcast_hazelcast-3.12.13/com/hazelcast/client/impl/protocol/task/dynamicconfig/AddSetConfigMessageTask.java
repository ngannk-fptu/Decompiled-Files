/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddSetConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.SetConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.List;

public class AddSetConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddSetConfigCodec.RequestParameters> {
    public AddSetConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddSetConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddSetConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddSetConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        SetConfig config = new SetConfig(((DynamicConfigAddSetConfigCodec.RequestParameters)this.parameters).name);
        config.setAsyncBackupCount(((DynamicConfigAddSetConfigCodec.RequestParameters)this.parameters).asyncBackupCount);
        config.setBackupCount(((DynamicConfigAddSetConfigCodec.RequestParameters)this.parameters).backupCount);
        config.setMaxSize(((DynamicConfigAddSetConfigCodec.RequestParameters)this.parameters).maxSize);
        config.setStatisticsEnabled(((DynamicConfigAddSetConfigCodec.RequestParameters)this.parameters).statisticsEnabled);
        if (((DynamicConfigAddSetConfigCodec.RequestParameters)this.parameters).listenerConfigs != null && !((DynamicConfigAddSetConfigCodec.RequestParameters)this.parameters).listenerConfigs.isEmpty()) {
            List<ListenerConfig> itemListenerConfigs = this.adaptListenerConfigs(((DynamicConfigAddSetConfigCodec.RequestParameters)this.parameters).listenerConfigs);
            config.setItemListenerConfigs(itemListenerConfigs);
        }
        MergePolicyConfig mergePolicyConfig = this.mergePolicyConfig(((DynamicConfigAddSetConfigCodec.RequestParameters)this.parameters).mergePolicyExist, ((DynamicConfigAddSetConfigCodec.RequestParameters)this.parameters).mergePolicy, ((DynamicConfigAddSetConfigCodec.RequestParameters)this.parameters).mergeBatchSize);
        config.setMergePolicyConfig(mergePolicyConfig);
        return config;
    }

    @Override
    public String getMethodName() {
        return "addSetConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        SetConfig setConfig = (SetConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getSetConfigs(), setConfig.getName(), setConfig);
    }
}

