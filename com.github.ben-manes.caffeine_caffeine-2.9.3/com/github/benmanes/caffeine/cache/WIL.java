/*
 * Decompiled with CFR 0.152.
 */
package com.github.benmanes.caffeine.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.WI;

class WIL<K, V>
extends WI<K, V> {
    final RemovalListener<K, V> removalListener;

    WIL(Caffeine<K, V> builder, CacheLoader<? super K, V> cacheLoader, boolean async) {
        super(builder, cacheLoader, async);
        this.removalListener = builder.getRemovalListener(async);
    }

    @Override
    public final RemovalListener<K, V> removalListener() {
        return this.removalListener;
    }

    @Override
    public final boolean hasRemovalListener() {
        return true;
    }
}

