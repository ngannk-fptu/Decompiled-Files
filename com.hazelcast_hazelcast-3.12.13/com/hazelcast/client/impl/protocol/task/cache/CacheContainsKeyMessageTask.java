/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheContainsKeyCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class CacheContainsKeyMessageTask
extends AbstractCacheMessageTask<CacheContainsKeyCodec.RequestParameters> {
    public CacheContainsKeyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheContainsKeyCodec.RequestParameters)this.parameters).name);
        return operationProvider.createContainsKeyOperation(((CacheContainsKeyCodec.RequestParameters)this.parameters).key);
    }

    @Override
    protected CacheContainsKeyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheContainsKeyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheContainsKeyCodec.encodeResponse((Boolean)response);
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheContainsKeyCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheContainsKeyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CacheContainsKeyCodec.RequestParameters)this.parameters).key};
    }

    @Override
    public String getMethodName() {
        return "containsKey";
    }
}

