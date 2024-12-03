/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.operation.CacheManagementConfigOperation;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheManagementConfigCodec;
import com.hazelcast.client.impl.protocol.task.AbstractInvocationMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import java.security.Permission;

public class CacheManagementConfigMessageTask
extends AbstractInvocationMessageTask<CacheManagementConfigCodec.RequestParameters> {
    public CacheManagementConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CacheManagementConfigOperation(((CacheManagementConfigCodec.RequestParameters)this.parameters).name, ((CacheManagementConfigCodec.RequestParameters)this.parameters).isStat, ((CacheManagementConfigCodec.RequestParameters)this.parameters).enabled);
    }

    @Override
    protected CacheManagementConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = CacheManagementConfigCodec.decodeRequest(clientMessage);
        ((CacheManagementConfigCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((CacheManagementConfigCodec.RequestParameters)this.parameters).address);
        return (CacheManagementConfigCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheManagementConfigCodec.encodeResponse();
    }

    @Override
    protected InvocationBuilder getInvocationBuilder(Operation op) {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        return operationService.createInvocationBuilder(this.getServiceName(), op, ((CacheManagementConfigCodec.RequestParameters)this.parameters).address);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheManagementConfigCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

