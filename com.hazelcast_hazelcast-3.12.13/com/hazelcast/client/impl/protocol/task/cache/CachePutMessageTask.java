/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CachePutCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import javax.cache.expiry.ExpiryPolicy;

public class CachePutMessageTask
extends AbstractCacheMessageTask<CachePutCodec.RequestParameters> {
    public CachePutMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CachePutCodec.RequestParameters)this.parameters).name);
        ExpiryPolicy expiryPolicy = (ExpiryPolicy)this.nodeEngine.toObject(((CachePutCodec.RequestParameters)this.parameters).expiryPolicy);
        return operationProvider.createPutOperation(((CachePutCodec.RequestParameters)this.parameters).key, ((CachePutCodec.RequestParameters)this.parameters).value, expiryPolicy, ((CachePutCodec.RequestParameters)this.parameters).get, ((CachePutCodec.RequestParameters)this.parameters).completionId);
    }

    @Override
    protected CachePutCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CachePutCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CachePutCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CachePutCodec.RequestParameters)this.parameters).name, "put");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CachePutCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        if (((CachePutCodec.RequestParameters)this.parameters).expiryPolicy == null) {
            return new Object[]{((CachePutCodec.RequestParameters)this.parameters).key, ((CachePutCodec.RequestParameters)this.parameters).value};
        }
        return new Object[]{((CachePutCodec.RequestParameters)this.parameters).key, ((CachePutCodec.RequestParameters)this.parameters).value, ((CachePutCodec.RequestParameters)this.parameters).expiryPolicy};
    }

    @Override
    public String getMethodName() {
        if (((CachePutCodec.RequestParameters)this.parameters).get) {
            return "getAndPut";
        }
        return "put";
    }
}

