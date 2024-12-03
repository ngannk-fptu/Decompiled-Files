/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.cache.ICache;

public interface ICacheManager {
    public <K, V> ICache<K, V> getCache(String var1);
}

