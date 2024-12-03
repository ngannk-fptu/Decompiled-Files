/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import org.aspectj.weaver.tools.cache.CacheBacking;
import org.aspectj.weaver.tools.cache.CacheFactory;
import org.aspectj.weaver.tools.cache.CacheKeyResolver;
import org.aspectj.weaver.tools.cache.DefaultCacheKeyResolver;
import org.aspectj.weaver.tools.cache.DefaultFileCacheBacking;

public class DefaultCacheFactory
implements CacheFactory {
    @Override
    public CacheKeyResolver createResolver() {
        return new DefaultCacheKeyResolver();
    }

    @Override
    public CacheBacking createBacking(String scope) {
        return DefaultFileCacheBacking.createBacking(scope);
    }
}

