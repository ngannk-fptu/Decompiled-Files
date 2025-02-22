/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transaction;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.XATransactionCreateCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.TransactionPermission;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.impl.xa.TransactionAccessor;
import com.hazelcast.transaction.impl.xa.XAService;
import java.security.Permission;

public class XATransactionCreateMessageTask
extends AbstractCallableMessageTask<XATransactionCreateCodec.RequestParameters> {
    public XATransactionCreateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        XAService xaService = (XAService)this.getService(this.getServiceName());
        String ownerUuid = this.endpoint.getUuid();
        TransactionContext context = xaService.newXATransactionContext(((XATransactionCreateCodec.RequestParameters)this.parameters).xid, ownerUuid, (int)((XATransactionCreateCodec.RequestParameters)this.parameters).timeout, true);
        TransactionAccessor.getTransaction(context).begin();
        this.endpoint.setTransactionContext(context);
        return context.getTxnId();
    }

    @Override
    protected XATransactionCreateCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return XATransactionCreateCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return XATransactionCreateCodec.encodeResponse((String)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:xaService";
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return new TransactionPermission();
    }
}

