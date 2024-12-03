/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import org.aspectj.weaver.tools.cache.CacheBacking;
import org.aspectj.weaver.tools.cache.CacheKeyResolver;

public interface CacheFactory {
    public CacheKeyResolver createResolver();

    public CacheBacking createBacking(String var1);
}

