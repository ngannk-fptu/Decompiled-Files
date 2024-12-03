/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.AbstractCacheResolver;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.lang.Nullable;

public class NamedCacheResolver
extends AbstractCacheResolver {
    @Nullable
    private Collection<String> cacheNames;

    public NamedCacheResolver() {
    }

    public NamedCacheResolver(CacheManager cacheManager, String ... cacheNames) {
        super(cacheManager);
        this.cacheNames = new ArrayList<String>(Arrays.asList(cacheNames));
    }

    public void setCacheNames(Collection<String> cacheNames) {
        this.cacheNames = cacheNames;
    }

    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        return this.cacheNames;
    }
}

