/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache;

import java.util.Collection;
import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;

public interface CacheManager {
    @Nullable
    public Cache getCache(String var1);

    public Collection<String> getCacheNames();
}

