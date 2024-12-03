/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.cache.CacheException
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.Supplier
 *  com.atlassian.cache.impl.CacheEntryListenerSupport
 *  com.atlassian.cache.impl.LazyCacheEntryListenerSupport
 *  com.google.common.collect.ImmutableSortedMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.sf.ehcache.CacheException
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Element
 *  net.sf.ehcache.event.CacheEventListener
 *  net.sf.ehcache.loader.CacheLoader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.ehcache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.CacheException;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.Supplier;
import com.atlassian.cache.ehcache.DelegatingCacheStatistics;
import com.atlassian.cache.ehcache.ManagedCacheSupport;
import com.atlassian.cache.ehcache.ReferenceCacheLoader;
import com.atlassian.cache.ehcache.wrapper.ValueProcessor;
import com.atlassian.cache.ehcache.wrapper.ValueProcessorEhcacheLoaderDecorator;
import com.atlassian.cache.ehcache.wrapper.WrapperUtils;
import com.atlassian.cache.impl.CacheEntryListenerSupport;
import com.atlassian.cache.impl.LazyCacheEntryListenerSupport;
import com.google.common.collect.ImmutableSortedMap;
import java.util.Collection;
import java.util.SortedMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.loader.CacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DelegatingCache<K, V>
extends ManagedCacheSupport
implements Cache<K, V> {
    private final Ehcache delegate;
    private final Logger eventLogger;
    private final Logger stacktraceLogger;
    private final CacheEntryListenerSupport<K, V> listenerSupport = new LazyCacheEntryListenerSupport<K, V>(){

        protected void init() {
            DelegatingCache.this.delegate.getCacheEventNotificationService().registerListener((CacheEventListener)new DelegatingCacheEventListener());
        }
    };
    private final ValueProcessor valueProcessor;

    private DelegatingCache(Ehcache delegate, CacheSettings settings, ValueProcessor valueProcessor) {
        super(delegate, settings);
        this.delegate = delegate;
        this.eventLogger = LoggerFactory.getLogger((String)("com.atlassian.cache.event." + delegate.getName()));
        this.stacktraceLogger = LoggerFactory.getLogger((String)("com.atlassian.cache.stacktrace." + delegate.getName()));
        this.valueProcessor = valueProcessor;
    }

    static <K, V> DelegatingCache<K, V> create(Ehcache delegate, CacheSettings settings, ValueProcessor valueProcessor) {
        return new DelegatingCache<K, V>(delegate, settings, valueProcessor);
    }

    public boolean containsKey(@Nonnull K key) {
        return this.delegate.isKeyInCache(this.wrap(key));
    }

    @Nonnull
    public Collection<K> getKeys() {
        try {
            return WrapperUtils.unwrapAllKeys(this.delegate.getKeys(), this.valueProcessor);
        }
        catch (Exception e) {
            throw new CacheException((Throwable)e);
        }
    }

    public void put(@Nonnull K key, @Nonnull V value) {
        try {
            this.delegate.put(new Element(this.wrap(key), this.wrap(value)));
        }
        catch (Exception e) {
            throw new CacheException((Throwable)e);
        }
    }

    @Nullable
    public V get(@Nonnull K key) {
        try {
            Element element = this.unwrap(this.delegate.get(this.wrap(key)));
            return (V)(element == null ? null : element.getObjectValue());
        }
        catch (net.sf.ehcache.CacheException e) {
            throw new CacheException(e.getCause());
        }
        catch (Exception e) {
            throw new CacheException((Throwable)e);
        }
    }

    @Nonnull
    public V get(@Nonnull K key, @Nonnull Supplier<? extends V> valueSupplier) {
        try {
            Element element = this.unwrap(this.delegate.getWithLoader(this.wrap(key), this.getCacheLoader(valueSupplier), null));
            return (V)element.getObjectValue();
        }
        catch (net.sf.ehcache.CacheException e) {
            throw new CacheException(e.getCause());
        }
        catch (Exception e) {
            throw new CacheException((Throwable)e);
        }
    }

    public void remove(@Nonnull K key) {
        try {
            this.delegate.remove(this.wrap(key));
        }
        catch (Exception e) {
            throw new CacheException((Throwable)e);
        }
    }

    public void removeAll() {
        try {
            this.delegate.removeAll();
            this.eventLogger.info("Cache {} was flushed", (Object)this.delegate.getName());
            if (this.stacktraceLogger.isInfoEnabled()) {
                this.stacktraceLogger.info("Cache {} was flushed. Stacktrace:", (Object)this.delegate.getName(), (Object)new Exception());
            }
        }
        catch (Exception e) {
            throw new CacheException((Throwable)e);
        }
    }

    public void clear() {
        this.removeAll();
    }

    @Nullable
    public V putIfAbsent(@Nonnull K key, @Nonnull V value) {
        try {
            Element previous = this.unwrap(this.delegate.putIfAbsent(new Element(this.wrap(key), this.wrap(value))));
            if (previous != null) {
                return (V)previous.getObjectValue();
            }
            return null;
        }
        catch (Exception e) {
            throw new CacheException((Throwable)e);
        }
    }

    public boolean remove(@Nonnull K key, @Nonnull V value) {
        try {
            return this.delegate.removeElement(new Element(this.wrap(key), this.wrap(value)));
        }
        catch (Exception e) {
            throw new CacheException((Throwable)e);
        }
    }

    public boolean replace(@Nonnull K key, @Nonnull V oldValue, @Nonnull V newValue) {
        try {
            return this.delegate.replace(new Element(this.wrap(key), this.wrap(oldValue)), new Element(this.wrap(key), this.wrap(newValue)));
        }
        catch (Exception e) {
            throw new CacheException((Throwable)e);
        }
    }

    @Nonnull
    public SortedMap<CacheStatisticsKey, java.util.function.Supplier<Long>> getStatistics() {
        if (this.isStatisticsEnabled()) {
            return DelegatingCacheStatistics.toStatistics(this.delegate.getStatistics());
        }
        return ImmutableSortedMap.of();
    }

    public boolean equals(@Nullable Object other) {
        if (other instanceof DelegatingCache) {
            DelegatingCache otherDelegatingCache = (DelegatingCache)other;
            if (this.delegate.equals(otherDelegatingCache.delegate)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return 3 + this.delegate.hashCode();
    }

    public void addListener(@Nonnull CacheEntryListener<K, V> listener, boolean includeValues) {
        this.listenerSupport.add(listener, includeValues);
    }

    public void removeListener(@Nonnull CacheEntryListener<K, V> listener) {
        this.listenerSupport.remove(listener);
    }

    private Object wrap(Object o) {
        return this.valueProcessor.wrap(o);
    }

    private Object unwrap(Object o) {
        return this.valueProcessor.unwrap(o);
    }

    private Element unwrap(Element element) {
        return WrapperUtils.unwrapElement(element, this.valueProcessor);
    }

    private CacheLoader getCacheLoader(Supplier<? extends V> valueSupplier) {
        return new ValueProcessorEhcacheLoaderDecorator(new ReferenceCacheLoader<V>(valueSupplier), this.valueProcessor);
    }

    private class DelegatingCacheEventListener
    implements CacheEventListener {
        private DelegatingCacheEventListener() {
        }

        public void notifyElementRemoved(Ehcache ehcache, Element element) throws net.sf.ehcache.CacheException {
            DelegatingCache.this.listenerSupport.notifyRemove(DelegatingCache.this.unwrap(element.getObjectKey()), DelegatingCache.this.unwrap(element.getObjectValue()));
        }

        public void notifyElementPut(Ehcache ehcache, Element element) throws net.sf.ehcache.CacheException {
            DelegatingCache.this.listenerSupport.notifyAdd(DelegatingCache.this.unwrap(element.getObjectKey()), DelegatingCache.this.unwrap(element.getObjectValue()));
        }

        public void notifyElementUpdated(Ehcache ehcache, Element element) throws net.sf.ehcache.CacheException {
            DelegatingCache.this.listenerSupport.notifyUpdate(DelegatingCache.this.unwrap(element.getObjectKey()), DelegatingCache.this.unwrap(element.getObjectValue()), null);
        }

        public void notifyElementExpired(Ehcache ehcache, Element element) {
            DelegatingCache.this.listenerSupport.notifyEvict(DelegatingCache.this.unwrap(element.getObjectKey()), DelegatingCache.this.unwrap(element.getObjectValue()));
        }

        public void notifyElementEvicted(Ehcache ehcache, Element element) {
            DelegatingCache.this.listenerSupport.notifyEvict(DelegatingCache.this.unwrap(element.getObjectKey()), DelegatingCache.this.unwrap(element.getObjectValue()));
        }

        public void notifyRemoveAll(Ehcache ehcache) {
        }

        public void dispose() {
        }

        public Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException();
        }
    }
}

