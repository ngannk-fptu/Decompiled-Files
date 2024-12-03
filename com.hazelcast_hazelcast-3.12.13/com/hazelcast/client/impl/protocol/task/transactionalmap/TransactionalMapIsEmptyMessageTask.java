/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapIsEmptyCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalMapIsEmptyMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapIsEmptyCodec.RequestParameters> {
    public TransactionalMapIsEmptyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapIsEmptyCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap map = context.getMap(((TransactionalMapIsEmptyCodec.RequestParameters)this.parameters).name);
        return map.isEmpty();
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapIsEmptyCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapIsEmptyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapIsEmptyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapIsEmptyCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapIsEmptyCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapIsEmptyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "isEmpty";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

