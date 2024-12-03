/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheRemoveCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class CacheRemoveMessageTask
extends AbstractCacheMessageTask<CacheRemoveCodec.RequestParameters> {
    public CacheRemoveMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheRemoveCodec.RequestParameters)this.parameters).name);
        return operationProvider.createRemoveOperation(((CacheRemoveCodec.RequestParameters)this.parameters).key, ((CacheRemoveCodec.RequestParameters)this.parameters).currentValue, ((CacheRemoveCodec.RequestParameters)this.parameters).completionId);
    }

    @Override
    protected CacheRemoveCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheRemoveCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheRemoveCodec.encodeResponse((Boolean)response);
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheRemoveCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheRemoveCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        if (((CacheRemoveCodec.RequestParameters)this.parameters).currentValue != null) {
            return new Object[]{((CacheRemoveCodec.RequestParameters)this.parameters).key, ((CacheRemoveCodec.RequestParameters)this.parameters).currentValue};
        }
        return new Object[]{((CacheRemoveCodec.RequestParameters)this.parameters).key};
    }

    @Override
    public String getMethodName() {
        return "remove";
    }
}

