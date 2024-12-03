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
import javax.annotation.Nonnull;

@PublicApi
public interface CacheEntryListener<K, V> {
    public void onAdd(@Nonnull CacheEntryEvent<K, V> var1);

    public void onEvict(@Nonnull CacheEntryEvent<K, V> var1);

    public void onRemove(@Nonnull CacheEntryEvent<K, V> var1);

    public void onUpdate(@Nonnull CacheEntryEvent<K, V> var1);
}

