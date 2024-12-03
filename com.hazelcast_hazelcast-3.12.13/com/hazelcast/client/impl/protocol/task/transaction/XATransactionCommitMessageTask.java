/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transaction;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.XATransactionCommitCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.TransactionalMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.TransactionPermission;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.transaction.impl.xa.TransactionAccessor;
import java.security.Permission;

public class XATransactionCommitMessageTask
extends AbstractCallableMessageTask<XATransactionCommitCodec.RequestParameters>
implements TransactionalMessageTask {
    public XATransactionCommitMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected XATransactionCommitCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return XATransactionCommitCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return XATransactionCommitCodec.encodeResponse();
    }

    @Override
    protected Object call() throws Exception {
        String transactionId = ((XATransactionCommitCodec.RequestParameters)this.parameters).transactionId;
        TransactionContext transactionContext = this.endpoint.getTransactionContext(transactionId);
        if (transactionContext == null) {
            throw new TransactionException("No transaction context with given transactionId: " + transactionId);
        }
        Transaction transaction = TransactionAccessor.getTransaction(transactionContext);
        if (((XATransactionCommitCodec.RequestParameters)this.parameters).onePhase) {
            transaction.prepare();
        }
        transaction.commit();
        this.endpoint.removeTransactionContext(transactionId);
        return null;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:xaService";
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

    @Override
    public Permission getRequiredPermission() {
        return new TransactionPermission();
    }
}

