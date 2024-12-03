/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapRemoveIfSameCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalMapRemoveIfSameMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapRemoveIfSameCodec.RequestParameters> {
    public TransactionalMapRemoveIfSameMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapRemoveIfSameCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap map = context.getMap(((TransactionalMapRemoveIfSameCodec.RequestParameters)this.parameters).name);
        return map.remove(((TransactionalMapRemoveIfSameCodec.RequestParameters)this.parameters).key, ((TransactionalMapRemoveIfSameCodec.RequestParameters)this.parameters).value);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapRemoveIfSameCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapRemoveIfSameCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapRemoveIfSameCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapRemoveIfSameCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapRemoveIfSameCodec.RequestParameters)this.parameters).name, "remove", "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapRemoveIfSameCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "remove";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMapRemoveIfSameCodec.RequestParameters)this.parameters).key};
    }
}

