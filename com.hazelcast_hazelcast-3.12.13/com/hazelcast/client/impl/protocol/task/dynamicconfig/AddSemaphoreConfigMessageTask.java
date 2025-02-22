/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddSemaphoreConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddSetConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.SemaphoreConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class AddSemaphoreConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddSemaphoreConfigCodec.RequestParameters> {
    public AddSemaphoreConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddSemaphoreConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddSemaphoreConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddSetConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        SemaphoreConfig config = new SemaphoreConfig();
        config.setName(((DynamicConfigAddSemaphoreConfigCodec.RequestParameters)this.parameters).name);
        config.setBackupCount(((DynamicConfigAddSemaphoreConfigCodec.RequestParameters)this.parameters).backupCount);
        config.setAsyncBackupCount(((DynamicConfigAddSemaphoreConfigCodec.RequestParameters)this.parameters).asyncBackupCount);
        config.setInitialPermits(((DynamicConfigAddSemaphoreConfigCodec.RequestParameters)this.parameters).initialPermits);
        return config;
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        SemaphoreConfig semaphoreConfig = (SemaphoreConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getSemaphoreConfigsAsMap(), semaphoreConfig.getName(), semaphoreConfig);
    }

    @Override
    public String getMethodName() {
        return "addSemaphoreConfig";
    }
}

