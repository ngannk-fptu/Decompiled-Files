/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CachePutAllCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import javax.cache.expiry.ExpiryPolicy;

public class CachePutAllMessageTask
extends AbstractCacheMessageTask<CachePutAllCodec.RequestParameters> {
    public CachePutAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CachePutAllCodec.RequestParameters)this.parameters).name);
        ExpiryPolicy expiryPolicy = (ExpiryPolicy)this.nodeEngine.toObject(((CachePutAllCodec.RequestParameters)this.parameters).expiryPolicy);
        return operationProvider.createPutAllOperation(((CachePutAllCodec.RequestParameters)this.parameters).entries, expiryPolicy, ((CachePutAllCodec.RequestParameters)this.parameters).completionId);
    }

    @Override
    protected CachePutAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CachePutAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CachePutAllCodec.encodeResponse();
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CachePutAllCodec.RequestParameters)this.parameters).name, "put");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CachePutAllCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        if (((CachePutAllCodec.RequestParameters)this.parameters).expiryPolicy == null) {
            return new Object[]{((CachePutAllCodec.RequestParameters)this.parameters).entries};
        }
        return new Object[]{((CachePutAllCodec.RequestParameters)this.parameters).entries, ((CachePutAllCodec.RequestParameters)this.parameters).expiryPolicy};
    }

    @Override
    public String getMethodName() {
        return "putAll";
    }
}

