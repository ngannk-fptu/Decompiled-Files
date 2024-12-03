/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheGetAndRemoveCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class CacheGetAndRemoveMessageTask
extends AbstractCacheMessageTask<CacheGetAndRemoveCodec.RequestParameters> {
    public CacheGetAndRemoveMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheGetAndRemoveCodec.RequestParameters)this.parameters).name);
        return operationProvider.createGetAndRemoveOperation(((CacheGetAndRemoveCodec.RequestParameters)this.parameters).key, ((CacheGetAndRemoveCodec.RequestParameters)this.parameters).completionId);
    }

    @Override
    protected CacheGetAndRemoveCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheGetAndRemoveCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheGetAndRemoveCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheGetAndRemoveCodec.RequestParameters)this.parameters).name, "read", "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheGetAndRemoveCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CacheGetAndRemoveCodec.RequestParameters)this.parameters).key};
    }

    @Override
    public String getMethodName() {
        return "getAndRemove";
    }
}

