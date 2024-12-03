/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.plugins.authentication.impl.web.AtlassianCacheSessionDataCache;
import com.atlassian.plugins.authentication.impl.web.GuavaSessionDataCache;
import com.atlassian.plugins.authentication.impl.web.SessionData;
import com.atlassian.plugins.authentication.impl.web.SessionDataCacheConfiguration;
import com.atlassian.plugins.authentication.impl.web.SessionDataCacheFactory;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSessionDataCacheFactory
implements SessionDataCacheFactory {
    private static final String SESSION_DATA_CACHE_NAME = "com.atlassian.plugins.authentication.impl.web.saml.SessionDataService-sessiondata";

    protected GuavaSessionDataCache getGuavaSessionDataCache(SessionDataCacheConfiguration configuration) {
        return new GuavaSessionDataCache((com.google.common.cache.Cache<String, SessionData>)CacheBuilder.newBuilder().expireAfterWrite(configuration.getCacheEntryLifetimeInSeconds(), TimeUnit.SECONDS).build());
    }

    protected AtlassianCacheSessionDataCache getAtlassianCacheSessionDataCache(CacheManager cacheManager, SessionDataCacheConfiguration parameters) {
        return new AtlassianCacheSessionDataCache((Cache<String, SessionData>)cacheManager.getCache(SESSION_DATA_CACHE_NAME, null, new CacheSettingsBuilder().remote().replicateAsynchronously().replicateViaCopy().expireAfterWrite(parameters.getCacheEntryLifetimeInSeconds(), TimeUnit.SECONDS).build()));
    }
}

