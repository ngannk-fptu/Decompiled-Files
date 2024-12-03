/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache
 *  javax.cache.processor.EntryProcessor
 *  javax.cache.processor.EntryProcessorException
 *  javax.cache.processor.MutableEntry
 *  org.springframework.cache.Cache$ValueRetrievalException
 *  org.springframework.cache.Cache$ValueWrapper
 *  org.springframework.cache.support.AbstractValueAdaptingCache
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.cache.jcache;

import java.util.concurrent.Callable;
import javax.cache.Cache;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class JCacheCache
extends AbstractValueAdaptingCache {
    private final Cache<Object, Object> cache;

    public JCacheCache(Cache<Object, Object> jcache) {
        this(jcache, true);
    }

    public JCacheCache(Cache<Object, Object> jcache, boolean allowNullValues) {
        super(allowNullValues);
        Assert.notNull(jcache, (String)"Cache must not be null");
        this.cache = jcache;
    }

    public final String getName() {
        return this.cache.getName();
    }

    public final Cache<Object, Object> getNativeCache() {
        return this.cache;
    }

    @Nullable
    protected Object lookup(Object key) {
        return this.cache.get(key);
    }

    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        try {
            return (T)this.cache.invoke(key, new ValueLoaderEntryProcessor(), new Object[]{valueLoader});
        }
        catch (EntryProcessorException ex) {
            throw new Cache.ValueRetrievalException(key, valueLoader, ex.getCause());
        }
    }

    public void put(Object key, @Nullable Object value) {
        this.cache.put(key, this.toStoreValue(value));
    }

    @Nullable
    public Cache.ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        boolean set = this.cache.putIfAbsent(key, this.toStoreValue(value));
        return set ? null : this.get(key);
    }

    public void evict(Object key) {
        this.cache.remove(key);
    }

    public boolean evictIfPresent(Object key) {
        return this.cache.remove(key);
    }

    public void clear() {
        this.cache.removeAll();
    }

    public boolean invalidate() {
        boolean notEmpty = this.cache.iterator().hasNext();
        this.cache.removeAll();
        return notEmpty;
    }

    private class ValueLoaderEntryProcessor<T>
    implements EntryProcessor<Object, Object, T> {
        private ValueLoaderEntryProcessor() {
        }

        @Nullable
        public T process(MutableEntry<Object, Object> entry, Object ... arguments) throws EntryProcessorException {
            Object value;
            Callable valueLoader = (Callable)arguments[0];
            if (entry.exists()) {
                return (T)JCacheCache.this.fromStoreValue(entry.getValue());
            }
            try {
                value = valueLoader.call();
            }
            catch (Exception ex) {
                throw new EntryProcessorException("Value loader '" + valueLoader + "' failed to compute value for key '" + entry.getKey() + "'", (Throwable)ex);
            }
            entry.setValue(JCacheCache.this.toStoreValue(value));
            return (T)value;
        }
    }
}

