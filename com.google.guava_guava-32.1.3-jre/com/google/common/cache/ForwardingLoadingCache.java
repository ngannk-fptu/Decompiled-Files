/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.cache.ElementTypesAreNonnullByDefault;
import com.google.common.cache.ForwardingCache;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.ExecutionException;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class ForwardingLoadingCache<K, V>
extends ForwardingCache<K, V>
implements LoadingCache<K, V> {
    protected ForwardingLoadingCache() {
    }

    @Override
    protected abstract LoadingCache<K, V> delegate();

    @Override
    @CanIgnoreReturnValue
    public V get(K key) throws ExecutionException {
        return this.delegate().get(key);
    }

    @Override
    @CanIgnoreReturnValue
    public V getUnchecked(K key) {
        return this.delegate().getUnchecked(key);
    }

    @Override
    @CanIgnoreReturnValue
    public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
        return this.delegate().getAll(keys);
    }

    @Override
    public V apply(K key) {
        return this.delegate().apply(key);
    }

    @Override
    public void refresh(K key) {
        this.delegate().refresh(key);
    }

    public static abstract class SimpleForwardingLoadingCache<K, V>
    extends ForwardingLoadingCache<K, V> {
        private final LoadingCache<K, V> delegate;

        protected SimpleForwardingLoadingCache(LoadingCache<K, V> delegate) {
            this.delegate = Preconditions.checkNotNull(delegate);
        }

        @Override
        protected final LoadingCache<K, V> delegate() {
            return this.delegate;
        }
    }
}

