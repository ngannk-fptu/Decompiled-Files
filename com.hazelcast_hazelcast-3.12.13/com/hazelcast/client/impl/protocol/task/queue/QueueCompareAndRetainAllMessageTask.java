/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueCompareAndRetainAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.CompareAndRemoveOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class QueueCompareAndRetainAllMessageTask
extends AbstractPartitionMessageTask<QueueCompareAndRetainAllCodec.RequestParameters> {
    public QueueCompareAndRetainAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CompareAndRemoveOperation(((QueueCompareAndRetainAllCodec.RequestParameters)this.parameters).name, ((QueueCompareAndRetainAllCodec.RequestParameters)this.parameters).dataList, true);
    }

    @Override
    protected QueueCompareAndRetainAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueCompareAndRetainAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        boolean result = response != null && (Boolean)response != false;
        return QueueCompareAndRetainAllCodec.encodeResponse(result);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((QueueCompareAndRetainAllCodec.RequestParameters)this.parameters).dataList};
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueCompareAndRetainAllCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "retainAll";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueueCompareAndRetainAllCodec.RequestParameters)this.parameters).name;
    }
}

