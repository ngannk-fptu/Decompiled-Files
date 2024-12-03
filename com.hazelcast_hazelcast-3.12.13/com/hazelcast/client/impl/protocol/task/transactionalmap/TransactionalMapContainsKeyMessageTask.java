/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapContainsKeyCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.tx.TransactionalMapProxy;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalMapContainsKeyMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapContainsKeyCodec.RequestParameters> {
    public TransactionalMapContainsKeyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapContainsKeyCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap map = context.getMap(((TransactionalMapContainsKeyCodec.RequestParameters)this.parameters).name);
        return ((TransactionalMapProxy)map).containsKey(((TransactionalMapContainsKeyCodec.RequestParameters)this.parameters).key, true);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapContainsKeyCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapContainsKeyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapContainsKeyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapContainsKeyCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapContainsKeyCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapContainsKeyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "containsKey";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMapContainsKeyCodec.RequestParameters)this.parameters).key};
    }
}

