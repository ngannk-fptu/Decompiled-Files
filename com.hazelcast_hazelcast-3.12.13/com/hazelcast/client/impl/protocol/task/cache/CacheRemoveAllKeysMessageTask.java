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
import com.hazelcast.client.impl.protocol.codec.CacheRemoveAllKeysCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheAllPartitionsTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.HashSet;
import java.util.Map;
import javax.cache.CacheException;

public class CacheRemoveAllKeysMessageTask
extends AbstractCacheAllPartitionsTask<CacheRemoveAllKeysCodec.RequestParameters> {
    public CacheRemoveAllKeysMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected CacheRemoveAllKeysCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheRemoveAllKeysCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheRemoveAllKeysCodec.encodeResponse();
    }

    @Override
    protected OperationFactory createOperationFactory() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheRemoveAllKeysCodec.RequestParameters)this.parameters).name);
        HashSet<Data> keys = new HashSet<Data>(((CacheRemoveAllKeysCodec.RequestParameters)this.parameters).keys);
        return operationProvider.createRemoveAllOperationFactory(keys, ((CacheRemoveAllKeysCodec.RequestParameters)this.parameters).completionId);
    }

    @Override
    protected ClientMessage reduce(Map<Integer, Object> map) {
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
        return new CachePermission(((CacheRemoveAllKeysCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheRemoveAllKeysCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CacheRemoveAllKeysCodec.RequestParameters)this.parameters).keys};
    }

    @Override
    public String getMethodName() {
        return "removeAll";
    }
}

