/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.transaction;

import java.util.Collection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class TransactionAwareCacheManagerProxy
implements CacheManager,
InitializingBean {
    @Nullable
    private CacheManager targetCacheManager;

    public TransactionAwareCacheManagerProxy() {
    }

    public TransactionAwareCacheManagerProxy(CacheManager targetCacheManager) {
        Assert.notNull((Object)targetCacheManager, "Target CacheManager must not be null");
        this.targetCacheManager = targetCacheManager;
    }

    public void setTargetCacheManager(CacheManager targetCacheManager) {
        this.targetCacheManager = targetCacheManager;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.targetCacheManager == null) {
            throw new IllegalArgumentException("Property 'targetCacheManager' is required");
        }
    }

    @Override
    @Nullable
    public Cache getCache(String name) {
        Assert.state(this.targetCacheManager != null, "No target CacheManager set");
        Cache targetCache = this.targetCacheManager.getCache(name);
        return targetCache != null ? new TransactionAwareCacheDecorator(targetCache) : null;
    }

    @Override
    public Collection<String> getCacheNames() {
        Assert.state(this.targetCacheManager != null, "No target CacheManager set");
        return this.targetCacheManager.getCacheNames();
    }
}

