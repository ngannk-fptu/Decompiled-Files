/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.benmanes.caffeine.cache.Cache
 *  com.github.benmanes.caffeine.cache.LoadingCache
 */
package org.springframework.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.concurrent.Callable;
import java.util.function.Function;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class CaffeineCache
extends AbstractValueAdaptingCache {
    private final String name;
    private final Cache<Object, Object> cache;

    public CaffeineCache(String name, Cache<Object, Object> cache) {
        this(name, cache, true);
    }

    public CaffeineCache(String name, Cache<Object, Object> cache, boolean allowNullValues) {
        super(allowNullValues);
        Assert.notNull((Object)name, "Name must not be null");
        Assert.notNull(cache, "Cache must not be null");
        this.name = name;
        this.cache = cache;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    public final Cache<Object, Object> getNativeCache() {
        return this.cache;
    }

    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T)this.fromStoreValue(this.cache.get(key, (Function)new LoadFunction(valueLoader)));
    }

    @Override
    @Nullable
    protected Object lookup(Object key) {
        if (this.cache instanceof LoadingCache) {
            return ((LoadingCache)this.cache).get(key);
        }
        return this.cache.getIfPresent(key);
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        this.cache.put(key, this.toStoreValue(value));
    }

    @Override
    @Nullable
    public Cache.ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        PutIfAbsentFunction callable = new PutIfAbsentFunction(value);
        Object result = this.cache.get(key, (Function)callable);
        return callable.called ? null : this.toValueWrapper(result);
    }

    @Override
    public void evict(Object key) {
        this.cache.invalidate(key);
    }

    @Override
    public boolean evictIfPresent(Object key) {
        return this.cache.asMap().remove(key) != null;
    }

    @Override
    public void clear() {
        this.cache.invalidateAll();
    }

    @Override
    public boolean invalidate() {
        boolean notEmpty = !this.cache.asMap().isEmpty();
        this.cache.invalidateAll();
        return notEmpty;
    }

    private class LoadFunction
    implements Function<Object, Object> {
        private final Callable<?> valueLoader;

        public LoadFunction(Callable<?> valueLoader) {
            Assert.notNull(valueLoader, "Callable must not be null");
            this.valueLoader = valueLoader;
        }

        @Override
        public Object apply(Object key) {
            try {
                return CaffeineCache.this.toStoreValue(this.valueLoader.call());
            }
            catch (Exception ex) {
                throw new Cache.ValueRetrievalException(key, this.valueLoader, ex);
            }
        }
    }

    private class PutIfAbsentFunction
    implements Function<Object, Object> {
        @Nullable
        private final Object value;
        boolean called;

        public PutIfAbsentFunction(Object value) {
            this.value = value;
        }

        @Override
        public Object apply(Object key) {
            this.called = true;
            return CaffeineCache.this.toStoreValue(this.value);
        }
    }
}

