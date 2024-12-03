/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmultimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMultiMapGetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMultiMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TransactionalMultiMapGetMessageTask
extends AbstractTransactionalMessageTask<TransactionalMultiMapGetCodec.RequestParameters> {
    public TransactionalMultiMapGetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMultiMapGetCodec.RequestParameters)this.parameters).txnId);
        TransactionalMultiMap multiMap = context.getMultiMap(((TransactionalMultiMapGetCodec.RequestParameters)this.parameters).name);
        Collection collection = multiMap.get(((TransactionalMultiMapGetCodec.RequestParameters)this.parameters).key);
        ArrayList list = new ArrayList(collection.size());
        for (Object o : collection) {
            list.add(this.serializationService.toData(o));
        }
        return list;
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMultiMapGetCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMultiMapGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMultiMapGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMultiMapGetCodec.encodeResponse((List)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((TransactionalMultiMapGetCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMultiMapGetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "get";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMultiMapGetCodec.RequestParameters)this.parameters).key};
    }
}

