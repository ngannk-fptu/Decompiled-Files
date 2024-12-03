/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueSizeCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.SizeOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class QueueSizeMessageTask
extends AbstractPartitionMessageTask<QueueSizeCodec.RequestParameters> {
    public QueueSizeMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new SizeOperation(((QueueSizeCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected QueueSizeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueSizeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return QueueSizeCodec.encodeResponse((Integer)response);
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueSizeCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "size";
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
        return ((QueueSizeCodec.RequestParameters)this.parameters).name;
    }
}

