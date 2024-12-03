/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.cache.Cache
 *  org.springframework.cache.CacheManager
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
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
        Assert.notNull((Object)targetCacheManager, (String)"Target CacheManager must not be null");
        this.targetCacheManager = targetCacheManager;
    }

    public void setTargetCacheManager(CacheManager targetCacheManager) {
        this.targetCacheManager = targetCacheManager;
    }

    public void afterPropertiesSet() {
        if (this.targetCacheManager == null) {
            throw new IllegalArgumentException("Property 'targetCacheManager' is required");
        }
    }

    @Nullable
    public Cache getCache(String name) {
        Assert.state((this.targetCacheManager != null ? 1 : 0) != 0, (String)"No target CacheManager set");
        Cache targetCache = this.targetCacheManager.getCache(name);
        return targetCache != null ? new TransactionAwareCacheDecorator(targetCache) : null;
    }

    public Collection<String> getCacheNames() {
        Assert.state((this.targetCacheManager != null ? 1 : 0) != 0, (String)"No target CacheManager set");
        return this.targetCacheManager.getCacheNames();
    }
}

