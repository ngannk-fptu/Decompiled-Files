/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueuePutCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.OfferOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class QueuePutMessageTask
extends AbstractPartitionMessageTask<QueuePutCodec.RequestParameters> {
    public QueuePutMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new OfferOperation(((QueuePutCodec.RequestParameters)this.parameters).name, -1L, ((QueuePutCodec.RequestParameters)this.parameters).value);
    }

    @Override
    protected QueuePutCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueuePutCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return QueuePutCodec.encodeResponse();
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((QueuePutCodec.RequestParameters)this.parameters).value};
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueuePutCodec.RequestParameters)this.parameters).name, "add");
    }

    @Override
    public String getMethodName() {
        return "put";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueuePutCodec.RequestParameters)this.parameters).name;
    }
}

