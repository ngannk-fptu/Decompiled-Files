/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueRemainingCapacityCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.RemainingCapacityOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class QueueRemainingCapacityMessageTask
extends AbstractPartitionMessageTask<QueueRemainingCapacityCodec.RequestParameters> {
    public QueueRemainingCapacityMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new RemainingCapacityOperation(((QueueRemainingCapacityCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected QueueRemainingCapacityCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueRemainingCapacityCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        int result = response != null ? (Integer)response : 0;
        return QueueRemainingCapacityCodec.encodeResponse(result);
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueRemainingCapacityCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "remainingCapacity";
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
        return ((QueueRemainingCapacityCodec.RequestParameters)this.parameters).name;
    }
}

