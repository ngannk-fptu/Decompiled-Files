/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.cache;

import com.atlassian.user.impl.cache.Cache;
import java.util.List;

public interface CacheManager {
    public Cache getCache(String var1);

    public void flushCaches();

    public void setNonFlushableCaches(List var1);
}

