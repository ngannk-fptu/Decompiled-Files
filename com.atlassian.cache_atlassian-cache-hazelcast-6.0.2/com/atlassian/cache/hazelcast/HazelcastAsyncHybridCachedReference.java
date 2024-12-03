/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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

import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CachedReference;
import com.atlassian.cache.CachedReferenceEvent;
import com.atlassian.cache.CachedReferenceListener;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.Supplier;
import com.atlassian.cache.hazelcast.HazelcastCacheManager;
import com.atlassian.cache.hazelcast.ManagedHybridCacheSupport;
import com.atlassian.cache.impl.CachedReferenceListenerSupport;
import com.atlassian.cache.impl.ReferenceKey;
import com.atlassian.cache.impl.ValueCachedReferenceListenerSupport;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MembershipAdapter;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import java.lang.ref.WeakReference;
import java.util.Optional;
import javax.annotation.Nonnull;

public class HazelcastAsyncHybridCachedReference<V>
extends ManagedHybridCacheSupport
implements CachedReference<V> {
    private final AsyncInvalidationListener listener;
    private final CachedReferenceListenerSupport<V> listenerSupport = new ValueCachedReferenceListenerSupport<V>(){

        protected void initValue(CachedReferenceListenerSupport<V> actualListenerSupport) {
            HazelcastAsyncHybridCachedReference.this.localReference.addListener(new DelegatingCachedReferenceListener(actualListenerSupport), true);
        }

        protected void initValueless(CachedReferenceListenerSupport<V> actualListenerSupport) {
            HazelcastAsyncHybridCachedReference.this.localReference.addListener(new DelegatingCachedReferenceListener(actualListenerSupport), false);
        }
    };
    private final CachedReference<V> localReference;

    public HazelcastAsyncHybridCachedReference(String name, CacheFactory localFactory, ITopic<ReferenceKey> topic, Supplier<V> supplier, HazelcastCacheManager cacheManager, CacheSettings settings) {
        super(name, cacheManager);
        this.localReference = localFactory.getCachedReference(name, supplier, settings);
        this.listener = new AsyncInvalidationListener(cacheManager.getHazelcastInstance().getCluster(), this.localReference, topic);
    }

    @Nonnull
    public V get() {
        return (V)this.localReference.get();
    }

    public boolean isFlushable() {
        return true;
    }

    public boolean isReplicateAsynchronously() {
        return true;
    }

    public void reset() {
        this.localReference.reset();
        this.invalidateRemotely();
    }

    public boolean isPresent() {
        return this.localReference.isPresent();
    }

    @Nonnull
    public Optional<V> getIfPresent() {
        return this.localReference.getIfPresent();
    }

    @Override
    protected ManagedCache getLocalCache() {
        return (ManagedCache)this.localReference;
    }

    public void clear() {
        this.reset();
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

    private void invalidateRemotely() {
        this.listener.publish(null);
    }

    private static class AsyncInvalidationListener
    extends MembershipAdapter
    implements MessageListener<ReferenceKey> {
        private final Cluster cluster;
        private final WeakReference<CachedReference<?>> localReferenceRef;
        private final String membershipListenerId;
        private final ITopic<ReferenceKey> topic;
        private final String topicListenerId;

        AsyncInvalidationListener(Cluster cluster, CachedReference<?> localReference, ITopic<ReferenceKey> topic) {
            this.cluster = cluster;
            this.localReferenceRef = new WeakReference(localReference);
            this.topic = topic;
            this.topicListenerId = topic.addMessageListener((MessageListener)this);
            this.membershipListenerId = cluster.addMembershipListener((MembershipListener)this);
        }

        public void memberAdded(MembershipEvent membershipEvent) {
            CachedReference localReference = (CachedReference)this.localReferenceRef.get();
            if (localReference == null) {
                this.destroy();
                return;
            }
            localReference.reset();
        }

        public void onMessage(Message<ReferenceKey> message) {
            CachedReference localReference = (CachedReference)this.localReferenceRef.get();
            if (localReference == null) {
                this.destroy();
                return;
            }
            if (!message.getPublishingMember().localMember()) {
                localReference.reset();
            }
        }

        void destroy() {
            this.cluster.removeMembershipListener(this.membershipListenerId);
            this.topic.removeMessageListener(this.topicListenerId);
        }

        void publish(ReferenceKey message) {
            this.topic.publish((Object)message);
        }
    }

    private static class DelegatingCachedReferenceListener<V>
    implements CachedReferenceListener<V> {
        private final CachedReferenceListenerSupport<V> listenerSupport;

        private DelegatingCachedReferenceListener(CachedReferenceListenerSupport<V> listenerSupport) {
            this.listenerSupport = listenerSupport;
        }

        public void onEvict(@Nonnull CachedReferenceEvent<V> event) {
            this.listenerSupport.notifyEvict(event.getValue());
        }

        public void onSet(@Nonnull CachedReferenceEvent<V> event) {
            this.listenerSupport.notifySet(event.getValue());
        }

        public void onReset(@Nonnull CachedReferenceEvent<V> event) {
            this.listenerSupport.notifyReset(event.getValue());
        }
    }
}

