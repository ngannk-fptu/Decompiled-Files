/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalqueue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalQueueSizeCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalQueueSizeMessageTask
extends AbstractTransactionalMessageTask<TransactionalQueueSizeCodec.RequestParameters> {
    public TransactionalQueueSizeMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalQueueSizeCodec.RequestParameters)this.parameters).txnId);
        TransactionalQueue queue = context.getQueue(((TransactionalQueueSizeCodec.RequestParameters)this.parameters).name);
        return queue.size();
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalQueueSizeCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalQueueSizeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalQueueSizeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalQueueSizeCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((TransactionalQueueSizeCodec.RequestParameters)this.parameters).name, "add");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalQueueSizeCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "size";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

