/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ehcache.CacheManager
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Status
 *  org.springframework.cache.Cache
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.cache.ehcache;

import java.util.Collection;
import java.util.LinkedHashSet;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheManagerUtils;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class EhCacheCacheManager
extends AbstractTransactionSupportingCacheManager {
    @Nullable
    private CacheManager cacheManager;

    public EhCacheCacheManager() {
    }

    public EhCacheCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setCacheManager(@Nullable CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Nullable
    public CacheManager getCacheManager() {
        return this.cacheManager;
    }

    public void afterPropertiesSet() {
        if (this.getCacheManager() == null) {
            this.setCacheManager(EhCacheManagerUtils.buildCacheManager());
        }
        super.afterPropertiesSet();
    }

    protected Collection<Cache> loadCaches() {
        CacheManager cacheManager = this.getCacheManager();
        Assert.state((cacheManager != null ? 1 : 0) != 0, (String)"No CacheManager set");
        Status status = cacheManager.getStatus();
        if (!Status.STATUS_ALIVE.equals(status)) {
            throw new IllegalStateException("An 'alive' EhCache CacheManager is required - current cache is " + status.toString());
        }
        String[] names = this.getCacheManager().getCacheNames();
        LinkedHashSet<Cache> caches = new LinkedHashSet<Cache>(names.length);
        for (String name : names) {
            caches.add(new EhCacheCache(this.getCacheManager().getEhcache(name)));
        }
        return caches;
    }

    protected Cache getMissingCache(String name) {
        CacheManager cacheManager = this.getCacheManager();
        Assert.state((cacheManager != null ? 1 : 0) != 0, (String)"No CacheManager set");
        Ehcache ehcache = cacheManager.getEhcache(name);
        if (ehcache != null) {
            return new EhCacheCache(ehcache);
        }
        return null;
    }
}

