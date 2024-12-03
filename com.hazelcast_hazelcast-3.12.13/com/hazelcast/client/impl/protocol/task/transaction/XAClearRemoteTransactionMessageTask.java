/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.transaction;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.XATransactionClearRemoteCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.TransactionPermission;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.transaction.impl.xa.operations.ClearRemoteTransactionOperation;
import java.security.Permission;

public class XAClearRemoteTransactionMessageTask
extends AbstractCallableMessageTask<XATransactionClearRemoteCodec.RequestParameters> {
    private static final int TRY_COUNT = 100;

    public XAClearRemoteTransactionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected XATransactionClearRemoteCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return XATransactionClearRemoteCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return XATransactionClearRemoteCodec.encodeResponse();
    }

    @Override
    protected Object call() throws Exception {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        InternalPartitionService partitionService = this.nodeEngine.getPartitionService();
        Object xidData = this.serializationService.toData(((XATransactionClearRemoteCodec.RequestParameters)this.parameters).xid);
        ClearRemoteTransactionOperation op = new ClearRemoteTransactionOperation((Data)xidData);
        op.setCallerUuid(this.endpoint.getUuid());
        int partitionId = partitionService.getPartitionId((Data)xidData);
        InvocationBuilder builder = operationService.createInvocationBuilder(this.getServiceName(), (Operation)op, partitionId);
        builder.setTryCount(100).setResultDeserialized(false);
        builder.invoke();
        return XATransactionClearRemoteCodec.encodeResponse();
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

