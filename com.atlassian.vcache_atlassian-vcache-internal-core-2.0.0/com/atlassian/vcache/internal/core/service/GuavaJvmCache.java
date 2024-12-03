/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.JvmCache
 *  com.atlassian.vcache.JvmCacheSettings
 *  com.atlassian.vcache.VCacheException
 *  com.atlassian.vcache.internal.NameValidator
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.util.concurrent.UncheckedExecutionException
 */
package com.atlassian.vcache.internal.core.service;

import com.atlassian.vcache.JvmCache;
import com.atlassian.vcache.JvmCacheSettings;
import com.atlassian.vcache.VCacheException;
import com.atlassian.vcache.internal.NameValidator;
import com.atlassian.vcache.internal.core.service.LocalCacheUtils;
import com.atlassian.vcache.internal.core.service.VCacheLock;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class GuavaJvmCache<K, V>
implements JvmCache<K, V> {
    private final String name;
    private final VCacheLock globalLock;
    private final Cache<K, V> delegate;

    public GuavaJvmCache(String name, JvmCacheSettings settings, Duration lockTimeout) {
        this.name = NameValidator.requireValidCacheName((String)name);
        this.globalLock = new VCacheLock(name, lockTimeout);
        this.delegate = CacheBuilder.newBuilder().maximumSize((long)((Integer)settings.getMaxEntries().get()).intValue()).expireAfterWrite(((Duration)settings.getDefaultTtl().get()).toMillis(), TimeUnit.MILLISECONDS).build();
    }

    public Set<K> getKeys() {
        return this.globalLock.withLock(() -> new HashSet(this.delegate.asMap().keySet()));
    }

    public Optional<V> get(K key) {
        return this.globalLock.withLock(() -> Optional.ofNullable(this.delegate.getIfPresent(key)));
    }

    public V get(K key, Supplier<? extends V> supplier) {
        Optional<V> current = this.get(Objects.requireNonNull(key));
        return (V)current.orElseGet(() -> {
            Object candidateValue = Objects.requireNonNull(supplier.get());
            return this.globalLock.withLock(() -> {
                try {
                    return this.delegate.get(key, () -> candidateValue);
                }
                catch (UncheckedExecutionException | ExecutionException e) {
                    throw new VCacheException("Internal Guava failure", e);
                }
            });
        });
    }

    @SafeVarargs
    public final Map<K, V> getBulk(Function<Set<K>, Map<K, V>> factory, K ... keys) {
        return this.getBulk(factory, (Iterable<K>)Arrays.asList(keys));
    }

    public Map<K, V> getBulk(Function<Set<K>, Map<K, V>> factory, Iterable<K> keys) {
        return this.globalLock.withLock(() -> LocalCacheUtils.getBulk(factory, keys, this::get, args -> this.putIfAbsent(args.key, args.value), this.globalLock));
    }

    public void put(K key, V value) {
        this.globalLock.withLock(() -> this.delegate.put(Objects.requireNonNull(key), Objects.requireNonNull(value)));
    }

    public Optional<V> putIfAbsent(K key, V value) {
        return this.globalLock.withLock(() -> Optional.ofNullable(this.delegate.asMap().putIfAbsent(Objects.requireNonNull(key), Objects.requireNonNull(value))));
    }

    public boolean replaceIf(K key, V currentValue, V newValue) {
        return this.globalLock.withLock(() -> this.delegate.asMap().replace(Objects.requireNonNull(key), Objects.requireNonNull(currentValue), Objects.requireNonNull(newValue)));
    }

    public void remove(K key) {
        this.globalLock.withLock(() -> this.delegate.invalidate(key));
    }

    public void removeAll() {
        this.globalLock.withLock(() -> this.delegate.invalidateAll());
    }

    public boolean removeIf(K key, V value) {
        return this.globalLock.withLock(() -> this.delegate.asMap().remove(key, value));
    }

    public String getName() {
        return this.name;
    }
}

