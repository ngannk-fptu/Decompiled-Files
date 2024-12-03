/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueClearCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.ClearOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class QueueClearMessageTask
extends AbstractPartitionMessageTask<QueueClearCodec.RequestParameters> {
    public QueueClearMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ClearOperation(((QueueClearCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected QueueClearCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueClearCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return QueueClearCodec.encodeResponse();
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueClearCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "clear";
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
        return ((QueueClearCodec.RequestParameters)this.parameters).name;
    }
}

