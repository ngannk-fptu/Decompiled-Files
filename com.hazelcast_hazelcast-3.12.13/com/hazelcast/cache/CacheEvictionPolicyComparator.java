/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache;

import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.internal.eviction.EvictionPolicyComparator;

public abstract class CacheEvictionPolicyComparator<K, V>
extends EvictionPolicyComparator<K, V, CacheEntryView<K, V>> {
    @Override
    public abstract int compare(CacheEntryView<K, V> var1, CacheEntryView<K, V> var2);
}

