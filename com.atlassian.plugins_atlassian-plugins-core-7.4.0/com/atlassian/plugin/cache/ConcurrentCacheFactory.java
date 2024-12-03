/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.LoadingCache
 */
package com.atlassian.plugin.cache;

import com.google.common.cache.LoadingCache;

@Deprecated
public interface ConcurrentCacheFactory<K, V> {
    public LoadingCache<K, V> createCache();
}

