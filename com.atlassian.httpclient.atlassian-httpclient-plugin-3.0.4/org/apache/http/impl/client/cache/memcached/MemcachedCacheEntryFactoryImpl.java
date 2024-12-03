/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache.memcached;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.impl.client.cache.memcached.MemcachedCacheEntry;
import org.apache.http.impl.client.cache.memcached.MemcachedCacheEntryFactory;
import org.apache.http.impl.client.cache.memcached.MemcachedCacheEntryImpl;

public class MemcachedCacheEntryFactoryImpl
implements MemcachedCacheEntryFactory {
    @Override
    public MemcachedCacheEntry getMemcachedCacheEntry(String key, HttpCacheEntry entry) {
        return new MemcachedCacheEntryImpl(key, entry);
    }

    @Override
    public MemcachedCacheEntry getUnsetCacheEntry() {
        return new MemcachedCacheEntryImpl(null, null);
    }
}

