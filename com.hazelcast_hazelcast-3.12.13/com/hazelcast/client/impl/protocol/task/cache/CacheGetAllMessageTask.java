/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheGetAllCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheAllPartitionsTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.cache.expiry.ExpiryPolicy;

public class CacheGetAllMessageTask
extends AbstractCacheAllPartitionsTask<CacheGetAllCodec.RequestParameters> {
    public CacheGetAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected CacheGetAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheGetAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheGetAllCodec.encodeResponse((List)response);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheGetAllCodec.RequestParameters)this.parameters).name);
        CacheService service = (CacheService)this.getService(this.getServiceName());
        ExpiryPolicy expiryPolicy = (ExpiryPolicy)service.toObject(((CacheGetAllCodec.RequestParameters)this.parameters).expiryPolicy);
        HashSet<Data> keys = new HashSet<Data>(((CacheGetAllCodec.RequestParameters)this.parameters).keys);
        return operationProvider.createGetAllOperationFactory(keys, expiryPolicy);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        ArrayList<Map.Entry<Data, Data>> reducedMap = new ArrayList<Map.Entry<Data, Data>>(map.size());
        for (Map.Entry<Integer, Object> entry : map.entrySet()) {
            MapEntries mapEntries = (MapEntries)this.nodeEngine.toObject(entry.getValue());
            mapEntries.putAllToList(reducedMap);
        }
        return reducedMap;
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheGetAllCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheGetAllCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getAll";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CacheGetAllCodec.RequestParameters)this.parameters).keys};
    }
}

