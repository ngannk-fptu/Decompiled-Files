/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheException
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.Supplier
 *  com.google.common.base.Throwables
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheException;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.Supplier;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.google.common.base.Throwables;
import io.atlassian.fugue.Option;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReadThroughAtlassianCache<K, V>
implements ReadThroughCache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(ReadThroughAtlassianCache.class);
    private final Cache<K, V> delegate;

    public static <K, V> ReadThroughAtlassianCache<K, V> create(CacheFactory atlassianCacheFactory, CoreCache cacheName) {
        return new ReadThroughAtlassianCache(cacheName.getCache(atlassianCacheFactory));
    }

    public ReadThroughAtlassianCache(Cache<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Map<K, V> getBulk(Set<K> keys, Function<Set<K>, Map<K, V>> valuesLoader) {
        return this.delegate.getBulk(keys, valuesLoader);
    }

    @Override
    public void removeAll() {
        this.delegate.removeAll();
    }

    @Override
    public void remove(K key) {
        this.delegate.remove(key);
    }

    @Override
    public V get(K key, java.util.function.Supplier<V> valueSupplier, Predicate<V> valueTester) {
        Supplier filteringSupplier = () -> {
            Object value = valueSupplier.get();
            if (value != null && valueTester.test(value)) {
                return value;
            }
            throw new UncacheableValueException(value);
        };
        try {
            Option result = Option.some((Object)this.delegate.get(key, filteringSupplier)).filter(valueTester);
            if (result.isDefined()) {
                return (V)result.get();
            }
            this.remove(key);
            return this.get(key, valueSupplier, valueTester);
        }
        catch (RuntimeException ex) {
            return (V)ReadThroughAtlassianCache.findInCausalChain(ex, UncacheableValueException.class).map(uvex -> uvex.value).getOrThrow(() -> {
                if (ex instanceof CacheException && ex.getCause() instanceof RuntimeException) {
                    return (RuntimeException)ex.getCause();
                }
                return ex;
            });
        }
    }

    private static <T extends Exception> Option<T> findInCausalChain(RuntimeException ex, Class<T> targetType) {
        return Option.fromOptional(ReadThroughAtlassianCache.getCausalChain(ex).filter(targetType::isInstance).map(targetType::cast).findFirst());
    }

    private static Stream<Throwable> getCausalChain(RuntimeException ex) {
        try {
            return Throwables.getCausalChain((Throwable)ex).stream();
        }
        catch (IllegalArgumentException iaex) {
            log.warn("Failed to decode exception thrown by cache value loader", (Throwable)iaex);
            return Stream.of(ex);
        }
    }

    private static class UncacheableValueException
    extends RuntimeException {
        final Object value;

        UncacheableValueException(@Nullable Object value) {
            this.value = value;
        }
    }
}

