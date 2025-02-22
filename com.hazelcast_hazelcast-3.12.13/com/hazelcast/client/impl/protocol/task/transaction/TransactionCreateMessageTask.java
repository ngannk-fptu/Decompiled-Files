/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transaction;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionCreateCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.TransactionPermission;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.impl.TransactionManagerServiceImpl;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class TransactionCreateMessageTask
extends AbstractTransactionalMessageTask<TransactionCreateCodec.RequestParameters> {
    public TransactionCreateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionOptions options = new TransactionOptions();
        options.setDurability(((TransactionCreateCodec.RequestParameters)this.parameters).durability);
        options.setTimeout(((TransactionCreateCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS);
        options.setTransactionType(TransactionOptions.TransactionType.getByValue(((TransactionCreateCodec.RequestParameters)this.parameters).transactionType));
        TransactionManagerServiceImpl transactionManager = (TransactionManagerServiceImpl)this.clientEngine.getTransactionManagerService();
        TransactionContext context = transactionManager.newClientTransactionContext(options, this.endpoint.getUuid());
        context.beginTransaction();
        this.endpoint.setTransactionContext(context);
        return context.getTxnId();
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionCreateCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionCreateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionCreateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionCreateCodec.encodeResponse((String)response);
    }

    @Override
    public String getServiceName() {
        return "hz:core:clientEngine";
    }

    @Override
    public Permission getRequiredPermission() {
        return new TransactionPermission();
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

