/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.lang.Nullable;

public class CompositeCacheManager
implements CacheManager,
InitializingBean {
    private final List<CacheManager> cacheManagers = new ArrayList<CacheManager>();
    private boolean fallbackToNoOpCache = false;

    public CompositeCacheManager() {
    }

    public CompositeCacheManager(CacheManager ... cacheManagers) {
        this.setCacheManagers(Arrays.asList(cacheManagers));
    }

    public void setCacheManagers(Collection<CacheManager> cacheManagers) {
        this.cacheManagers.addAll(cacheManagers);
    }

    public void setFallbackToNoOpCache(boolean fallbackToNoOpCache) {
        this.fallbackToNoOpCache = fallbackToNoOpCache;
    }

    public void afterPropertiesSet() {
        if (this.fallbackToNoOpCache) {
            this.cacheManagers.add(new NoOpCacheManager());
        }
    }

    @Override
    @Nullable
    public Cache getCache(String name) {
        for (CacheManager cacheManager : this.cacheManagers) {
            Cache cache = cacheManager.getCache(name);
            if (cache == null) continue;
            return cache;
        }
        return null;
    }

    @Override
    public Collection<String> getCacheNames() {
        LinkedHashSet<String> names = new LinkedHashSet<String>();
        for (CacheManager manager : this.cacheManagers) {
            names.addAll(manager.getCacheNames());
        }
        return Collections.unmodifiableSet(names);
    }
}

