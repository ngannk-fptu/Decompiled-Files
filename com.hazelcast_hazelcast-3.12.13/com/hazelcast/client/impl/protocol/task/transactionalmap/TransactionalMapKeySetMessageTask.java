/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapKeySetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TransactionalMapKeySetMessageTask
extends AbstractTransactionalMessageTask<TransactionalMapKeySetCodec.RequestParameters> {
    public TransactionalMapKeySetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMapKeySetCodec.RequestParameters)this.parameters).txnId);
        TransactionalMap map = context.getMap(((TransactionalMapKeySetCodec.RequestParameters)this.parameters).name);
        Set keySet = map.keySet();
        ArrayList list = new ArrayList(keySet.size());
        for (Object o : keySet) {
            list.add(this.serializationService.toData(o));
        }
        return list;
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMapKeySetCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMapKeySetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMapKeySetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMapKeySetCodec.encodeResponse((List)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((TransactionalMapKeySetCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMapKeySetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "keySet";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

