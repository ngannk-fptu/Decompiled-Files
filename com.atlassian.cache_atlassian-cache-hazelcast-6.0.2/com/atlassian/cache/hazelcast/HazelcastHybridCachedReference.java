/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheException
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.cache.CachedReferenceEvent
 *  com.atlassian.cache.CachedReferenceListener
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cache.Supplier
 *  com.atlassian.cache.impl.CachedReferenceListenerSupport
 *  com.atlassian.cache.impl.ReferenceKey
 *  com.atlassian.cache.impl.ValueCachedReferenceListenerSupport
 *  com.hazelcast.core.EntryAdapter
 *  com.hazelcast.core.EntryEvent
 *  com.hazelcast.core.EntryListener
 *  com.hazelcast.core.IMap
 *  javax.annotation.Nonnull
 */
package com.atlassian.cache.hazelcast;

import com.atlassian.cache.CacheException;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.CachedReferenceEvent;
import com.atlassian.cache.CachedReferenceListener;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.Supplier;
import com.atlassian.cache.hazelcast.GetOrInitVersionEntryProcessor;
import com.atlassian.cache.hazelcast.HazelcastCacheManager;
import com.atlassian.cache.hazelcast.IncrementVersionEntryProcessor;
import com.atlassian.cache.hazelcast.ManagedHybridCacheSupport;
import com.atlassian.cache.hazelcast.Versioned;
import com.atlassian.cache.impl.CachedReferenceListenerSupport;
import com.atlassian.cache.impl.ReferenceKey;
import com.atlassian.cache.impl.ValueCachedReferenceListenerSupport;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import java.util.Optional;
import javax.annotation.Nonnull;

public class HazelcastHybridCachedReference<V>
extends ManagedHybridCacheSupport
implements CachedReference<V> {
    private final CachedReference<Versioned<V>> localReference;
    private final IMap<ReferenceKey, Long> versionMap;
    private final CachedReferenceListenerSupport<V> listenerSupport = new ValueCachedReferenceListenerSupport<V>(){

        protected void init(CachedReferenceListenerSupport<V> actualListenerSupport) {
            HazelcastHybridCachedReference.this.versionMap.addEntryListener((EntryListener)new HazelcastHybridReferenceEntryListener(), false);
        }

        protected void initValue(CachedReferenceListenerSupport<V> actualListenerSupport) {
            HazelcastHybridCachedReference.this.localReference.addListener(new DelegatingCachedReferenceListener(actualListenerSupport), true);
        }

        protected void initValueless(CachedReferenceListenerSupport<V> actualListenerSupport) {
            HazelcastHybridCachedReference.this.localReference.addListener(new DelegatingCachedReferenceListener(actualListenerSupport), false);
        }
    };

    public HazelcastHybridCachedReference(String name, CacheFactory localFactory, IMap<ReferenceKey, Long> versionMap, final Supplier<V> supplier, HazelcastCacheManager cacheManager) {
        super(name, cacheManager);
        Supplier localSupplier = new Supplier<Versioned<V>>(){

            public Versioned<V> get() {
                long version = HazelcastHybridCachedReference.this.getVersion();
                Object value = supplier.get();
                if (value == null) {
                    throw new CacheException("The Supplier for cached reference '" + HazelcastHybridCachedReference.this.getName() + "'returned null. Null values are not supported.");
                }
                return new Versioned<Object>(value, version);
            }
        };
        this.versionMap = versionMap;
        this.localReference = localFactory.getCachedReference(name, localSupplier, this.getCacheSettings());
    }

    @Nonnull
    public V get() {
        Versioned value = (Versioned)this.localReference.get();
        Long version = (Long)this.versionMap.get((Object)ReferenceKey.KEY);
        if (version == null || value.getVersion() != version.longValue()) {
            this.localReference.reset();
            value = (Versioned)this.localReference.get();
        }
        return (V)value.getValue();
    }

    public boolean isFlushable() {
        return this.getCacheSettings().getFlushable(true);
    }

    public boolean isReplicateAsynchronously() {
        return false;
    }

    public void reset() {
        this.versionMap.executeOnKey((Object)ReferenceKey.KEY, IncrementVersionEntryProcessor.getInstance());
        this.localReference.reset();
    }

    public boolean isPresent() {
        Optional value = this.localReference.getIfPresent();
        Long version = (Long)this.versionMap.get((Object)ReferenceKey.KEY);
        return version != null && value.isPresent() && ((Versioned)value.get()).getVersion() == version.longValue();
    }

    @Nonnull
    public Optional<V> getIfPresent() {
        Optional value = this.localReference.getIfPresent();
        Long version = (Long)this.versionMap.get((Object)ReferenceKey.KEY);
        if (version != null && value.isPresent() && ((Versioned)value.get()).getVersion() == version.longValue()) {
            return value.map(Versioned::getValue);
        }
        return Optional.empty();
    }

    @Override
    protected ManagedCache getLocalCache() {
        return (ManagedCache)this.localReference;
    }

    private CacheSettings getCacheSettings() {
        return this.cacheManager.getCacheSettings(this.getHazelcastMapName());
    }

    private String getHazelcastMapName() {
        return this.versionMap.getName();
    }

    public void clear() {
        if (this.isFlushable()) {
            this.reset();
        }
    }

    @Override
    public boolean updateMaxEntries(int newValue) {
        return false;
    }

    public void addListener(@Nonnull CachedReferenceListener<V> listener, boolean includeValues) {
        this.listenerSupport.add(listener, includeValues);
    }

    public void removeListener(@Nonnull CachedReferenceListener<V> listener) {
        this.listenerSupport.remove(listener);
    }

    private long getVersion() {
        Long version = (Long)this.versionMap.get((Object)ReferenceKey.KEY);
        if (version == null) {
            version = (Long)this.versionMap.executeOnKey((Object)ReferenceKey.KEY, GetOrInitVersionEntryProcessor.getInstance());
        }
        return version;
    }

    private static class DelegatingCachedReferenceListener<V>
    implements CachedReferenceListener<Versioned<V>> {
        private final CachedReferenceListenerSupport<V> listenerSupport;

        private DelegatingCachedReferenceListener(CachedReferenceListenerSupport<V> listenerSupport) {
            this.listenerSupport = listenerSupport;
        }

        public void onEvict(@Nonnull CachedReferenceEvent<Versioned<V>> event) {
            this.listenerSupport.notifyEvict(this.get((Versioned)event.getValue()));
        }

        public void onSet(@Nonnull CachedReferenceEvent<Versioned<V>> event) {
            this.listenerSupport.notifySet(this.get((Versioned)event.getValue()));
        }

        public void onReset(@Nonnull CachedReferenceEvent<Versioned<V>> event) {
            this.listenerSupport.notifyReset(this.get((Versioned)event.getValue()));
        }

        private V get(Versioned<V> versioned) {
            return versioned != null ? (V)versioned.getValue() : null;
        }
    }

    private class HazelcastHybridReferenceEntryListener
    extends EntryAdapter<ReferenceKey, Long> {
        private HazelcastHybridReferenceEntryListener() {
        }

        public void entryRemoved(EntryEvent<ReferenceKey, Long> event) {
            HazelcastHybridCachedReference.this.localReference.reset();
        }

        public void entryUpdated(EntryEvent<ReferenceKey, Long> event) {
            HazelcastHybridCachedReference.this.localReference.reset();
        }

        public void entryEvicted(EntryEvent<ReferenceKey, Long> event) {
            HazelcastHybridCachedReference.this.localReference.reset();
        }
    }
}

