/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.CacheNotExistsException;
import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractAllPartitionsMessageTask;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

abstract class AbstractCacheAllPartitionsTask<P>
extends AbstractAllPartitionsMessageTask<P> {
    public AbstractCacheAllPartitionsTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    protected CacheOperationProvider getOperationProvider(String name) {
        ICacheService service = (ICacheService)this.getService("hz:impl:cacheService");
        CacheConfig cacheConfig = service.getCacheConfig(name);
        if (cacheConfig == null) {
            throw new CacheNotExistsException("Cache config for cache " + name + " has not been created yet!");
        }
        return service.getCacheOperationProvider(name, cacheConfig.getInMemoryFormat());
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }
}

