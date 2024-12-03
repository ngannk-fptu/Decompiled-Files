/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheManager
 *  javax.cache.Caching
 */
package org.hibernate.cache.jcache;

import javax.cache.CacheManager;
import javax.cache.Caching;

public class JCacheHelper {
    public static CacheManager locateStandardCacheManager() {
        return Caching.getCachingProvider().getCacheManager();
    }
}

