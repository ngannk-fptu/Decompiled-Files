/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transactionalmultimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TransactionalMultiMapValueCountCodec;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMultiMap;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.transaction.TransactionContext;
import java.security.Permission;

public class TransactionalMultiMapValueCountMessageTask
extends AbstractTransactionalMessageTask<TransactionalMultiMapValueCountCodec.RequestParameters> {
    public TransactionalMultiMapValueCountMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object innerCall() throws Exception {
        TransactionContext context = this.endpoint.getTransactionContext(((TransactionalMultiMapValueCountCodec.RequestParameters)this.parameters).txnId);
        TransactionalMultiMap multiMap = context.getMultiMap(((TransactionalMultiMapValueCountCodec.RequestParameters)this.parameters).name);
        return multiMap.valueCount(((TransactionalMultiMapValueCountCodec.RequestParameters)this.parameters).key);
    }

    @Override
    protected long getClientThreadId() {
        return ((TransactionalMultiMapValueCountCodec.RequestParameters)this.parameters).threadId;
    }

    @Override
    protected TransactionalMultiMapValueCountCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMultiMapValueCountCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TransactionalMultiMapValueCountCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((TransactionalMultiMapValueCountCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TransactionalMultiMapValueCountCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "valueCount";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TransactionalMultiMapValueCountCodec.RequestParameters)this.parameters).key};
    }
}

