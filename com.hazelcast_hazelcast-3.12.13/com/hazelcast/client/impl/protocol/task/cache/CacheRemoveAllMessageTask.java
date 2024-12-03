/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheException
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheClearResponse;
import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheRemoveAllCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheAllPartitionsTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.Map;
import javax.cache.CacheException;

public class CacheRemoveAllMessageTask
extends AbstractCacheAllPartitionsTask<CacheRemoveAllCodec.RequestParameters> {
    public CacheRemoveAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected CacheRemoveAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheRemoveAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheRemoveAllCodec.encodeResponse();
    }

    @Override
    protected OperationFactory createOperationFactory() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheRemoveAllCodec.RequestParameters)this.parameters).name);
        return operationProvider.createRemoveAllOperationFactory(null, ((CacheRemoveAllCodec.RequestParameters)this.parameters).completionId);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        for (Map.Entry<Integer, Object> entry : map.entrySet()) {
            CacheClearResponse cacheClearResponse;
            Object response;
            if (entry.getValue() == null || !((response = (cacheClearResponse = (CacheClearResponse)this.nodeEngine.toObject(entry.getValue())).getResponse()) instanceof CacheException)) continue;
            throw (CacheException)((Object)response);
        }
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheRemoveAllCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheRemoveAllCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String getMethodName() {
        return "removeAll";
    }
}

