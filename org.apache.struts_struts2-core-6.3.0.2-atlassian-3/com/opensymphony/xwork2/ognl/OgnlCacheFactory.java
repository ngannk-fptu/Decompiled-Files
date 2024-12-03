/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.ognl.OgnlCache;

public interface OgnlCacheFactory<Key, Value> {
    public OgnlCache<Key, Value> buildOgnlCache();

    @Deprecated
    default public OgnlCache<Key, Value> buildOgnlCache(int evictionLimit, int initialCapacity, float loadFactor, boolean lruCache) {
        return this.buildOgnlCache(evictionLimit, initialCapacity, loadFactor, lruCache ? CacheType.LRU : this.getDefaultCacheType());
    }

    public OgnlCache<Key, Value> buildOgnlCache(int var1, int var2, float var3, CacheType var4);

    public int getCacheMaxSize();

    @Deprecated
    default public boolean getUseLRUCache() {
        return CacheType.LRU.equals((Object)this.getDefaultCacheType());
    }

    public CacheType getDefaultCacheType();

    public static enum CacheType {
        BASIC,
        LRU,
        WTLFU;

    }
}

