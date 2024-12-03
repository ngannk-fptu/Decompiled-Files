/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionallist;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalListAddCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalList;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalListAddMessageTask
extends AbstractTransactionalMessageTask<TransactionalListAddCodec.RequestParameters> {
    public TransactionalListAddMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalListAddCodec.RequestParameters)this.parameters).txnId);
        TransactionalList<Data> list = context.getList(((TransactionalListAddCodec.RequestParameters)this.parameters).name);
        return list.add(((TransactionalListAddCodec.RequestParameters)this.parameters).item);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalListAddCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalListAddCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalListAddCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalListAddCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((TransactionalListAddCodec.RequestParameters)this.parameters).name, "add");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalListAddCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "set";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalListAddCodec.RequestParameters)this.parameters).item};
    }
}

