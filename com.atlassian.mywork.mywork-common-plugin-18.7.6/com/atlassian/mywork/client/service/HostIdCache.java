/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.fugue.Option
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.client.service;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.fugue.Option;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
class HostIdCache {
    private static final Logger log = LoggerFactory.getLogger(HostIdCache.class);
    private final Supplier<Cache<String, Option<String>>> cacheRef;

    public HostIdCache(CacheFactory cacheFactory, String cacheName) {
        this((Supplier<Cache<String, Option<String>>>)Lazy.supplier(() -> HostIdCache.createCache(cacheFactory, cacheName)));
    }

    @VisibleForTesting
    HostIdCache(Supplier<Cache<String, Option<String>>> cacheRef) {
        this.cacheRef = cacheRef;
    }

    private static Cache<String, Option<String>> createCache(CacheFactory cacheFactory, String cacheName) {
        return cacheFactory.getCache(cacheName, null, new CacheSettingsBuilder().remote().build());
    }

    public void setHost(String hostKey, Option<ApplicationId> host) {
        Option appId = host.map(ApplicationId::get);
        log.debug("Setting cached {} to {}", (Object)hostKey, (Object)appId);
        ((Cache)this.cacheRef.get()).put((Object)hostKey, (Object)appId);
    }

    @Nullable
    public Option<ApplicationId> getHost(String hostKey) {
        try {
            Option cachedValue = (Option)((Cache)this.cacheRef.get()).get((Object)hostKey);
            if (cachedValue != null) {
                return cachedValue.map(ApplicationId::new);
            }
            return null;
        }
        catch (RuntimeException ex) {
            log.warn("Failed to retrieve host from cache", (Throwable)ex);
            return null;
        }
    }

    public void clear() {
        try {
            ((Cache)this.cacheRef.get()).removeAll();
        }
        catch (RuntimeException ex) {
            log.error("Failed to clear cache", (Throwable)ex);
            throw ex;
        }
    }
}

