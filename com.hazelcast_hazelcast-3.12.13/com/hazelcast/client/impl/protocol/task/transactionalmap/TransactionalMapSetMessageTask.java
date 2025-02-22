/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapSetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalMapSetMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapSetCodec.RequestParameters> {
    public TransactionalMapSetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapSetCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap<Data, Data> map = context.getMap(((TransactionalMapSetCodec.RequestParameters)this.parameters).name);
        map.set(((TransactionalMapSetCodec.RequestParameters)this.parameters).key, ((TransactionalMapSetCodec.RequestParameters)this.parameters).value);
        return null;
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapSetCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapSetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapSetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapSetCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapSetCodec.RequestParameters)this.parameters).name, "put", "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapSetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "set";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMapSetCodec.RequestParameters)this.parameters).key, ((TransactionalMapSetCodec.RequestParameters)this.parameters).value};
    }
}

