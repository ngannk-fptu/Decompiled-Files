/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.vcache.JvmCache
 *  com.atlassian.vcache.internal.core.service.LocalCacheUtils
 *  com.atlassian.vcache.internal.core.service.VCacheLock
 */
package com.atlassian.vcache.internal.legacy;

import com.atlassian.cache.Cache;
import com.atlassian.vcache.JvmCache;
import com.atlassian.vcache.internal.core.service.LocalCacheUtils;
import com.atlassian.vcache.internal.core.service.VCacheLock;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

class LegacyJvmCache<K, V>
implements JvmCache<K, V> {
    private final Cache<K, V> delegate;
    private final VCacheLock globalLock;

    LegacyJvmCache(Cache<K, V> delegate, Duration lockTimeout) {
        this.delegate = Objects.requireNonNull(delegate);
        this.globalLock = new VCacheLock(delegate.getName(), lockTimeout);
    }

    public Set<K> getKeys() {
        return (Set)this.globalLock.withLock(() -> new HashSet(this.delegate.getKeys()));
    }

    public Optional<V> get(K key) {
        return (Optional)this.globalLock.withLock(() -> Optional.ofNullable(this.delegate.get(key)));
    }

    public V get(K key, Supplier<? extends V> supplier) {
        Optional<V> current = this.get(Objects.requireNonNull(key));
        return (V)current.orElseGet(() -> {
            Object candidateValue = Objects.requireNonNull(supplier.get());
            return this.globalLock.withLock(() -> this.delegate.get(key, () -> candidateValue));
        });
    }

    @SafeVarargs
    public final Map<K, V> getBulk(Function<Set<K>, Map<K, V>> factory, K ... keys) {
        return this.getBulk(factory, (Iterable<K>)Arrays.asList(keys));
    }

    public Map<K, V> getBulk(Function<Set<K>, Map<K, V>> factory, Iterable<K> keys) {
        return (Map)this.globalLock.withLock(() -> LocalCacheUtils.getBulk((Function)factory, (Iterable)keys, this::get, args -> this.putIfAbsent(args.key, args.value), (VCacheLock)this.globalLock));
    }

    public void put(K key, V value) {
        this.globalLock.withLock(() -> this.delegate.put(key, value));
    }

    public Optional<V> putIfAbsent(K key, V value) {
        return (Optional)this.globalLock.withLock(() -> Optional.ofNullable(this.delegate.putIfAbsent(key, value)));
    }

    public boolean replaceIf(K key, V currentValue, V newValue) {
        return (Boolean)this.globalLock.withLock(() -> this.delegate.replace(key, currentValue, newValue));
    }

    public boolean removeIf(K key, V value) {
        return (Boolean)this.globalLock.withLock(() -> this.delegate.remove(key, value));
    }

    public void remove(K key) {
        this.globalLock.withLock(() -> this.delegate.remove(key));
    }

    public void removeAll() {
        this.globalLock.withLock(() -> this.delegate.removeAll());
    }

    public String getName() {
        return this.delegate.getName();
    }
}

