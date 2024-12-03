/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache.memcached;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.impl.client.cache.memcached.MemcachedCacheEntry;

public interface MemcachedCacheEntryFactory {
    public MemcachedCacheEntry getMemcachedCacheEntry(String var1, HttpCacheEntry var2);

    public MemcachedCacheEntry getUnsetCacheEntry();
}

