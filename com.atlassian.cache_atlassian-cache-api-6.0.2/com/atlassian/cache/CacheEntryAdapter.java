/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.cache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.cache.CacheEntryEvent;
import com.atlassian.cache.CacheEntryListener;
import javax.annotation.Nonnull;

@PublicApi
public abstract class CacheEntryAdapter<K, V>
implements CacheEntryListener<K, V> {
    @Override
    public void onAdd(@Nonnull CacheEntryEvent<K, V> event) {
    }

    @Override
    public void onEvict(@Nonnull CacheEntryEvent<K, V> event) {
    }

    @Override
    public void onRemove(@Nonnull CacheEntryEvent<K, V> event) {
    }

    @Override
    public void onUpdate(@Nonnull CacheEntryEvent<K, V> event) {
    }
}

