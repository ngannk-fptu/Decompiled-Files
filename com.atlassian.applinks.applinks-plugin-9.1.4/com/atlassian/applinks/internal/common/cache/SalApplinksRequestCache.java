/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.common.cache;

import com.atlassian.applinks.internal.common.cache.ApplinksRequestCache;
import com.atlassian.sal.api.web.context.HttpContext;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class SalApplinksRequestCache
implements ApplinksRequestCache {
    public static final String CACHE_KEY = "applinks.internal.cache.SalApplinksRequestCache";
    private final HttpContext httpContext;

    @Autowired
    public SalApplinksRequestCache(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    @Override
    @Nonnull
    public <K, V> ApplinksRequestCache.Cache<K, V> getCache(@Nonnull String cacheName, @Nonnull Class<K> keyType, @Nonnull Class<V> valueType) {
        Objects.requireNonNull(cacheName, "cacheName");
        Objects.requireNonNull(keyType, "keyType");
        Objects.requireNonNull(valueType, "valueType");
        return new CacheImpl(this.getMapFromRequest(cacheName, keyType, valueType));
    }

    @Nonnull
    private <K, V> Map<K, V> getMapFromRequest(@Nonnull String cacheName, @Nonnull Class<K> keyType, @Nonnull Class<V> valueType) {
        Map<String, Object> allCaches = this.getCachesMap();
        Map cache = (Map)allCaches.get(cacheName);
        if (cache == null) {
            cache = Maps.newHashMap();
            allCaches.put(cacheName, cache);
        }
        return cache;
    }

    private Map<String, Object> getCachesMap() {
        HttpServletRequest request = this.httpContext.getRequest();
        if (request == null) {
            return Maps.newHashMap();
        }
        Map caches = (Map)request.getAttribute(CACHE_KEY);
        if (caches == null) {
            caches = Maps.newHashMap();
            request.setAttribute(CACHE_KEY, (Object)caches);
        }
        return caches;
    }

    private static final class CacheImpl<K, V>
    implements ApplinksRequestCache.Cache<K, V> {
        private final Map<K, V> map;

        private CacheImpl(Map<K, V> map) {
            this.map = map;
        }

        @Override
        public void put(@Nonnull K key, @Nonnull V value) {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(value, "value");
            this.map.put(key, value);
        }

        @Override
        @Nullable
        public V get(@Nonnull K key) {
            Objects.requireNonNull(key, "key");
            return this.map.get(key);
        }
    }
}

