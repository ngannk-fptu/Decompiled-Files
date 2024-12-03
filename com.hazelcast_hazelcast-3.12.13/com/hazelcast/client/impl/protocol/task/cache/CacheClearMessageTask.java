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
import com.hazelcast.client.impl.protocol.codec.CacheClearCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheAllPartitionsTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.Map;
import javax.cache.CacheException;

public class CacheClearMessageTask
extends AbstractCacheAllPartitionsTask<CacheClearCodec.RequestParameters> {
    public CacheClearMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected CacheClearCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheClearCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheClearCodec.encodeResponse();
    }

    @Override
    protected OperationFactory createOperationFactory() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheClearCodec.RequestParameters)this.parameters).name);
        return operationProvider.createClearOperationFactory();
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
        return new CachePermission(((CacheClearCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheClearCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String getMethodName() {
        return "clear";
    }
}

