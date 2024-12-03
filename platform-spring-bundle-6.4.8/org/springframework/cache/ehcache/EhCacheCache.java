/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Element
 *  net.sf.ehcache.Status
 */
package org.springframework.cache.ehcache;

import java.util.concurrent.Callable;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class EhCacheCache
implements Cache {
    private final Ehcache cache;

    public EhCacheCache(Ehcache ehcache) {
        Assert.notNull((Object)ehcache, "Ehcache must not be null");
        Status status = ehcache.getStatus();
        if (!Status.STATUS_ALIVE.equals(status)) {
            throw new IllegalArgumentException("An 'alive' Ehcache is required - current cache is " + status.toString());
        }
        this.cache = ehcache;
    }

    @Override
    public final String getName() {
        return this.cache.getName();
    }

    public final Ehcache getNativeCache() {
        return this.cache;
    }

    @Override
    @Nullable
    public Cache.ValueWrapper get(Object key) {
        Element element = this.lookup(key);
        return this.toValueWrapper(element);
    }

    @Override
    @Nullable
    public <T> T get(Object key, @Nullable Class<T> type) {
        Object value;
        Element element = this.cache.get(key);
        Object object = value = element != null ? element.getObjectValue() : null;
        if (value != null && type != null && !type.isInstance(value)) {
            throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
        }
        return (T)value;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        Element element = this.lookup(key);
        if (element != null) {
            return (T)element.getObjectValue();
        }
        this.cache.acquireWriteLockOnKey(key);
        try {
            element = this.lookup(key);
            if (element != null) {
                Object object = element.getObjectValue();
                return (T)object;
            }
            T t = this.loadValue(key, valueLoader);
            return t;
        }
        finally {
            this.cache.releaseWriteLockOnKey(key);
        }
    }

    private <T> T loadValue(Object key, Callable<T> valueLoader) {
        T value;
        try {
            value = valueLoader.call();
        }
        catch (Throwable ex) {
            throw new Cache.ValueRetrievalException(key, valueLoader, ex);
        }
        this.put(key, value);
        return value;
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        this.cache.put(new Element(key, value));
    }

    @Override
    @Nullable
    public Cache.ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        Element existingElement = this.cache.putIfAbsent(new Element(key, value));
        return this.toValueWrapper(existingElement);
    }

    @Override
    public void evict(Object key) {
        this.cache.remove(key);
    }

    @Override
    public boolean evictIfPresent(Object key) {
        return this.cache.remove(key);
    }

    @Override
    public void clear() {
        this.cache.removeAll();
    }

    @Override
    public boolean invalidate() {
        boolean notEmpty = this.cache.getSize() > 0;
        this.cache.removeAll();
        return notEmpty;
    }

    @Nullable
    private Element lookup(Object key) {
        return this.cache.get(key);
    }

    @Nullable
    private Cache.ValueWrapper toValueWrapper(@Nullable Element element) {
        return element != null ? new SimpleValueWrapper(element.getObjectValue()) : null;
    }
}

