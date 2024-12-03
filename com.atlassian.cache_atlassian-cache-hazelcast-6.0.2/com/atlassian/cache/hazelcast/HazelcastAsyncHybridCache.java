/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheEntryEvent
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cache.Supplier
 *  com.atlassian.cache.impl.CacheEntryListenerSupport
 *  com.atlassian.cache.impl.ValueCacheEntryListenerSupport
 *  com.hazelcast.core.Cluster
 *  com.hazelcast.core.ITopic
 *  com.hazelcast.core.MembershipAdapter
 *  com.hazelcast.core.MembershipEvent
 *  com.hazelcast.core.MembershipListener
 *  com.hazelcast.core.Message
 *  com.hazelcast.core.MessageListener
 *  javax.annotation.Nonnull
 */
package com.atlassian.cache.hazelcast;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheEntryEvent;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.Supplier;
import com.atlassian.cache.hazelcast.HazelcastCacheManager;
import com.atlassian.cache.hazelcast.ManagedHybridCacheSupport;
import com.atlassian.cache.impl.CacheEntryListenerSupport;
import com.atlassian.cache.impl.ValueCacheEntryListenerSupport;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MembershipAdapter;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;

public class HazelcastAsyncHybridCache<K, V>
extends ManagedHybridCacheSupport
implements Cache<K, V> {
    private final AsyncInvalidationListener<K> listener;
    private final Cache<K, V> localCache;
    private final CacheEntryListenerSupport<K, V> listenerSupport = new ValueCacheEntryListenerSupport<K, V>(){

        protected void initValue(CacheEntryListenerSupport<K, V> actualListenerSupport) {
            HazelcastAsyncHybridCache.this.localCache.addListener(new DelegatingCacheEntryListener(actualListenerSupport), true);
        }

        protected void initValueless(CacheEntryListenerSupport<K, V> actualListenerSupport) {
            HazelcastAsyncHybridCache.this.localCache.addListener(new DelegatingCacheEntryListener(actualListenerSupport), false);
        }
    };

    public HazelcastAsyncHybridCache(String name, CacheFactory localCacheFactory, ITopic<K> invalidationTopic, CacheLoader<K, V> cacheLoader, HazelcastCacheManager cacheManager, CacheSettings settings) {
        super(name, cacheManager);
        this.localCache = localCacheFactory.getCache(name, cacheLoader, settings);
        this.listener = new AsyncInvalidationListener<K>(cacheManager.getHazelcastInstance().getCluster(), this.localCache, invalidationTopic);
    }

    public void clear() {
        this.removeAll();
    }

    public boolean containsKey(@Nonnull K key) {
        return this.localCache.containsKey(key);
    }

    public V get(@Nonnull K key) {
        return (V)this.localCache.get(key);
    }

    @Nonnull
    public V get(@Nonnull K key, @Nonnull Supplier<? extends V> valueSupplier) {
        return (V)this.localCache.get(key, valueSupplier);
    }

    @Nonnull
    public Map<K, V> getBulk(@Nonnull Set<K> keys, @Nonnull Function<Set<K>, Map<K, V>> valuesSupplier) {
        return this.localCache.getBulk(keys, valuesSupplier);
    }

    @Nonnull
    public Collection<K> getKeys() {
        return this.localCache.getKeys();
    }

    @Override
    @Nonnull
    public String getName() {
        return this.localCache.getName();
    }

    public boolean isFlushable() {
        return true;
    }

    public boolean isReplicateAsynchronously() {
        return true;
    }

    public void put(@Nonnull K key, @Nonnull V value) {
        this.invalidateRemotely(key);
        this.localCache.put(key, value);
    }

    public V putIfAbsent(@Nonnull K key, @Nonnull V value) {
        Object oldValue = this.localCache.putIfAbsent(key, value);
        if (oldValue == null) {
            this.invalidateRemotely(key);
        }
        return (V)oldValue;
    }

    public void remove(@Nonnull K key) {
        this.invalidateRemotely(key);
        this.localCache.remove(key);
    }

    public boolean remove(@Nonnull K key, @Nonnull V value) {
        if (this.localCache.remove(key, value)) {
            this.invalidateRemotely(key);
            return true;
        }
        return false;
    }

    public void removeAll() {
        this.invalidateRemotely();
        this.localCache.removeAll();
    }

    public boolean replace(@Nonnull K key, @Nonnull V oldValue, @Nonnull V newValue) {
        if (this.localCache.replace(key, oldValue, newValue)) {
            this.invalidateRemotely(key);
            return true;
        }
        return false;
    }

    public void addListener(@Nonnull CacheEntryListener<K, V> listener, boolean includeValues) {
        this.listenerSupport.add(listener, includeValues);
    }

    public void removeListener(@Nonnull CacheEntryListener<K, V> listener) {
        this.listenerSupport.remove(listener);
    }

    @Override
    protected ManagedCache getLocalCache() {
        return (ManagedCache)this.localCache;
    }

    private void invalidateRemotely() {
        this.listener.publish(null);
    }

    private void invalidateRemotely(@Nonnull K key) {
        this.listener.publish(key);
    }

    private static class AsyncInvalidationListener<K>
    extends MembershipAdapter
    implements MessageListener<K> {
        private final Cluster cluster;
        private final WeakReference<Cache<K, ?>> localCacheRef;
        private final String membershipListenerId;
        private final ITopic<K> topic;
        private final String topicListenerId;

        AsyncInvalidationListener(Cluster cluster, Cache<K, ?> localCache, ITopic<K> topic) {
            this.cluster = cluster;
            this.localCacheRef = new WeakReference(localCache);
            this.topic = topic;
            this.topicListenerId = topic.addMessageListener((MessageListener)this);
            this.membershipListenerId = cluster.addMembershipListener((MembershipListener)this);
        }

        public void memberAdded(MembershipEvent membershipEvent) {
            Cache localCache = (Cache)this.localCacheRef.get();
            if (localCache == null) {
                this.destroy();
                return;
            }
            localCache.removeAll();
        }

        public void onMessage(Message<K> message) {
            Cache localCache = (Cache)this.localCacheRef.get();
            if (localCache == null) {
                this.destroy();
                return;
            }
            if (!message.getPublishingMember().localMember()) {
                Object key = message.getMessageObject();
                if (key == null) {
                    localCache.removeAll();
                } else {
                    localCache.remove(key);
                }
            }
        }

        void destroy() {
            this.cluster.removeMembershipListener(this.membershipListenerId);
            this.topic.removeMessageListener(this.topicListenerId);
        }

        void publish(K message) {
            this.topic.publish(message);
        }
    }

    private static class DelegatingCacheEntryListener<K, V>
    implements CacheEntryListener<K, V> {
        private final CacheEntryListenerSupport<K, V> listenerSupport;

        private DelegatingCacheEntryListener(CacheEntryListenerSupport<K, V> listenerSupport) {
            this.listenerSupport = listenerSupport;
        }

        public void onAdd(@Nonnull CacheEntryEvent<K, V> event) {
            this.listenerSupport.notifyAdd(event.getKey(), event.getValue());
        }

        public void onEvict(@Nonnull CacheEntryEvent<K, V> event) {
            this.listenerSupport.notifyEvict(event.getKey(), event.getOldValue());
        }

        public void onRemove(@Nonnull CacheEntryEvent<K, V> event) {
            this.listenerSupport.notifyRemove(event.getKey(), event.getOldValue());
        }

        public void onUpdate(@Nonnull CacheEntryEvent<K, V> event) {
            this.listenerSupport.notifyUpdate(event.getKey(), event.getValue(), event.getOldValue());
        }
    }
}

