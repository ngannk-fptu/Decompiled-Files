/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddDurableExecutorConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class AddDurableExecutorConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddDurableExecutorConfigCodec.RequestParameters> {
    public AddDurableExecutorConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddDurableExecutorConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddDurableExecutorConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddDurableExecutorConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        DurableExecutorConfig config = new DurableExecutorConfig(((DynamicConfigAddDurableExecutorConfigCodec.RequestParameters)this.parameters).name, ((DynamicConfigAddDurableExecutorConfigCodec.RequestParameters)this.parameters).poolSize, ((DynamicConfigAddDurableExecutorConfigCodec.RequestParameters)this.parameters).durability, ((DynamicConfigAddDurableExecutorConfigCodec.RequestParameters)this.parameters).capacity);
        return config;
    }

    @Override
    public String getMethodName() {
        return "addDurableExecutorConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        DurableExecutorConfig durableExecutorConfig = (DurableExecutorConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getDurableExecutorConfigs(), durableExecutorConfig.getName(), durableExecutorConfig);
    }
}

