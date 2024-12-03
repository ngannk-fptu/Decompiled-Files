/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueIsEmptyCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.IsEmptyOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class QueueIsEmptyMessageTask
extends AbstractPartitionMessageTask<QueueIsEmptyCodec.RequestParameters> {
    public QueueIsEmptyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new IsEmptyOperation(((QueueIsEmptyCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return QueueIsEmptyCodec.encodeResponse((Boolean)response);
    }

    @Override
    protected QueueIsEmptyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueIsEmptyCodec.decodeRequest(clientMessage);
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueIsEmptyCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "isEmpty";
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
        return ((QueueIsEmptyCodec.RequestParameters)this.parameters).name;
    }
}

