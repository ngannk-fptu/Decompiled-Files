/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transaction;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionRollbackCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.TransactionPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionRollbackMessageTask
extends AbstractTransactionalMessageTask<TransactionRollbackCodec.RequestParameters> {
    public TransactionRollbackMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext transactionContext = this.endpoint.getTransactionContext(((TransactionRollbackCodec.RequestParameters)this.parameters).transactionId);
        transactionContext.rollbackTransaction();
        this.endpoint.removeTransactionContext(((TransactionRollbackCodec.RequestParameters)this.parameters).transactionId);
        return null;
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionRollbackCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionRollbackCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionRollbackCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionRollbackCodec.encodeResponse();
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

