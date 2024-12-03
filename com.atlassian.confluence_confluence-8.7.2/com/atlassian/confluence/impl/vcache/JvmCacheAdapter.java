/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.cache.Supplier
 *  com.atlassian.vcache.JvmCache
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.vcache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.Supplier;
import com.atlassian.vcache.JvmCache;
import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Deprecated
public final class JvmCacheAdapter<K, V>
implements Cache<K, V> {
    private final JvmCache<K, V> delegate;

    public JvmCacheAdapter(JvmCache<K, V> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Nonnull
    public String getName() {
        return this.delegate.getName();
    }

    public boolean containsKey(@Nonnull K key) {
        return this.delegate.getKeys().contains(key);
    }

    @Nonnull
    public Collection<K> getKeys() {
        return this.delegate.getKeys();
    }

    @Nullable
    public V get(@Nonnull K key) {
        return this.delegate.get(key).orElse(null);
    }

    @Nonnull
    public V get(@Nonnull K key, @Nonnull Supplier<? extends V> valueSupplier) {
        return (V)this.delegate.get(key, valueSupplier);
    }

    public void put(@Nonnull K key, @Nonnull V value) {
        this.delegate.put(key, value);
    }

    @Nullable
    public V putIfAbsent(@Nonnull K key, @Nonnull V value) {
        return this.delegate.putIfAbsent(key, value).orElse(null);
    }

    public void remove(@Nonnull K key) {
        this.delegate.remove(key);
    }

    public boolean remove(@Nonnull K key, @Nonnull V value) {
        return this.delegate.removeIf(key, value);
    }

    public void removeAll() {
        this.delegate.removeAll();
    }

    public boolean replace(@Nonnull K key, @Nonnull V oldValue, @Nonnull V newValue) {
        return this.delegate.replaceIf(key, oldValue, newValue);
    }

    public void addListener(@Nonnull CacheEntryListener<K, V> listener, boolean includeValues) {
        throw new UnsupportedOperationException();
    }

    public void removeListener(@Nonnull CacheEntryListener<K, V> listener) {
        throw new UnsupportedOperationException();
    }
}

