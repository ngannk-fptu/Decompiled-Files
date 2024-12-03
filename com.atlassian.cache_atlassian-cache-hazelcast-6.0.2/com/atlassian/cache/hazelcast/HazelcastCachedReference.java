/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheException
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.CachedReferenceEvent
 *  com.atlassian.cache.CachedReferenceListener
 *  com.atlassian.cache.Supplier
 *  com.atlassian.cache.impl.CachedReferenceListenerSupport
 *  com.atlassian.cache.impl.DefaultCachedReferenceEvent
 *  com.atlassian.cache.impl.ValueCachedReferenceListenerSupport
 *  com.atlassian.hazelcast.serialization.OsgiSafe
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Throwables
 *  com.hazelcast.core.EntryAdapter
 *  com.hazelcast.core.EntryEvent
 *  com.hazelcast.core.IMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.cache.hazelcast;

import com.atlassian.cache.CacheException;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.CachedReferenceEvent;
import com.atlassian.cache.CachedReferenceListener;
import com.atlassian.cache.Supplier;
import com.atlassian.cache.hazelcast.HazelcastCacheManager;
import com.atlassian.cache.hazelcast.ManagedCacheSupport;
import com.atlassian.cache.hazelcast.OsgiSafeUtils;
import com.atlassian.cache.impl.CachedReferenceListenerSupport;
import com.atlassian.cache.impl.DefaultCachedReferenceEvent;
import com.atlassian.cache.impl.ValueCachedReferenceListenerSupport;
import com.atlassian.hazelcast.serialization.OsgiSafe;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.IMap;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HazelcastCachedReference<V>
extends ManagedCacheSupport
implements CachedReference<V> {
    private final IMap<String, OsgiSafe<V>> hazelcastMap;
    private final Supplier<V> supplier;
    private final CachedReferenceListenerSupport<OsgiSafe<V>> listenerSupport = new ValueCachedReferenceListenerSupport<OsgiSafe<V>>(){

        protected void initValue(CachedReferenceListenerSupport<OsgiSafe<V>> actualListenerSupport) {
            HazelcastCachedReference.this.hazelcastMap.addEntryListener(new HazelcastCachedReferenceListener(actualListenerSupport), true);
        }

        protected void initValueless(CachedReferenceListenerSupport<OsgiSafe<V>> actualListenerSupport) {
            HazelcastCachedReference.this.hazelcastMap.addEntryListener(new HazelcastCachedReferenceListener(actualListenerSupport), false);
        }
    };
    private static final String REFERENCE_KEY = "ReferenceKey";

    HazelcastCachedReference(String name, IMap<String, OsgiSafe<V>> hazelcastMap, Supplier<V> supplier, HazelcastCacheManager cacheManager) {
        super(name, cacheManager);
        this.hazelcastMap = hazelcastMap;
        this.supplier = supplier;
    }

    public void clear() {
        this.hazelcastMap.remove((Object)REFERENCE_KEY);
    }

    @Nonnull
    public V get() {
        try {
            OsgiSafe<Object> value = (OsgiSafe<Object>)this.hazelcastMap.get((Object)REFERENCE_KEY);
            if (value == null) {
                Object newValue = this.supplier.get();
                if (newValue == null) {
                    throw new CacheException("The provided supplier returned null. Null values are not supported.");
                }
                value = OsgiSafeUtils.wrap(newValue);
                OsgiSafe current = (OsgiSafe)this.hazelcastMap.putIfAbsent((Object)REFERENCE_KEY, value);
                return (V)OsgiSafeUtils.unwrap((OsgiSafe)MoreObjects.firstNonNull((Object)current, value));
            }
            return (V)OsgiSafeUtils.unwrap(value);
        }
        catch (RuntimeException e) {
            Throwables.throwIfInstanceOf((Throwable)e, CacheException.class);
            throw new CacheException((Throwable)e);
        }
    }

    public void reset() {
        try {
            this.hazelcastMap.remove((Object)REFERENCE_KEY);
        }
        catch (RuntimeException e) {
            throw new CacheException((Throwable)e);
        }
    }

    public boolean isPresent() {
        try {
            return this.hazelcastMap.containsKey((Object)REFERENCE_KEY);
        }
        catch (RuntimeException e) {
            throw new CacheException((Throwable)e);
        }
    }

    @Nonnull
    public Optional<V> getIfPresent() {
        try {
            OsgiSafe value = (OsgiSafe)this.hazelcastMap.get((Object)REFERENCE_KEY);
            if (value != null) {
                return Optional.of(OsgiSafeUtils.unwrap(value));
            }
            return Optional.empty();
        }
        catch (RuntimeException e) {
            throw new CacheException((Throwable)e);
        }
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HazelcastCachedReference other = (HazelcastCachedReference)o;
        return this.hazelcastMap.equals(other.hazelcastMap);
    }

    public int hashCode() {
        return 3 + this.hazelcastMap.hashCode();
    }

    public void addListener(@Nonnull CachedReferenceListener<V> listener, boolean includeValues) {
        this.listenerSupport.add(new OsgiSafeCachedReferenceListener(listener), includeValues);
    }

    public void removeListener(@Nonnull CachedReferenceListener<V> listener) {
        this.listenerSupport.remove(new OsgiSafeCachedReferenceListener(listener));
    }

    @Override
    @Nonnull
    protected String getHazelcastMapName() {
        return this.hazelcastMap.getName();
    }

    private static class HazelcastCachedReferenceListener<V>
    extends EntryAdapter<String, V> {
        private final CachedReferenceListenerSupport listenerSupport;

        private HazelcastCachedReferenceListener(CachedReferenceListenerSupport listenerSupport) {
            this.listenerSupport = listenerSupport;
        }

        public void entryAdded(EntryEvent<String, V> event) {
            this.listenerSupport.notifySet(event.getValue());
        }

        public void entryRemoved(EntryEvent<String, V> event) {
            this.listenerSupport.notifyReset(event.getOldValue());
        }

        public void entryUpdated(EntryEvent<String, V> event) {
            this.listenerSupport.notifySet(event.getValue());
        }

        public void entryEvicted(EntryEvent<String, V> event) {
            this.listenerSupport.notifyEvict(event.getOldValue());
        }
    }

    private static class OsgiSafeCachedReferenceListener<V>
    implements CachedReferenceListener<OsgiSafe<V>> {
        private final CachedReferenceListener<V> delegate;

        private OsgiSafeCachedReferenceListener(CachedReferenceListener<V> listener) {
            this.delegate = (CachedReferenceListener)Preconditions.checkNotNull(listener, (Object)"listener");
        }

        public void onEvict(@Nonnull CachedReferenceEvent<OsgiSafe<V>> event) {
            this.delegate.onEvict((CachedReferenceEvent)new DefaultCachedReferenceEvent(OsgiSafeUtils.unwrap((OsgiSafe)event.getValue())));
        }

        public void onSet(@Nonnull CachedReferenceEvent<OsgiSafe<V>> event) {
            this.delegate.onSet((CachedReferenceEvent)new DefaultCachedReferenceEvent(OsgiSafeUtils.unwrap((OsgiSafe)event.getValue())));
        }

        public void onReset(@Nonnull CachedReferenceEvent<OsgiSafe<V>> event) {
            this.delegate.onReset((CachedReferenceEvent)new DefaultCachedReferenceEvent(OsgiSafeUtils.unwrap((OsgiSafe)event.getValue())));
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            OsgiSafeCachedReferenceListener that = (OsgiSafeCachedReferenceListener)o;
            return this.delegate.equals(that.delegate);
        }

        public int hashCode() {
            return this.delegate.hashCode();
        }
    }
}

