/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transaction;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.XATransactionFinalizeCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.TransactionPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.transaction.impl.xa.operations.FinalizeRemoteTransactionOperation;
import java.security.Permission;

public class XAFinalizeTransactionMessageTask
extends AbstractPartitionMessageTask<XATransactionFinalizeCodec.RequestParameters> {
    public XAFinalizeTransactionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected XATransactionFinalizeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return XATransactionFinalizeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return XATransactionFinalizeCodec.encodeResponse();
    }

    @Override
    protected Operation prepareOperation() {
        Object xid = this.serializationService.toData(((XATransactionFinalizeCodec.RequestParameters)this.parameters).xid);
        return new FinalizeRemoteTransactionOperation((Data)xid, ((XATransactionFinalizeCodec.RequestParameters)this.parameters).isCommit);
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

