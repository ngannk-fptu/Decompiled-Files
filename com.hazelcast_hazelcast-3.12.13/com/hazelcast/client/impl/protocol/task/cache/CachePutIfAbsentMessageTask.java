/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CachePutIfAbsentCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import javax.cache.expiry.ExpiryPolicy;

public class CachePutIfAbsentMessageTask
extends AbstractCacheMessageTask<CachePutIfAbsentCodec.RequestParameters> {
    public CachePutIfAbsentMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CachePutIfAbsentCodec.RequestParameters)this.parameters).name);
        ExpiryPolicy expiryPolicy = (ExpiryPolicy)this.nodeEngine.toObject(((CachePutIfAbsentCodec.RequestParameters)this.parameters).expiryPolicy);
        return operationProvider.createPutIfAbsentOperation(((CachePutIfAbsentCodec.RequestParameters)this.parameters).key, ((CachePutIfAbsentCodec.RequestParameters)this.parameters).value, expiryPolicy, ((CachePutIfAbsentCodec.RequestParameters)this.parameters).completionId);
    }

    @Override
    protected CachePutIfAbsentCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CachePutIfAbsentCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CachePutIfAbsentCodec.encodeResponse((Boolean)response);
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CachePutIfAbsentCodec.RequestParameters)this.parameters).name, "put");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CachePutIfAbsentCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        if (((CachePutIfAbsentCodec.RequestParameters)this.parameters).expiryPolicy == null) {
            return new Object[]{((CachePutIfAbsentCodec.RequestParameters)this.parameters).key, ((CachePutIfAbsentCodec.RequestParameters)this.parameters).value};
        }
        return new Object[]{((CachePutIfAbsentCodec.RequestParameters)this.parameters).key, ((CachePutIfAbsentCodec.RequestParameters)this.parameters).value, ((CachePutIfAbsentCodec.RequestParameters)this.parameters).expiryPolicy};
    }

    @Override
    public String getMethodName() {
        return "putIfAbsent";
    }
}

