/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.interceptor;

import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;

public interface CacheErrorHandler {
    public void handleCacheGetError(RuntimeException var1, Cache var2, Object var3);

    public void handleCachePutError(RuntimeException var1, Cache var2, Object var3, @Nullable Object var4);

    public void handleCacheEvictError(RuntimeException var1, Cache var2, Object var3);

    public void handleCacheClearError(RuntimeException var1, Cache var2);
}

