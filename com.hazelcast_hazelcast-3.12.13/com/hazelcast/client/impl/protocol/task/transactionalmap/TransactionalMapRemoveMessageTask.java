/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapRemoveCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalMapRemoveMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapRemoveCodec.RequestParameters> {
    public TransactionalMapRemoveMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapRemoveCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap map = context.getMap(((TransactionalMapRemoveCodec.RequestParameters)this.parameters).name);
        Object oldValue = map.remove(((TransactionalMapRemoveCodec.RequestParameters)this.parameters).key);
        return this.serializationService.toData(oldValue);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapRemoveCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapRemoveCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapRemoveCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapRemoveCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapRemoveCodec.RequestParameters)this.parameters).name, "remove", "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapRemoveCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "remove";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMapRemoveCodec.RequestParameters)this.parameters).key};
    }
}

