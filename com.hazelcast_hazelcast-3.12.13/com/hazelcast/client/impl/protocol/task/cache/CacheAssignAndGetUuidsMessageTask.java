/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheAssignAndGetUuidsCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheAllPartitionsTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheAssignAndGetUuidsOperationFactory;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.Map;

public class CacheAssignAndGetUuidsMessageTask
extends AbstractCacheAllPartitionsTask<CacheAssignAndGetUuidsCodec.RequestParameters> {
    public CacheAssignAndGetUuidsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected CacheAssignAndGetUuidsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheAssignAndGetUuidsCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheAssignAndGetUuidsCodec.encodeResponse(((Map)response).entrySet());
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }

    @Override
    protected OperationFactory createOperationFactory() {
        return new CacheAssignAndGetUuidsOperationFactory();
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        return map;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }
}

