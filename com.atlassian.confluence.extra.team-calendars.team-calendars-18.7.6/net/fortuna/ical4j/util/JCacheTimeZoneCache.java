/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache
 *  javax.cache.CacheManager
 *  javax.cache.Caching
 *  javax.cache.configuration.Configuration
 *  javax.cache.configuration.MutableConfiguration
 */
package net.fortuna.ical4j.util;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.util.TimeZoneCache;

public class JCacheTimeZoneCache
implements TimeZoneCache {
    private final Cache<String, VTimeZone> jcacheCache;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public JCacheTimeZoneCache() {
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
        Cache cache = cacheManager.getCache("ical4j.timezones", String.class, VTimeZone.class);
        if (cache == null) {
            Class<JCacheTimeZoneCache> clazz = JCacheTimeZoneCache.class;
            // MONITORENTER : net.fortuna.ical4j.util.JCacheTimeZoneCache.class
            cache = cacheManager.getCache("ical4j.timezones", String.class, VTimeZone.class);
            if (cache == null) {
                MutableConfiguration cacheConfig = new MutableConfiguration();
                cacheConfig.setTypes(String.class, VTimeZone.class);
                cache = cacheManager.createCache("ical4j.timezones", (Configuration)cacheConfig);
            }
            // MONITOREXIT : clazz
        }
        this.jcacheCache = cache;
    }

    @Override
    public VTimeZone getTimezone(String id) {
        return (VTimeZone)this.jcacheCache.get((Object)id);
    }

    @Override
    public boolean putIfAbsent(String id, VTimeZone timeZone) {
        return this.jcacheCache.putIfAbsent((Object)id, (Object)timeZone);
    }

    @Override
    public boolean containsId(String id) {
        return this.jcacheCache.containsKey((Object)id);
    }

    @Override
    public void clear() {
        this.jcacheCache.clear();
    }
}

