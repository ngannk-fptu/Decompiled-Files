/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionallist;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalListRemoveCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalList;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalListRemoveMessageTask
extends AbstractTransactionalMessageTask<TransactionalListRemoveCodec.RequestParameters> {
    public TransactionalListRemoveMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalListRemoveCodec.RequestParameters)this.parameters).txnId);
        TransactionalList<Data> list = context.getList(((TransactionalListRemoveCodec.RequestParameters)this.parameters).name);
        return list.remove(((TransactionalListRemoveCodec.RequestParameters)this.parameters).item);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalListRemoveCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalListRemoveCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalListRemoveCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalListRemoveCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((TransactionalListRemoveCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalListRemoveCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "remove";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalListRemoveCodec.RequestParameters)this.parameters).item};
    }
}

