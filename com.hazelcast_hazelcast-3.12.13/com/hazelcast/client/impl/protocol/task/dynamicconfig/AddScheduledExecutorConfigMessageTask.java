/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddScheduledExecutorConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class AddScheduledExecutorConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddScheduledExecutorConfigCodec.RequestParameters> {
    public AddScheduledExecutorConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddScheduledExecutorConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddScheduledExecutorConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddScheduledExecutorConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        ScheduledExecutorConfig config = new ScheduledExecutorConfig();
        config.setPoolSize(((DynamicConfigAddScheduledExecutorConfigCodec.RequestParameters)this.parameters).poolSize);
        config.setDurability(((DynamicConfigAddScheduledExecutorConfigCodec.RequestParameters)this.parameters).durability);
        config.setCapacity(((DynamicConfigAddScheduledExecutorConfigCodec.RequestParameters)this.parameters).capacity);
        config.setName(((DynamicConfigAddScheduledExecutorConfigCodec.RequestParameters)this.parameters).name);
        MergePolicyConfig mergePolicyConfig = this.mergePolicyConfig(((DynamicConfigAddScheduledExecutorConfigCodec.RequestParameters)this.parameters).mergePolicyExist, ((DynamicConfigAddScheduledExecutorConfigCodec.RequestParameters)this.parameters).mergePolicy, ((DynamicConfigAddScheduledExecutorConfigCodec.RequestParameters)this.parameters).mergeBatchSize);
        config.setMergePolicyConfig(mergePolicyConfig);
        return config;
    }

    @Override
    public String getMethodName() {
        return "addScheduledExecutorConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        ScheduledExecutorConfig scheduledExecutorConfig = (ScheduledExecutorConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getScheduledExecutorConfigs(), scheduledExecutorConfig.getName(), scheduledExecutorConfig);
    }
}

