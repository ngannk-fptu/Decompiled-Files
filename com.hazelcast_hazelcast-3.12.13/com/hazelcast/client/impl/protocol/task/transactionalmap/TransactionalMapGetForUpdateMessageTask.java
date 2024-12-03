/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapGetForUpdateCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalMapGetForUpdateMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapGetForUpdateCodec.RequestParameters> {
    public TransactionalMapGetForUpdateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapGetForUpdateCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap map = context.getMap(((TransactionalMapGetForUpdateCodec.RequestParameters)this.parameters).name);
        Object response = map.getForUpdate(((TransactionalMapGetForUpdateCodec.RequestParameters)this.parameters).key);
        return this.serializationService.toData(response);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapGetForUpdateCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapGetForUpdateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapGetForUpdateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapGetForUpdateCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapGetForUpdateCodec.RequestParameters)this.parameters).name, "read", "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapGetForUpdateCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getForUpdate";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMapGetForUpdateCodec.RequestParameters)this.parameters).key};
    }
}

