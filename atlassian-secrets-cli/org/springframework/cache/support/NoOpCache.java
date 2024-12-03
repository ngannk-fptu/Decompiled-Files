/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.support;

import java.util.concurrent.Callable;
import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class NoOpCache
implements Cache {
    private final String name;

    public NoOpCache(String name) {
        Assert.notNull((Object)name, "Cache name must not be null");
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    @Nullable
    public Cache.ValueWrapper get(Object key) {
        return null;
    }

    @Override
    @Nullable
    public <T> T get(Object key, @Nullable Class<T> type) {
        return null;
    }

    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        try {
            return valueLoader.call();
        }
        catch (Exception ex) {
            throw new Cache.ValueRetrievalException(key, valueLoader, ex);
        }
    }

    @Override
    public void put(Object key, @Nullable Object value) {
    }

    @Override
    @Nullable
    public Cache.ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        return null;
    }

    @Override
    public void evict(Object key) {
    }

    @Override
    public void clear() {
    }
}

