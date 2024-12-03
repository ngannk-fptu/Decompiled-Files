/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.interceptor;

import java.util.Collection;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.AbstractCacheResolver;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.lang.Nullable;

public class SimpleCacheResolver
extends AbstractCacheResolver {
    public SimpleCacheResolver() {
    }

    public SimpleCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        return context.getOperation().getCacheNames();
    }

    @Nullable
    static SimpleCacheResolver of(@Nullable CacheManager cacheManager) {
        return cacheManager != null ? new SimpleCacheResolver(cacheManager) : null;
    }
}

