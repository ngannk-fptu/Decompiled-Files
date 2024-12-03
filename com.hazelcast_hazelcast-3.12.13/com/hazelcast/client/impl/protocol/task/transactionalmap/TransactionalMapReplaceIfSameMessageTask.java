/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapReplaceIfSameCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalMapReplaceIfSameMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapReplaceIfSameCodec.RequestParameters> {
    public TransactionalMapReplaceIfSameMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapReplaceIfSameCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap<Data, Data> map = context.getMap(((TransactionalMapReplaceIfSameCodec.RequestParameters)this.parameters).name);
        return map.replace(((TransactionalMapReplaceIfSameCodec.RequestParameters)this.parameters).key, ((TransactionalMapReplaceIfSameCodec.RequestParameters)this.parameters).oldValue, ((TransactionalMapReplaceIfSameCodec.RequestParameters)this.parameters).newValue);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapReplaceIfSameCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapReplaceIfSameCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapReplaceIfSameCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapReplaceIfSameCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapReplaceIfSameCodec.RequestParameters)this.parameters).name, "put", "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapReplaceIfSameCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "replace";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMapReplaceIfSameCodec.RequestParameters)this.parameters).key, ((TransactionalMapReplaceIfSameCodec.RequestParameters)this.parameters).oldValue, ((TransactionalMapReplaceIfSameCodec.RequestParameters)this.parameters).newValue};
    }
}

