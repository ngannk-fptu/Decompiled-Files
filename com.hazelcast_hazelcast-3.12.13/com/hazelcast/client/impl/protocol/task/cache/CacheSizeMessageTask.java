/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheSizeCodec;
import com.hazelcast.client.impl.protocol.task.cache.AbstractCacheAllPartitionsTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.util.MapUtil;
import java.security.Permission;
import java.util.Map;

public class CacheSizeMessageTask
extends AbstractCacheAllPartitionsTask<CacheSizeCodec.RequestParameters> {
    public CacheSizeMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        CacheOperationProvider operationProvider = this.getOperationProvider(((CacheSizeCodec.RequestParameters)this.parameters).name);
        return operationProvider.createSizeOperationFactory();
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        long total = 0L;
        CacheService service = (CacheService)this.getService(this.getServiceName());
        for (Object result : map.values()) {
            Integer size = (Integer)service.toObject(result);
            total += (long)size.intValue();
        }
        return MapUtil.toIntSize(total);
    }

    @Override
    protected CacheSizeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheSizeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheSizeCodec.encodeResponse((Integer)response);
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheSizeCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheSizeCodec.RequestParameters)this.parameters).name;
    }
}

