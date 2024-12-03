/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.configuration.CacheEntryListenerConfiguration
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.operation.CacheListenerRegistrationOperation;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheListenerRegistrationCodec;
import com.hazelcast.client.impl.protocol.task.AbstractInvocationMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import java.security.Permission;
import javax.cache.configuration.CacheEntryListenerConfiguration;

public class CacheListenerRegistrationMessageTask
extends AbstractInvocationMessageTask<CacheListenerRegistrationCodec.RequestParameters> {
    public CacheListenerRegistrationMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        CacheEntryListenerConfiguration conf = (CacheEntryListenerConfiguration)this.nodeEngine.toObject(((CacheListenerRegistrationCodec.RequestParameters)this.parameters).listenerConfig);
        return new CacheListenerRegistrationOperation(((CacheListenerRegistrationCodec.RequestParameters)this.parameters).name, conf, ((CacheListenerRegistrationCodec.RequestParameters)this.parameters).shouldRegister);
    }

    @Override
    protected CacheListenerRegistrationCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = CacheListenerRegistrationCodec.decodeRequest(clientMessage);
        ((CacheListenerRegistrationCodec.RequestParameters)this.parameters).address = this.clientEngine.memberAddressOf(((CacheListenerRegistrationCodec.RequestParameters)this.parameters).address);
        return (CacheListenerRegistrationCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheListenerRegistrationCodec.encodeResponse();
    }

    @Override
    protected InvocationBuilder getInvocationBuilder(Operation op) {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        return operationService.createInvocationBuilder(this.getServiceName(), op, ((CacheListenerRegistrationCodec.RequestParameters)this.parameters).address);
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
        return ((CacheListenerRegistrationCodec.RequestParameters)this.parameters).name;
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

