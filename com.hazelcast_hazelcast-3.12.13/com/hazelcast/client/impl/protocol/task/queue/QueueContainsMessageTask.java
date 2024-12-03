/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueContainsCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.ContainsOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.Collections;

public class QueueContainsMessageTask
extends AbstractPartitionMessageTask<QueueContainsCodec.RequestParameters> {
    public QueueContainsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ContainsOperation(((QueueContainsCodec.RequestParameters)this.parameters).name, Collections.singleton(((QueueContainsCodec.RequestParameters)this.parameters).value));
    }

    @Override
    protected QueueContainsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueContainsCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        boolean result = response != null && (Boolean)response != false;
        return QueueContainsCodec.encodeResponse(result);
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((QueueContainsCodec.RequestParameters)this.parameters).value};
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueContainsCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getMethodName() {
        return "contains";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueueContainsCodec.RequestParameters)this.parameters).name;
    }
}

