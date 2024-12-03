/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmultimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMultiMapPutCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMultiMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalMultiMapPutMessageTask
extends AbstractTransactionalMessageTask<TransactionalMultiMapPutCodec.RequestParameters> {
    public TransactionalMultiMapPutMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMultiMapPutCodec.RequestParameters)this.parameters).txnId);
        TransactionalMultiMap<Data, Data> multiMap = context.getMultiMap(((TransactionalMultiMapPutCodec.RequestParameters)this.parameters).name);
        return multiMap.put(((TransactionalMultiMapPutCodec.RequestParameters)this.parameters).key, ((TransactionalMultiMapPutCodec.RequestParameters)this.parameters).value);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMultiMapPutCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMultiMapPutCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMultiMapPutCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMultiMapPutCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((TransactionalMultiMapPutCodec.RequestParameters)this.parameters).name, "put");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMultiMapPutCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "put";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMultiMapPutCodec.RequestParameters)this.parameters).key, ((TransactionalMultiMapPutCodec.RequestParameters)this.parameters).value};
    }
}

