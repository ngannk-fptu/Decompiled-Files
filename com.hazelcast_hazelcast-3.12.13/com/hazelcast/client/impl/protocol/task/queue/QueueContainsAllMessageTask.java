/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueContainsAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.ContainsOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class QueueContainsAllMessageTask
extends AbstractPartitionMessageTask<QueueContainsAllCodec.RequestParameters> {
    public QueueContainsAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ContainsOperation(((QueueContainsAllCodec.RequestParameters)this.parameters).name, ((QueueContainsAllCodec.RequestParameters)this.parameters).dataList);
    }

    @Override
    protected QueueContainsAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueContainsAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        boolean result = response != null && (Boolean)response != false;
        return QueueContainsAllCodec.encodeResponse(result);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((QueueContainsAllCodec.RequestParameters)this.parameters).dataList};
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueContainsAllCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "containsAll";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueueContainsAllCodec.RequestParameters)this.parameters).name;
    }
}

