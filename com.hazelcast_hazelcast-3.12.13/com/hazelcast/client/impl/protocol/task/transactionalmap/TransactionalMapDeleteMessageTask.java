/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapDeleteCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalMapDeleteMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapDeleteCodec.RequestParameters> {
    public TransactionalMapDeleteMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapDeleteCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap map = context.getMap(((TransactionalMapDeleteCodec.RequestParameters)this.parameters).name);
        map.delete(((TransactionalMapDeleteCodec.RequestParameters)this.parameters).key);
        return null;
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapDeleteCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapDeleteCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapDeleteCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapDeleteCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapDeleteCodec.RequestParameters)this.parameters).name, "remove", "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapDeleteCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "delete";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMapDeleteCodec.RequestParameters)this.parameters).key};
    }
}

