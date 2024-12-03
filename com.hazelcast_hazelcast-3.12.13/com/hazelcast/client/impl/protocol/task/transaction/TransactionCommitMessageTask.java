/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transaction;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionCommitCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.TransactionPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionCommitMessageTask
extends AbstractTransactionalMessageTask<TransactionCommitCodec.RequestParameters> {
    public TransactionCommitMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext transactionContext = this.endpoint.getTransactionContext(((TransactionCommitCodec.RequestParameters)this.parameters).transactionId);
        transactionContext.commitTransaction();
        this.endpoint.removeTransactionContext(((TransactionCommitCodec.RequestParameters)this.parameters).transactionId);
        return null;
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionCommitCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionCommitCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionCommitCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionCommitCodec.encodeResponse();
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

