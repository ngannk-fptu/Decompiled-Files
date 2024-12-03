/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.ContextHolder;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component(value="cacheContextHolder")
public class CacheContextHolder
implements ContextHolder {
    private static final String CACHE_NAME = "com.atlassian.confluence.plugin.copyspace.context";
    private static final int CACHE_SIZE = Integer.getInteger("confluence.plugin.space.copy.context.cache.size", 5000);
    private static final int EXPIRES_AFTER = Integer.getInteger("confluence.plugin.space.copy.context.cache.expires.after.hours", 6);
    private final Cache<String, CopySpaceContext> requestIdToContextCache;

    public CacheContextHolder(@ComponentImport CacheManager cacheManager) {
        this.requestIdToContextCache = this.createCache((CacheFactory)cacheManager);
    }

    @Override
    public void put(String requestId, CopySpaceContext context) {
        this.requestIdToContextCache.put((Object)requestId, (Object)context);
    }

    @Override
    public CopySpaceContext getContext(String requestId) {
        return (CopySpaceContext)this.requestIdToContextCache.get((Object)requestId);
    }

    private Cache<String, CopySpaceContext> createCache(CacheFactory cacheFactory) {
        return cacheFactory.getCache(CACHE_NAME, null, new CacheSettingsBuilder().expireAfterWrite((long)EXPIRES_AFTER, TimeUnit.HOURS).expireAfterAccess((long)EXPIRES_AFTER, TimeUnit.HOURS).local().maxEntries(CACHE_SIZE).build());
    }
}

