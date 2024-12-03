/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmultimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMultiMapRemoveEntryCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMultiMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalMultiMapRemoveEntryMessageTask
extends AbstractTransactionalMessageTask<TransactionalMultiMapRemoveEntryCodec.RequestParameters> {
    public TransactionalMultiMapRemoveEntryMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMultiMapRemoveEntryCodec.RequestParameters)this.parameters).txnId);
        TransactionalMultiMap multiMap = context.getMultiMap(((TransactionalMultiMapRemoveEntryCodec.RequestParameters)this.parameters).name);
        return multiMap.remove(((TransactionalMultiMapRemoveEntryCodec.RequestParameters)this.parameters).key, ((TransactionalMultiMapRemoveEntryCodec.RequestParameters)this.parameters).value);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMultiMapRemoveEntryCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMultiMapRemoveEntryCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMultiMapRemoveEntryCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMultiMapRemoveEntryCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((TransactionalMultiMapRemoveEntryCodec.RequestParameters)this.parameters).name, "put");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMultiMapRemoveEntryCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "removeEntry";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMultiMapRemoveEntryCodec.RequestParameters)this.parameters).key, ((TransactionalMultiMapRemoveEntryCodec.RequestParameters)this.parameters).value};
    }
}

