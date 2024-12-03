/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheGetAndReplaceCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import javax.cache.expiry.ExpiryPolicy;

public class CacheGetAndReplaceMessageTask
extends AbstractCacheMessageTask<CacheGetAndReplaceCodec.RequestParameters> {
    public CacheGetAndReplaceMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheGetAndReplaceCodec.RequestParameters)this.parameters).name);
        ExpiryPolicy expiryPolicy = (ExpiryPolicy)this.nodeEngine.toObject(((CacheGetAndReplaceCodec.RequestParameters)this.parameters).expiryPolicy);
        return operationProvider.createGetAndReplaceOperation(((CacheGetAndReplaceCodec.RequestParameters)this.parameters).key, ((CacheGetAndReplaceCodec.RequestParameters)this.parameters).value, expiryPolicy, ((CacheGetAndReplaceCodec.RequestParameters)this.parameters).completionId);
    }

    @Override
    protected CacheGetAndReplaceCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheGetAndReplaceCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheGetAndReplaceCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheGetAndReplaceCodec.RequestParameters)this.parameters).name, "read", "put");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheGetAndReplaceCodec.RequestParameters)this.parameters).name;
    }
}

