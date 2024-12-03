/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueuePollCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.queue.operations.PollOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class QueuePollMessageTask
extends AbstractPartitionMessageTask<QueuePollCodec.RequestParameters> {
    public QueuePollMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new PollOperation(((QueuePollCodec.RequestParameters)this.parameters).name, ((QueuePollCodec.RequestParameters)this.parameters).timeoutMillis);
    }

    @Override
    protected QueuePollCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueuePollCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return QueuePollCodec.encodeResponse((Data)response);
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueuePollCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getMethodName() {
        return "poll";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public Object[] getParameters() {
        if (((QueuePollCodec.RequestParameters)this.parameters).timeoutMillis > 0L) {
            return new Object[]{((QueuePollCodec.RequestParameters)this.parameters).timeoutMillis, TimeUnit.MILLISECONDS};
        }
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueuePollCodec.RequestParameters)this.parameters).name;
    }
}

