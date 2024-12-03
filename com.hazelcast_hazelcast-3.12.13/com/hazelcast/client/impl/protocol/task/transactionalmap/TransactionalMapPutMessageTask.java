/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapPutCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class TransactionalMapPutMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapPutCodec.RequestParameters> {
    public TransactionalMapPutMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapPutCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap<Data, Data> map = context.getMap(((TransactionalMapPutCodec.RequestParameters)this.parameters).name);
        Data response = map.put(((TransactionalMapPutCodec.RequestParameters)this.parameters).key, ((TransactionalMapPutCodec.RequestParameters)this.parameters).value, ((TransactionalMapPutCodec.RequestParameters)this.parameters).ttl, TimeUnit.MILLISECONDS);
        return this.serializationService.toData(response);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapPutCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapPutCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapPutCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapPutCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapPutCodec.RequestParameters)this.parameters).name, "put", "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapPutCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "put";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMapPutCodec.RequestParameters)this.parameters).key, ((TransactionalMapPutCodec.RequestParameters)this.parameters).value, ((TransactionalMapPutCodec.RequestParameters)this.parameters).ttl};
    }
}

