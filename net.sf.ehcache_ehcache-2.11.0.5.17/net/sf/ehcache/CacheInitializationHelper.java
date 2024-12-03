/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

public class CacheInitializationHelper {
    private final CacheManager cacheManager;

    public CacheInitializationHelper(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void initializeEhcache(Ehcache cache) {
        this.cacheManager.initializeEhcache(cache, false);
    }

    public static void initializeEhcache(CacheManager cacheManager, Ehcache cache) {
        cacheManager.initializeEhcache(cache, false);
    }

    public static CacheManager getInitializingCacheManager(String name) {
        return CacheManager.getInitializingCacheManager(name);
    }
}

