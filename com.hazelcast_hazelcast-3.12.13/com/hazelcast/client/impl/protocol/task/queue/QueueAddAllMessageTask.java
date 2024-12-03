/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueAddAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.AddAllOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class QueueAddAllMessageTask
extends AbstractPartitionMessageTask<QueueAddAllCodec.RequestParameters> {
    public QueueAddAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new AddAllOperation(((QueueAddAllCodec.RequestParameters)this.parameters).name, ((QueueAddAllCodec.RequestParameters)this.parameters).dataList);
    }

    @Override
    protected QueueAddAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueAddAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        boolean result = response != null && (Boolean)response != false;
        return QueueAddAllCodec.encodeResponse(result);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((QueueAddAllCodec.RequestParameters)this.parameters).dataList};
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueAddAllCodec.RequestParameters)this.parameters).name, "add");
    }

    @Override
    public String getMethodName() {
        return "addAll";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueueAddAllCodec.RequestParameters)this.parameters).name;
    }
}

