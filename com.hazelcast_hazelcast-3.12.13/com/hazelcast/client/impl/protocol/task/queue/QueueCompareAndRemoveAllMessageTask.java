/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueCompareAndRemoveAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.CompareAndRemoveOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class QueueCompareAndRemoveAllMessageTask
extends AbstractPartitionMessageTask<QueueCompareAndRemoveAllCodec.RequestParameters> {
    public QueueCompareAndRemoveAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CompareAndRemoveOperation(((QueueCompareAndRemoveAllCodec.RequestParameters)this.parameters).name, ((QueueCompareAndRemoveAllCodec.RequestParameters)this.parameters).dataList, false);
    }

    @Override
    protected QueueCompareAndRemoveAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueCompareAndRemoveAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        boolean result = response != null && (Boolean)response != false;
        return QueueCompareAndRemoveAllCodec.encodeResponse(result);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((QueueCompareAndRemoveAllCodec.RequestParameters)this.parameters).dataList};
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueCompareAndRemoveAllCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "removeAll";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueueCompareAndRemoveAllCodec.RequestParameters)this.parameters).name;
    }
}

