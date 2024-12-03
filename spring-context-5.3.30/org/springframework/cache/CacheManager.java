/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
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

