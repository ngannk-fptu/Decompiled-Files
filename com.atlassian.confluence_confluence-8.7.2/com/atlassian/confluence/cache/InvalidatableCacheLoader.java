/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cache.CacheLoader
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.CacheLoader;
import com.google.common.base.Preconditions;
import java.util.concurrent.atomic.AtomicLong;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public final class InvalidatableCacheLoader<K, V>
implements CacheLoader<K, V> {
    private static final Logger log = LoggerFactory.getLogger(InvalidatableCacheLoader.class);
    private final CacheLoader<K, V> delegate;
    private final AtomicLong version = new AtomicLong();

    private InvalidatableCacheLoader(CacheLoader<K, V> delegate) {
        this.delegate = (CacheLoader)Preconditions.checkNotNull(delegate);
    }

    public static <K, V> InvalidatableCacheLoader<K, V> createLocal(CacheLoader<K, V> delegate) {
        return new InvalidatableCacheLoader<K, V>(delegate);
    }

    public @NonNull V load(K key) {
        Object value;
        long version;
        do {
            version = this.version.get();
            value = this.delegate.load(key);
        } while (this.isInvalid(version, key));
        return (V)value;
    }

    private boolean isInvalid(long version, K key) {
        if (version == this.version.get()) {
            return false;
        }
        log.warn("Value for key '{}' was invalidated while it was being loaded. Reloading the value.", key);
        return true;
    }

    public void invalidateAll() {
        log.debug("Invalidating all values");
        this.version.incrementAndGet();
    }
}

