/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalqueue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalQueuePollCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class TransactionalQueuePollMessageTask
extends AbstractTransactionalMessageTask<TransactionalQueuePollCodec.RequestParameters> {
    public TransactionalQueuePollMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalQueuePollCodec.RequestParameters)this.parameters).txnId);
        TransactionalQueue queue = context.getQueue(((TransactionalQueuePollCodec.RequestParameters)this.parameters).name);
        Object item = queue.poll(((TransactionalQueuePollCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS);
        return this.serializationService.toData(item);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalQueuePollCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalQueuePollCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalQueuePollCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalQueuePollCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((TransactionalQueuePollCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalQueuePollCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "poll";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalQueuePollCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS};
    }
}

