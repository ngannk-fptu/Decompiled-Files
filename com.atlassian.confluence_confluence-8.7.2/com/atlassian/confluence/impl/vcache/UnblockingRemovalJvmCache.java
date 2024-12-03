/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.fugue.Pair
 *  com.atlassian.vcache.JvmCache
 *  com.atlassian.vcache.JvmCacheSettings
 *  com.atlassian.vcache.VCacheFactory
 *  com.google.common.collect.MapMaker
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.vcache;

import com.atlassian.annotations.Internal;
import com.atlassian.fugue.Pair;
import com.atlassian.vcache.JvmCache;
import com.atlassian.vcache.JvmCacheSettings;
import com.atlassian.vcache.VCacheFactory;
import com.google.common.collect.MapMaker;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public final class UnblockingRemovalJvmCache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(UnblockingRemovalJvmCache.class);
    private final JvmCache<K, Pair<Long, V>> delegate;
    private final ConcurrentMap<K, Lock> locks = new MapMaker().weakValues().makeMap();
    private final AtomicLong version = new AtomicLong(0L);

    public UnblockingRemovalJvmCache(VCacheFactory cacheFactory, String name, JvmCacheSettings settings) {
        this.delegate = cacheFactory.getJvmCache(name, settings);
    }

    public @NonNull V get(K key, Supplier<? extends V> supplier) {
        return (V)this.getIfValid(key).orElseGet(() -> {
            Lock lock = this.locks.computeIfAbsent(key, k -> new ReentrantLock());
            lock.lock();
            try {
                Object object = this.getIfValid(key).orElseGet(() -> this.lambda$get$1(key, (Supplier)supplier));
                return object;
            }
            finally {
                lock.unlock();
            }
        });
    }

    private Optional<V> getIfValid(K key) {
        return this.delegate.get(key).filter(pair -> this.version.get() == ((Long)pair.left()).longValue()).map(Pair::right);
    }

    public void removeAll() {
        this.version.incrementAndGet();
        this.delegate.removeAll();
    }

    private /* synthetic */ Object lambda$get$1(Object key, Supplier supplier) {
        log.debug("Generating value for key '{}' in cache '{}'", key, (Object)this.delegate.getName());
        long ver = this.version.get();
        Object value = supplier.get();
        this.delegate.put(key, (Object)Pair.pair((Object)ver, value));
        return value;
    }
}

