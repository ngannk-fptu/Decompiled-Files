/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.Supplier
 *  com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictiveness
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.whitelist.core.applinks;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.Supplier;
import com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictiveness;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationLinkRestrictionsCache {
    private static final String CACHE_NAME = ApplicationLinkRestrictionsCache.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(ApplicationLinkRestrictionsCache.class);
    private final Cache<String, ApplicationLinkRestrictiveness> cache;

    public ApplicationLinkRestrictionsCache(CacheManager cacheFactory) {
        this.cache = cacheFactory.getCache(CACHE_NAME, null, new CacheSettingsBuilder().remote().expireAfterWrite(1L, TimeUnit.HOURS).build());
    }

    public ApplicationLinkRestrictiveness getWithDefault(String key, Supplier<ApplicationLinkRestrictiveness> defaultSupplier) {
        try {
            return (ApplicationLinkRestrictiveness)this.cache.get((Object)key, defaultSupplier);
        }
        catch (RuntimeException exception) {
            logger.warn("Failed to read entry from cache '" + CACHE_NAME + "': {}", (Object)exception.getMessage());
            return (ApplicationLinkRestrictiveness)defaultSupplier.get();
        }
    }

    public void clear() {
        try {
            this.cache.removeAll();
        }
        catch (RuntimeException exception) {
            logger.error("Failed to remove all entries from cache '" + CACHE_NAME + "': {}", (Object)exception.getMessage());
        }
    }
}

