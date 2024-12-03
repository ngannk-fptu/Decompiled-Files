/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapPutIfAbsentCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalMapPutIfAbsentMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapPutIfAbsentCodec.RequestParameters> {
    public TransactionalMapPutIfAbsentMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapPutIfAbsentCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap<Data, Data> map = context.getMap(((TransactionalMapPutIfAbsentCodec.RequestParameters)this.parameters).name);
        Data response = map.putIfAbsent(((TransactionalMapPutIfAbsentCodec.RequestParameters)this.parameters).key, ((TransactionalMapPutIfAbsentCodec.RequestParameters)this.parameters).value);
        return this.serializationService.toData(response);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapPutIfAbsentCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapPutIfAbsentCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapPutIfAbsentCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapPutIfAbsentCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapPutIfAbsentCodec.RequestParameters)this.parameters).name, "put", "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapPutIfAbsentCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "putIfAbsent";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMapPutIfAbsentCodec.RequestParameters)this.parameters).key, ((TransactionalMapPutIfAbsentCodec.RequestParameters)this.parameters).value};
    }
}

