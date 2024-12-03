/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalset;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalSetRemoveCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalSet;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalSetRemoveMessageTask
extends AbstractTransactionalMessageTask<TransactionalSetRemoveCodec.RequestParameters> {
    public TransactionalSetRemoveMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalSetRemoveCodec.RequestParameters)this.parameters).txnId);
        TransactionalSet<Data> set = context.getSet(((TransactionalSetRemoveCodec.RequestParameters)this.parameters).name);
        boolean success = set.remove(((TransactionalSetRemoveCodec.RequestParameters)this.parameters).item);
        return success;
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalSetRemoveCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalSetRemoveCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalSetRemoveCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalSetRemoveCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:setService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SetPermission(((TransactionalSetRemoveCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalSetRemoveCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "remove";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalSetRemoveCodec.RequestParameters)this.parameters).item};
    }
}

