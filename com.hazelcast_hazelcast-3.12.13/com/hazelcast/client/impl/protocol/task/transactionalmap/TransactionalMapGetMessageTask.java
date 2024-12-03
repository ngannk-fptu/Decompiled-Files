/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapGetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.tx.TransactionalMapProxy;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalMapGetMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapGetCodec.RequestParameters> {
    public TransactionalMapGetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapGetCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap map = context.getMap(((TransactionalMapGetCodec.RequestParameters)this.parameters).name);
        Object response = ((TransactionalMapProxy)map).get(((TransactionalMapGetCodec.RequestParameters)this.parameters).key, true);
        return this.serializationService.toData(response);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapGetCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapGetCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapGetCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapGetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "get";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMapGetCodec.RequestParameters)this.parameters).key};
    }
}

