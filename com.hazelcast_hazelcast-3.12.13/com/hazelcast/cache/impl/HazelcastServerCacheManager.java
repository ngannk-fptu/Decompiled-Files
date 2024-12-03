/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.HazelcastCacheManager;
import com.hazelcast.cache.impl.AbstractHazelcastCacheManager;
import com.hazelcast.cache.impl.CacheProxy;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.HazelcastServerCachingProvider;
import com.hazelcast.cache.impl.ICacheInternal;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.PreJoinCacheConfig;
import com.hazelcast.cache.impl.merge.policy.CacheMergePolicyProvider;
import com.hazelcast.cache.impl.operation.CacheGetConfigOperation;
import com.hazelcast.cache.impl.operation.CacheManagementConfigOperation;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.instance.HazelcastInstanceCacheManager;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.HazelcastInstanceProxy;
import com.hazelcast.internal.config.ConfigValidator;
import com.hazelcast.internal.config.MergePolicyValidator;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.Preconditions;
import java.net.URI;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class HazelcastServerCacheManager
extends AbstractHazelcastCacheManager {
    private final HazelcastInstanceImpl instance;
    private final NodeEngine nodeEngine;
    private final CacheService cacheService;

    public HazelcastServerCacheManager(HazelcastServerCachingProvider cachingProvider, HazelcastInstance hazelcastInstance, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, hazelcastInstance, uri, classLoader, properties);
        this.instance = hazelcastInstance instanceof HazelcastInstanceProxy ? ((HazelcastInstanceProxy)hazelcastInstance).getOriginal() : (HazelcastInstanceImpl)hazelcastInstance;
        this.nodeEngine = this.instance.node.getNodeEngine();
        this.cacheService = (CacheService)this.nodeEngine.getService("hz:impl:cacheService");
    }

    public void enableManagement(String cacheName, boolean enabled) {
        this.ensureOpen();
        Preconditions.checkNotNull(cacheName, "cacheName cannot be null");
        String cacheNameWithPrefix = this.getCacheNameWithPrefix(cacheName);
        this.cacheService.setManagementEnabled(null, cacheNameWithPrefix, enabled);
        this.enableStatisticManagementOnOtherNodes(cacheName, false, enabled);
    }

    public void enableStatistics(String cacheName, boolean enabled) {
        this.ensureOpen();
        Preconditions.checkNotNull(cacheName, "cacheName cannot be null");
        String cacheNameWithPrefix = this.getCacheNameWithPrefix(cacheName);
        this.cacheService.setStatisticsEnabled(null, cacheNameWithPrefix, enabled);
        this.enableStatisticManagementOnOtherNodes(cacheName, true, enabled);
    }

    private void enableStatisticManagementOnOtherNodes(String cacheName, boolean statOrMan, boolean enabled) {
        String cacheNameWithPrefix = this.getCacheNameWithPrefix(cacheName);
        OperationService operationService = this.nodeEngine.getOperationService();
        ArrayList futures = new ArrayList();
        for (Member member : this.nodeEngine.getClusterService().getMembers()) {
            if (member.localMember()) continue;
            CacheManagementConfigOperation op = new CacheManagementConfigOperation(cacheNameWithPrefix, statOrMan, enabled);
            InternalCompletableFuture future = operationService.invokeOnTarget("hz:impl:cacheService", op, member.getAddress());
            futures.add(future);
        }
        FutureUtil.waitWithDeadline(futures, 60L, TimeUnit.SECONDS);
    }

    @Override
    protected <K, V> void addCacheConfigIfAbsent(CacheConfig<K, V> cacheConfig) {
        this.cacheService.putCacheConfigIfAbsent(cacheConfig);
    }

    @Override
    protected <K, V> CacheConfig<K, V> findCacheConfig(String cacheName, String simpleCacheName) {
        CacheConfig config = this.cacheService.getCacheConfig(cacheName);
        if (config == null) {
            config = this.cacheService.findCacheConfig(simpleCacheName);
            if (config != null) {
                config.setManagerPrefix(cacheName.substring(0, cacheName.lastIndexOf(simpleCacheName)));
            } else {
                config = this.getCacheConfig(cacheName, simpleCacheName);
            }
        }
        if (config != null) {
            this.createCacheConfig(cacheName, config);
        }
        return config;
    }

    @Override
    protected <K, V> void createCacheConfig(String cacheName, CacheConfig<K, V> config) {
        this.cacheService.createCacheConfigOnAllMembers(PreJoinCacheConfig.of(config));
    }

    @Override
    protected <K, V> ICacheInternal<K, V> createCacheProxy(CacheConfig<K, V> cacheConfig) {
        HazelcastInstanceCacheManager cacheManager = this.instance.getCacheManager();
        CacheProxy cacheProxy = (CacheProxy)cacheManager.getCacheByFullName(cacheConfig.getNameWithPrefix());
        cacheProxy.setCacheManager(this);
        return cacheProxy;
    }

    @Override
    protected <K, V> CacheConfig<K, V> getCacheConfig(String cacheNameWithPrefix, String cacheName) {
        CacheGetConfigOperation op = new CacheGetConfigOperation(cacheNameWithPrefix, cacheName);
        int partitionId = this.nodeEngine.getPartitionService().getPartitionId(cacheNameWithPrefix);
        InternalCompletableFuture f = this.nodeEngine.getOperationService().invokeOnPartition("hz:impl:cacheService", op, partitionId);
        return (CacheConfig)f.join();
    }

    @Override
    protected void removeCacheConfigFromLocal(String cacheNameWithPrefix) {
        this.cacheService.deleteCacheConfig(cacheNameWithPrefix);
        super.removeCacheConfigFromLocal(cacheNameWithPrefix);
    }

    @Override
    protected <K, V> void validateCacheConfig(CacheConfig<K, V> cacheConfig) {
        CacheMergePolicyProvider mergePolicyProvider = this.cacheService.getMergePolicyProvider();
        ConfigValidator.checkCacheConfig(cacheConfig, mergePolicyProvider);
        Object mergePolicy = mergePolicyProvider.getMergePolicy(cacheConfig.getMergePolicy());
        MergePolicyValidator.checkMergePolicySupportsInMemoryFormat(cacheConfig.getName(), mergePolicy, cacheConfig.getInMemoryFormat(), true, this.nodeEngine.getLogger(HazelcastCacheManager.class));
    }

    public <T> T unwrap(Class<T> clazz) {
        if (HazelcastServerCacheManager.class.isAssignableFrom(clazz)) {
            return (T)this;
        }
        throw new IllegalArgumentException();
    }

    @Override
    protected void postClose() {
        if (this.properties.getProperty("hazelcast.config.location") != null) {
            this.hazelcastInstance.shutdown();
        }
    }

    @Override
    protected void onShuttingDown() {
        this.close();
    }

    public ICacheService getCacheService() {
        return this.cacheService;
    }
}

