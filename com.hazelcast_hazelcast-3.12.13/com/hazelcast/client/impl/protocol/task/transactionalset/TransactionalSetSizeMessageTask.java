/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalset;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalSetSizeCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalSet;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalSetSizeMessageTask
extends AbstractTransactionalMessageTask<TransactionalSetSizeCodec.RequestParameters> {
    public TransactionalSetSizeMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalSetSizeCodec.RequestParameters)this.parameters).txnId);
        TransactionalSet set = context.getSet(((TransactionalSetSizeCodec.RequestParameters)this.parameters).name);
        return set.size();
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalSetSizeCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalSetSizeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalSetSizeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalSetSizeCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:setService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SetPermission(((TransactionalSetSizeCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalSetSizeCodec.RequestParameters)this.parameters).name;
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

