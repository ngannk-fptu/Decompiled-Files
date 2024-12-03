/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 */
package com.atlassian.confluence.impl.cache;

import com.atlassian.cache.CacheManager;
import java.util.Collection;

public interface CacheFlusher {
    public void flushCaches();

    @Deprecated
    public static CacheFlusher cacheFlusher(CacheManager cacheManager) {
        return CacheFlusher.createCacheManagerFlusher(cacheManager);
    }

    @Deprecated
    public static CacheFlusher createCacheManagerFlusher(CacheManager cacheManager) {
        return () -> ((CacheManager)cacheManager).flushCaches();
    }

    public static CacheFlusher createCompositeCacheFlusher(Collection<CacheFlusher> cacheFlushers) {
        return () -> cacheFlushers.forEach(CacheFlusher::flushCaches);
    }
}

