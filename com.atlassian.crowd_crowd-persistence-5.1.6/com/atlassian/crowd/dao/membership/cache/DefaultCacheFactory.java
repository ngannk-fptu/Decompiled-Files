/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettingsBuilder
 */
package com.atlassian.crowd.dao.membership.cache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.crowd.dao.membership.cache.CacheFactory;
import com.atlassian.crowd.dao.membership.cache.QueryTypeCacheKey;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DefaultCacheFactory
implements CacheFactory {
    private final com.atlassian.cache.CacheFactory cacheFactory;

    public DefaultCacheFactory(com.atlassian.cache.CacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
    }

    @Override
    public Cache<String, List<String>> createCache(QueryTypeCacheKey cacheKey, Duration cacheTtl, int maxEntries) {
        return this.cacheFactory.getCache("crowd.internal.groupMembership." + cacheKey.getDirectoryId() + "-" + (Object)((Object)cacheKey.getQueryType()), null, new CacheSettingsBuilder().expireAfterWrite(cacheTtl.getSeconds(), TimeUnit.SECONDS).maxEntries(maxEntries).remote().replicateAsynchronously().replicateViaInvalidation().build());
    }
}

