/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionallist;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalListSizeCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalList;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalListSizeMessageTask
extends AbstractTransactionalMessageTask<TransactionalListSizeCodec.RequestParameters> {
    public TransactionalListSizeMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalListSizeCodec.RequestParameters)this.parameters).txnId);
        TransactionalList list = context.getList(((TransactionalListSizeCodec.RequestParameters)this.parameters).name);
        return list.size();
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalListSizeCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalListSizeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalListSizeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalListSizeCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((TransactionalListSizeCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalListSizeCodec.RequestParameters)this.parameters).name;
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

