/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface LoadingCache<K, V>
extends Cache<K, V>,
Function<K, V> {
    @CanIgnoreReturnValue
    public V get(K var1) throws ExecutionException;

    @CanIgnoreReturnValue
    public V getUnchecked(K var1);

    @CanIgnoreReturnValue
    public ImmutableMap<K, V> getAll(Iterable<? extends K> var1) throws ExecutionException;

    @Override
    @Deprecated
    public V apply(K var1);

    public void refresh(K var1);

    @Override
    public ConcurrentMap<K, V> asMap();
}

