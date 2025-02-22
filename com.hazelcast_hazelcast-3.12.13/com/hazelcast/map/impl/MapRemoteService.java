/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.internal.config.MergePolicyValidator;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.proxy.MapProxyImpl;
import com.hazelcast.map.impl.proxy.NearCachedMapProxyImpl;
import com.hazelcast.map.merge.MergePolicyProvider;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.RemoteService;

class MapRemoteService
implements RemoteService {
    protected final MapServiceContext mapServiceContext;
    protected final NodeEngine nodeEngine;

    MapRemoteService(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
        this.nodeEngine = mapServiceContext.getNodeEngine();
    }

    @Override
    public DistributedObject createDistributedObject(String name) {
        Config config = this.nodeEngine.getConfig();
        MapConfig mapConfig = config.findMapConfig(name);
        MergePolicyProvider mergePolicyProvider = this.mapServiceContext.getMergePolicyProvider();
        ConfigValidator.checkMapConfig(mapConfig, mergePolicyProvider);
        Object mergePolicy = mergePolicyProvider.getMergePolicy(mapConfig.getMergePolicyConfig().getPolicy());
        MergePolicyValidator.checkMergePolicySupportsInMemoryFormat(name, mergePolicy, mapConfig.getInMemoryFormat(), true, this.nodeEngine.getLogger(this.getClass()));
        if (mapConfig.isNearCacheEnabled()) {
            ConfigValidator.checkNearCacheConfig(name, mapConfig.getNearCacheConfig(), config.getNativeMemoryConfig(), false);
            return new NearCachedMapProxyImpl(name, this.mapServiceContext.getService(), this.nodeEngine, mapConfig);
        }
        return new MapProxyImpl(name, this.mapServiceContext.getService(), this.nodeEngine, mapConfig);
    }

    @Override
    public void destroyDistributedObject(String name) {
        this.mapServiceContext.destroyMap(name);
    }
}

