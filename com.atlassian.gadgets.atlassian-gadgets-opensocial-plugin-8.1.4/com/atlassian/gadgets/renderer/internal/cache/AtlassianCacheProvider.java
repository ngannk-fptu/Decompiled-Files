/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gadgets.renderer.internal.cache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.gadgets.renderer.internal.cache.AtlassianCache;
import com.atlassian.gadgets.renderer.internal.cache.ClearableCacheProvider;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtlassianCacheProvider
implements ClearableCacheProvider {
    private static final Logger log = LoggerFactory.getLogger(AtlassianCacheProvider.class);
    private static final String ANONYMOUS_CACHE_NAME_SUFFIX = "Anonymous";
    private static final String CACHE_NAME_PREFIX = AtlassianCacheProvider.class.getPackage().getName() + '.';
    private final CacheFactory cacheFactory;
    private final Set<String> cacheNames = Sets.newSetFromMap(new ConcurrentHashMap());

    public AtlassianCacheProvider(CacheFactory cacheFactory) {
        this.cacheFactory = Objects.requireNonNull(cacheFactory);
    }

    public <K, V> AtlassianCache<K, V> createCache(String name) {
        String cacheName = AtlassianCacheProvider.getCacheName(name);
        Cache cache = this.cacheFactory.getCache(cacheName);
        this.cacheNames.add(cacheName);
        return new AtlassianCache(cache);
    }

    private static String getCacheName(String name) {
        String suffix = StringUtils.defaultString((String)name, (String)ANONYMOUS_CACHE_NAME_SUFFIX);
        return CACHE_NAME_PREFIX + suffix;
    }

    @Override
    public void clear() {
        ArrayList<String> cacheNamesCopy = new ArrayList<String>(this.cacheNames);
        this.cacheNames.clear();
        log.debug("Clearing caches {}", cacheNamesCopy);
        for (String cacheName : cacheNamesCopy) {
            this.cacheFactory.getCache(cacheName).removeAll();
        }
    }
}

