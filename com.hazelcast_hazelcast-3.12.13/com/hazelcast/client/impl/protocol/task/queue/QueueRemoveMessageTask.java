/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueRemoveCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.RemoveOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class QueueRemoveMessageTask
extends AbstractPartitionMessageTask<QueueRemoveCodec.RequestParameters> {
    public QueueRemoveMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new RemoveOperation(((QueueRemoveCodec.RequestParameters)this.parameters).name, ((QueueRemoveCodec.RequestParameters)this.parameters).value);
    }

    @Override
    protected QueueRemoveCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueRemoveCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        boolean result = response != null && (Boolean)response != false;
        return QueueRemoveCodec.encodeResponse(result);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((QueueRemoveCodec.RequestParameters)this.parameters).value};
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueRemoveCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "remove";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueueRemoveCodec.RequestParameters)this.parameters).name;
    }
}

