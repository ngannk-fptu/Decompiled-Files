/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cache.ConfluenceCache
 */
package com.atlassian.confluence.impl.cache.whitelist;

import com.atlassian.confluence.cache.ConfluenceCache;

public interface CacheOperationsWhitelistService {
    public <K, V> ConfluenceCache<K, V> wrap(ConfluenceCache<K, V> var1);
}

