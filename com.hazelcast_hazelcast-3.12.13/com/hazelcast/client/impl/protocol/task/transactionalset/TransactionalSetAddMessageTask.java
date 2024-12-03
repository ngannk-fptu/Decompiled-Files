/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalset;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalSetAddCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalSet;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalSetAddMessageTask
extends AbstractTransactionalMessageTask<TransactionalSetAddCodec.RequestParameters> {
    public TransactionalSetAddMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalSetAddCodec.RequestParameters)this.parameters).txnId);
        TransactionalSet<Data> set = context.getSet(((TransactionalSetAddCodec.RequestParameters)this.parameters).name);
        return set.add(((TransactionalSetAddCodec.RequestParameters)this.parameters).item);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalSetAddCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalSetAddCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalSetAddCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalSetAddCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:setService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SetPermission(((TransactionalSetAddCodec.RequestParameters)this.parameters).name, "add");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalSetAddCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "set";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalSetAddCodec.RequestParameters)this.parameters).item};
    }
}

