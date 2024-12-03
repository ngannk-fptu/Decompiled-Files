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
import com.hazelcast.client.impl.protocol.codec.CacheLoadAllCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheAllPartitionsTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.HashSet;
import java.util.Map;
import javax.cache.CacheException;

public class CacheLoadAllMessageTask
extends AbstractCacheAllPartitionsTask<CacheLoadAllCodec.RequestParameters> {
    public CacheLoadAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected CacheLoadAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheLoadAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheLoadAllCodec.encodeResponse();
    }

    @Override
    protected OperationFactory createOperationFactory() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheLoadAllCodec.RequestParameters)this.parameters).name);
        HashSet<Data> keys = new HashSet<Data>(((CacheLoadAllCodec.RequestParameters)this.parameters).keys);
        return operationProvider.createLoadAllOperationFactory(keys, ((CacheLoadAllCodec.RequestParameters)this.parameters).replaceExistingValues);
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
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheLoadAllCodec.RequestParameters)this.parameters).name;
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

