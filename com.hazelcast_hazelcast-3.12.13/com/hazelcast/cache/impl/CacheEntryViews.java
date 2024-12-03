/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.cache.impl.merge.entry.DefaultCacheEntryView;
import com.hazelcast.cache.impl.merge.entry.LazyCacheEntryView;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.nio.serialization.Data;

public final class CacheEntryViews {
    private CacheEntryViews() {
    }

    public static CacheEntryView<Data, Data> createDefaultEntryView(Data key, Data value, Data expiryPolicy, CacheRecord<Object, Data> record) {
        DefaultCacheEntryView entryView = new DefaultCacheEntryView(key, value, record.getCreationTime(), record.getExpirationTime(), record.getLastAccessTime(), record.getAccessHit(), expiryPolicy);
        return entryView;
    }

    public static CacheEntryView<Data, Data> createEntryView(Data key, Data expiryPolicy, CacheRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Empty record");
        }
        return CacheEntryViews.createDefaultEntryView(key, (Data)record.getValue(), expiryPolicy, record);
    }

    public static CacheEntryView<Data, Data> createLazyEntryView(Data key, Data value, Data expiryPolicy, CacheRecord record) {
        LazyCacheEntryView<Data, Data> entryView = new LazyCacheEntryView<Data, Data>(key, value, record.getCreationTime(), record.getExpirationTime(), record.getLastAccessTime(), record.getAccessHit(), expiryPolicy);
        return entryView;
    }

    public static CacheEntryView<Data, Data> createEntryView(Data key, Data value, Data expiryPolicy, CacheRecord record, CacheEntryViewType cacheEntryViewType) {
        if (cacheEntryViewType == null) {
            throw new IllegalArgumentException("Empty cache entry view type");
        }
        switch (cacheEntryViewType) {
            case DEFAULT: {
                return CacheEntryViews.createDefaultEntryView(key, value, expiryPolicy, record);
            }
            case LAZY: {
                return CacheEntryViews.createLazyEntryView(key, value, expiryPolicy, record);
            }
        }
        throw new IllegalArgumentException("Invalid cache entry view type: " + (Object)((Object)cacheEntryViewType));
    }

    public static enum CacheEntryViewType {
        DEFAULT,
        LAZY;

    }
}

