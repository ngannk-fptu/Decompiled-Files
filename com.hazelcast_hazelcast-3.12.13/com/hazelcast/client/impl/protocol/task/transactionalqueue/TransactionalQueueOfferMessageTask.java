/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalqueue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalQueueOfferCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class TransactionalQueueOfferMessageTask
extends AbstractTransactionalMessageTask<TransactionalQueueOfferCodec.RequestParameters> {
    public TransactionalQueueOfferMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalQueueOfferCodec.RequestParameters)this.parameters).txnId);
        TransactionalQueue<Data> queue = context.getQueue(((TransactionalQueueOfferCodec.RequestParameters)this.parameters).name);
        return queue.offer(((TransactionalQueueOfferCodec.RequestParameters)this.parameters).item, ((TransactionalQueueOfferCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalQueueOfferCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalQueueOfferCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalQueueOfferCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalQueueOfferCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((TransactionalQueueOfferCodec.RequestParameters)this.parameters).name, "add");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalQueueOfferCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "offer";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalQueueOfferCodec.RequestParameters)this.parameters).item, ((TransactionalQueueOfferCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS};
    }
}

