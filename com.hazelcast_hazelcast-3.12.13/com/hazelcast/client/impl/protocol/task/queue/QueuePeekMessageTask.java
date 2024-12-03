/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueuePeekCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.PeekOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class QueuePeekMessageTask
extends AbstractPartitionMessageTask<QueuePeekCodec.RequestParameters> {
    public QueuePeekMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new PeekOperation(((QueuePeekCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected QueuePeekCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueuePeekCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return QueuePeekCodec.encodeResponse((Data)response);
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueuePeekCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "peek";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueuePeekCodec.RequestParameters)this.parameters).name;
    }
}

