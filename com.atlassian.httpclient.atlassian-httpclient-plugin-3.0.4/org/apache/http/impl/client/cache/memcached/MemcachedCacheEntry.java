/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache.memcached;

import org.apache.http.client.cache.HttpCacheEntry;

public interface MemcachedCacheEntry {
    public byte[] toByteArray();

    public String getStorageKey();

    public HttpCacheEntry getHttpCacheEntry();

    public void set(byte[] var1);
}

