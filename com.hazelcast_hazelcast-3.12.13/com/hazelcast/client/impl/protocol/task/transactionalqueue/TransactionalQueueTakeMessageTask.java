/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalqueue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalQueueTakeCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalQueueTakeMessageTask
extends AbstractTransactionalMessageTask<TransactionalQueueTakeCodec.RequestParameters> {
    public TransactionalQueueTakeMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalQueueTakeCodec.RequestParameters)this.parameters).txnId);
        TransactionalQueue queue = context.getQueue(((TransactionalQueueTakeCodec.RequestParameters)this.parameters).name);
        Object item = queue.take();
        return this.serializationService.toData(item);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalQueueTakeCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalQueueTakeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalQueueTakeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalQueueTakeCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((TransactionalQueueTakeCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalQueueTakeCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "take";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

