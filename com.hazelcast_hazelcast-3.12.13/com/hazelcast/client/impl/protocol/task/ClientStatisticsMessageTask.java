/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientStatisticsCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class ClientStatisticsMessageTask
extends AbstractCallableMessageTask<ClientStatisticsCodec.RequestParameters> {
    public ClientStatisticsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected ClientStatisticsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ClientStatisticsCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ClientStatisticsCodec.encodeResponse();
    }

    @Override
    protected Object call() throws Exception {
        this.endpoint.setClientStatistics(((ClientStatisticsCodec.RequestParameters)this.parameters).stats);
        return null;
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

