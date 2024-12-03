/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 */
package com.atlassian.crowd.dao.membership.cache;

import com.atlassian.cache.Cache;
import com.atlassian.crowd.dao.membership.cache.QueryTypeCacheKey;
import java.time.Duration;
import java.util.List;

public interface CacheFactory {
    public Cache<String, List<String>> createCache(QueryTypeCacheKey var1, Duration var2, int var3);
}

