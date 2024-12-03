/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalqueue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalQueuePeekCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class TransactionalQueuePeekMessageTask
extends AbstractTransactionalMessageTask<TransactionalQueuePeekCodec.RequestParameters> {
    public TransactionalQueuePeekMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalQueuePeekCodec.RequestParameters)this.parameters).txnId);
        TransactionalQueue queue = context.getQueue(((TransactionalQueuePeekCodec.RequestParameters)this.parameters).name);
        Object item = queue.peek(((TransactionalQueuePeekCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS);
        return this.serializationService.toData(item);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalQueuePeekCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalQueuePeekCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalQueuePeekCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalQueuePeekCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((TransactionalQueuePeekCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalQueuePeekCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "peek";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalQueuePeekCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS};
    }
}

