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
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PublicApi
public interface CacheFactory {
    @Nonnull
    public <V> CachedReference<V> getCachedReference(@Nonnull String var1, @Nonnull Supplier<V> var2);

    @Nonnull
    public <V> CachedReference<V> getCachedReference(@Nonnull String var1, @Nonnull Supplier<V> var2, @Nonnull CacheSettings var3);

    @Nonnull
    public <V> CachedReference<V> getCachedReference(@Nonnull Class<?> var1, @Nonnull String var2, @Nonnull Supplier<V> var3);

    @Nonnull
    public <V> CachedReference<V> getCachedReference(@Nonnull Class<?> var1, @Nonnull String var2, @Nonnull Supplier<V> var3, @Nonnull CacheSettings var4);

    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull String var1);

    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull Class<?> var1, @Nonnull String var2);

    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull String var1, @Nullable CacheLoader<K, V> var2);

    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull String var1, @Nullable CacheLoader<K, V> var2, @Nonnull CacheSettings var3);

    @Deprecated
    @Nonnull
    public <K, V> Cache<K, V> getCache(@Nonnull String var1, @Nonnull Class<K> var2, @Nonnull Class<V> var3);
}

