/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheGetCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import javax.cache.expiry.ExpiryPolicy;

public class CacheGetMessageTask
extends AbstractCacheMessageTask<CacheGetCodec.RequestParameters> {
    public CacheGetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheGetCodec.RequestParameters)this.parameters).name);
        ExpiryPolicy expiryPolicy = (ExpiryPolicy)this.nodeEngine.toObject(((CacheGetCodec.RequestParameters)this.parameters).expiryPolicy);
        return operationProvider.createGetOperation(((CacheGetCodec.RequestParameters)this.parameters).key, expiryPolicy);
    }

    @Override
    protected CacheGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheGetCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheGetCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheGetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        if (((CacheGetCodec.RequestParameters)this.parameters).expiryPolicy == null) {
            return new Object[]{((CacheGetCodec.RequestParameters)this.parameters).key};
        }
        return new Object[]{((CacheGetCodec.RequestParameters)this.parameters).key, ((CacheGetCodec.RequestParameters)this.parameters).expiryPolicy};
    }

    @Override
    public String getMethodName() {
        return "get";
    }
}

