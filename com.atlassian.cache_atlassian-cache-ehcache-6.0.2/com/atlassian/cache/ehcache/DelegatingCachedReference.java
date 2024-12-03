/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheException
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.CachedReferenceListener
 *  com.atlassian.cache.impl.CachedReferenceListenerSupport
 *  com.atlassian.cache.impl.LazyCachedReferenceListenerSupport
 *  com.atlassian.cache.impl.ReferenceKey
 *  com.google.common.collect.ImmutableSortedMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.sf.ehcache.CacheException
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Element
 *  net.sf.ehcache.event.CacheEventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.ehcache;

import com.atlassian.cache.CacheException;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.CachedReferenceListener;
import com.atlassian.cache.ehcache.DelegatingCacheStatistics;
import com.atlassian.cache.ehcache.ManagedCacheSupport;
import com.atlassian.cache.ehcache.wrapper.ValueProcessor;
import com.atlassian.cache.ehcache.wrapper.WrapperUtils;
import com.atlassian.cache.impl.CachedReferenceListenerSupport;
import com.atlassian.cache.impl.LazyCachedReferenceListenerSupport;
import com.atlassian.cache.impl.ReferenceKey;
import com.google.common.collect.ImmutableSortedMap;
import java.util.Optional;
import java.util.SortedMap;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DelegatingCachedReference<V>
extends ManagedCacheSupport
implements CachedReference<V> {
    private final Ehcache delegate;
    private final CachedReferenceListenerSupport<V> listenerSupport = new LazyCachedReferenceListenerSupport<V>(){

        protected void init() {
            DelegatingCachedReference.this.delegate.getCacheEventNotificationService().registerListener((CacheEventListener)new DelegatingReferenceCacheEventListener());
        }
    };
    private final Logger eventLogger;
    private final Logger stacktraceLogger;
    private final ValueProcessor valueProcessor;

    private DelegatingCachedReference(Ehcache delegate, CacheSettings settings, ValueProcessor valueProcessor) {
        super(delegate, settings);
        this.delegate = delegate;
        this.eventLogger = LoggerFactory.getLogger((String)("com.atlassian.cache.event." + delegate.getName()));
        this.stacktraceLogger = LoggerFactory.getLogger((String)("com.atlassian.cache.stacktrace." + delegate.getName()));
        this.valueProcessor = valueProcessor;
    }

    static <V> DelegatingCachedReference<V> create(Ehcache delegate, CacheSettings settings, ValueProcessor valueProcessor) {
        return new DelegatingCachedReference<V>(delegate, settings, valueProcessor);
    }

    @Nonnull
    public V get() {
        try {
            return (V)this.unwrap(this.delegate.get(this.wrap(ReferenceKey.KEY))).getObjectValue();
        }
        catch (net.sf.ehcache.CacheException e) {
            throw new CacheException(e.getCause());
        }
        catch (RuntimeException e) {
            throw new CacheException((Throwable)e);
        }
    }

    public void reset() {
        try {
            this.delegate.remove(this.wrap(ReferenceKey.KEY));
            this.eventLogger.info("Cache {} was flushed", (Object)this.delegate.getName());
            if (this.stacktraceLogger.isInfoEnabled()) {
                this.stacktraceLogger.info("Cache {} was flushed. Stacktrace:", (Object)this.delegate.getName(), (Object)new Exception());
            }
        }
        catch (RuntimeException e) {
            throw new CacheException((Throwable)e);
        }
    }

    public boolean isPresent() {
        boolean bl;
        Object key = this.wrap(ReferenceKey.KEY);
        this.delegate.acquireReadLockOnKey(key);
        try {
            bl = this.delegate.isKeyInCache(key) && this.delegate.getQuiet(key) != null;
        }
        catch (Throwable throwable) {
            try {
                this.delegate.releaseReadLockOnKey(key);
                throw throwable;
            }
            catch (RuntimeException e) {
                throw new CacheException((Throwable)e);
            }
        }
        this.delegate.releaseReadLockOnKey(key);
        return bl;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nonnull
    public Optional<V> getIfPresent() {
        try {
            Object key = this.wrap(ReferenceKey.KEY);
            this.delegate.acquireReadLockOnKey(key);
            try {
                if (this.delegate.isKeyInCache(key) && this.delegate.getQuiet(key) != null) {
                    Optional<Object> optional = Optional.of(this.unwrap(this.delegate.get(key)).getObjectValue());
                    return optional;
                }
                Optional optional = Optional.empty();
                return optional;
            }
            finally {
                this.delegate.releaseReadLockOnKey(key);
            }
        }
        catch (RuntimeException e) {
            throw new CacheException((Throwable)e);
        }
    }

    public void clear() {
        this.reset();
    }

    public boolean equals(@Nullable Object other) {
        if (other instanceof DelegatingCachedReference) {
            DelegatingCachedReference otherDelegatingReference = (DelegatingCachedReference)other;
            if (this.delegate.equals(otherDelegatingReference.delegate)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return 3 + this.delegate.hashCode();
    }

    @Nonnull
    public SortedMap<CacheStatisticsKey, Supplier<Long>> getStatistics() {
        if (this.isStatisticsEnabled()) {
            return DelegatingCacheStatistics.toStatistics(this.delegate.getStatistics());
        }
        return ImmutableSortedMap.of();
    }

    public void addListener(@Nonnull CachedReferenceListener<V> listener, boolean includeValues) {
        this.listenerSupport.add(listener, includeValues);
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

    public void removeListener(@Nonnull CachedReferenceListener<V> listener) {
        this.listenerSupport.remove(listener);
    }

    private class DelegatingReferenceCacheEventListener
    implements CacheEventListener {
        private DelegatingReferenceCacheEventListener() {
        }

        public void notifyElementRemoved(Ehcache ehcache, Element element) throws net.sf.ehcache.CacheException {
            DelegatingCachedReference.this.listenerSupport.notifyReset(DelegatingCachedReference.this.unwrap(element.getObjectValue()));
        }

        public void notifyElementPut(Ehcache ehcache, Element element) throws net.sf.ehcache.CacheException {
            DelegatingCachedReference.this.listenerSupport.notifySet(DelegatingCachedReference.this.unwrap(element.getObjectValue()));
        }

        public void notifyElementUpdated(Ehcache ehcache, Element element) throws net.sf.ehcache.CacheException {
            DelegatingCachedReference.this.listenerSupport.notifySet(DelegatingCachedReference.this.unwrap(element.getObjectValue()));
        }

        public void notifyElementExpired(Ehcache ehcache, Element element) {
            DelegatingCachedReference.this.listenerSupport.notifyEvict(DelegatingCachedReference.this.unwrap(element.getObjectValue()));
        }

        public void notifyElementEvicted(Ehcache ehcache, Element element) {
            DelegatingCachedReference.this.listenerSupport.notifyEvict(DelegatingCachedReference.this.unwrap(element.getObjectValue()));
        }

        public void notifyRemoveAll(Ehcache ehcache) {
            DelegatingCachedReference.this.listenerSupport.notifyReset(null);
        }

        public void dispose() {
        }

        public Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException();
        }
    }
}

