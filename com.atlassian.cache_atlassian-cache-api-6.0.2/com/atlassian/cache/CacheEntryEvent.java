/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.cache;

import com.atlassian.annotations.PublicApi;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PublicApi
public interface CacheEntryEvent<K, V> {
    @Nonnull
    public K getKey();

    @Nullable
    public V getValue();

    @Nullable
    public V getOldValue();
}

