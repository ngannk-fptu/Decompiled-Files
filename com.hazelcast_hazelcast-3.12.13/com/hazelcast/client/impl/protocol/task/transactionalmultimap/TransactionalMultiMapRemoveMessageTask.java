/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmultimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMultiMapRemoveCodec;
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

public class TransactionalMultiMapRemoveMessageTask
extends AbstractTransactionalMessageTask<TransactionalMultiMapRemoveCodec.RequestParameters> {
    public TransactionalMultiMapRemoveMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMultiMapRemoveCodec.RequestParameters)this.parameters).txnId);
        TransactionalMultiMap multiMap = context.getMultiMap(((TransactionalMultiMapRemoveCodec.RequestParameters)this.parameters).name);
        Collection collection = multiMap.remove(((TransactionalMultiMapRemoveCodec.RequestParameters)this.parameters).key);
        ArrayList list = new ArrayList(collection.size());
        for (Object o : collection) {
            list.add(this.serializationService.toData(o));
        }
        return list;
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMultiMapRemoveCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMultiMapRemoveCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMultiMapRemoveCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMultiMapRemoveCodec.encodeResponse((List)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((TransactionalMultiMapRemoveCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMultiMapRemoveCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "remove";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMultiMapRemoveCodec.RequestParameters)this.parameters).key};
    }
}

