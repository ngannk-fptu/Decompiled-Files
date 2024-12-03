/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.serializer.support.SerializationDelegate
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.cache.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ConcurrentMapCache
extends AbstractValueAdaptingCache {
    private final String name;
    private final ConcurrentMap<Object, Object> store;
    @Nullable
    private final SerializationDelegate serialization;

    public ConcurrentMapCache(String name) {
        this(name, new ConcurrentHashMap<Object, Object>(256), true);
    }

    public ConcurrentMapCache(String name, boolean allowNullValues) {
        this(name, new ConcurrentHashMap<Object, Object>(256), allowNullValues);
    }

    public ConcurrentMapCache(String name, ConcurrentMap<Object, Object> store, boolean allowNullValues) {
        this(name, store, allowNullValues, null);
    }

    protected ConcurrentMapCache(String name, ConcurrentMap<Object, Object> store, boolean allowNullValues, @Nullable SerializationDelegate serialization) {
        super(allowNullValues);
        Assert.notNull((Object)name, (String)"Name must not be null");
        Assert.notNull(store, (String)"Store must not be null");
        this.name = name;
        this.store = store;
        this.serialization = serialization;
    }

    public final boolean isStoreByValue() {
        return this.serialization != null;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final ConcurrentMap<Object, Object> getNativeCache() {
        return this.store;
    }

    @Override
    @Nullable
    protected Object lookup(Object key) {
        return this.store.get(key);
    }

    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T)this.fromStoreValue(this.store.computeIfAbsent(key, k -> {
            try {
                return this.toStoreValue(valueLoader.call());
            }
            catch (Throwable ex) {
                throw new Cache.ValueRetrievalException(key, valueLoader, ex);
            }
        }));
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        this.store.put(key, this.toStoreValue(value));
    }

    @Override
    @Nullable
    public Cache.ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        Object existing = this.store.putIfAbsent(key, this.toStoreValue(value));
        return this.toValueWrapper(existing);
    }

    @Override
    public void evict(Object key) {
        this.store.remove(key);
    }

    @Override
    public boolean evictIfPresent(Object key) {
        return this.store.remove(key) != null;
    }

    @Override
    public void clear() {
        this.store.clear();
    }

    @Override
    public boolean invalidate() {
        boolean notEmpty = !this.store.isEmpty();
        this.store.clear();
        return notEmpty;
    }

    @Override
    protected Object toStoreValue(@Nullable Object userValue) {
        Object storeValue = super.toStoreValue(userValue);
        if (this.serialization != null) {
            try {
                return this.serialization.serializeToByteArray(storeValue);
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("Failed to serialize cache value '" + userValue + "'. Does it implement Serializable?", ex);
            }
        }
        return storeValue;
    }

    @Override
    protected Object fromStoreValue(@Nullable Object storeValue) {
        if (storeValue != null && this.serialization != null) {
            try {
                return super.fromStoreValue(this.serialization.deserializeFromByteArray((byte[])storeValue));
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("Failed to deserialize cache value '" + storeValue + "'", ex);
            }
        }
        return super.fromStoreValue(storeValue);
    }
}

