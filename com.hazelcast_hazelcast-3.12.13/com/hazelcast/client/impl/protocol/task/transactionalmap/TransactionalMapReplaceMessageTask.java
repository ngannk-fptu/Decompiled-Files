/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapReplaceCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalMapReplaceMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapReplaceCodec.RequestParameters> {
    public TransactionalMapReplaceMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapReplaceCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap<Data, Data> map = context.getMap(((TransactionalMapReplaceCodec.RequestParameters)this.parameters).name);
        Data oldValue = map.replace(((TransactionalMapReplaceCodec.RequestParameters)this.parameters).key, ((TransactionalMapReplaceCodec.RequestParameters)this.parameters).value);
        return this.serializationService.toData(oldValue);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapReplaceCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapReplaceCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapReplaceCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapReplaceCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapReplaceCodec.RequestParameters)this.parameters).name, "put", "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapReplaceCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "replace";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMapReplaceCodec.RequestParameters)this.parameters).key, ((TransactionalMapReplaceCodec.RequestParameters)this.parameters).value};
    }
}

