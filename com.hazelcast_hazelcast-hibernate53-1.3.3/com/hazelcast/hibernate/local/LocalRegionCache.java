/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.MapConfig
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.ITopic
 *  com.hazelcast.core.Message
 *  com.hazelcast.core.MessageListener
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.logging.Logger
 *  com.hazelcast.util.Clock
 *  com.hazelcast.util.EmptyStatement
 *  org.hibernate.cache.cfg.spi.CollectionDataCachingConfig
 *  org.hibernate.cache.cfg.spi.DomainDataRegionConfig
 *  org.hibernate.cache.cfg.spi.EntityDataCachingConfig
 *  org.hibernate.cache.spi.RegionFactory
 *  org.hibernate.cache.spi.access.SoftLock
 *  org.hibernate.cache.spi.support.AbstractReadWriteAccess$Lockable
 */
package com.hazelcast.hibernate.local;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.hazelcast.hibernate.CacheEnvironment;
import com.hazelcast.hibernate.HazelcastTimestamper;
import com.hazelcast.hibernate.RegionCache;
import com.hazelcast.hibernate.local.Invalidation;
import com.hazelcast.hibernate.serialization.Expirable;
import com.hazelcast.hibernate.serialization.ExpiryMarker;
import com.hazelcast.hibernate.serialization.Value;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.Clock;
import com.hazelcast.util.EmptyStatement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.hibernate.cache.cfg.spi.CollectionDataCachingConfig;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;

public class LocalRegionCache
implements RegionCache {
    private static final long SEC_TO_MS = 1000L;
    private static final int MAX_SIZE = 100000;
    private static final float BASE_EVICTION_RATE = 0.2f;
    protected final ConcurrentMap<Object, Expirable> cache;
    private final HazelcastInstance hazelcastInstance;
    private final ILogger log = Logger.getLogger(this.getClass());
    private final String name;
    private final RegionFactory regionFactory;
    private final ITopic<Object> topic;
    private final Comparator versionComparator;
    private final EvictionConfig evictionConfig;
    private MapConfig config;

    public LocalRegionCache(RegionFactory regionFactory, String name, HazelcastInstance hazelcastInstance, DomainDataRegionConfig regionConfig) {
        this(regionFactory, name, hazelcastInstance, regionConfig, true);
    }

    public LocalRegionCache(RegionFactory regionFactory, String name, HazelcastInstance hazelcastInstance, DomainDataRegionConfig regionConfig, boolean withTopic) {
        this(regionFactory, name, hazelcastInstance, regionConfig, withTopic, null);
    }

    public LocalRegionCache(RegionFactory regionFactory, String name, HazelcastInstance hazelcastInstance, DomainDataRegionConfig regionConfig, boolean withTopic, EvictionConfig evictionConfig) {
        this.hazelcastInstance = hazelcastInstance;
        this.name = name;
        this.regionFactory = regionFactory;
        try {
            this.config = hazelcastInstance == null ? null : hazelcastInstance.getConfig().findMapConfig(name);
        }
        catch (UnsupportedOperationException ignored) {
            EmptyStatement.ignore((Throwable)ignored);
        }
        this.cache = new ConcurrentHashMap<Object, Expirable>();
        if (withTopic && hazelcastInstance != null) {
            this.topic = hazelcastInstance.getTopic(name);
            this.topic.addMessageListener(this.createMessageListener());
        } else {
            this.topic = null;
        }
        this.versionComparator = this.findVersionComparator(regionConfig);
        this.evictionConfig = evictionConfig == null ? EvictionConfig.create(this.config) : evictionConfig;
    }

    @Override
    public void afterUpdate(Object key, Object newValue, Object newVersion) {
        this.maybeNotifyTopic(key, newValue, newVersion);
    }

    @Override
    public boolean contains(Object key) {
        return this.cache.containsKey(key);
    }

    @Override
    public void evictData() {
        this.cache.clear();
        this.maybeNotifyTopic(null, null, null);
    }

    @Override
    public void evictData(Object key) {
        Expirable value = (Expirable)this.cache.remove(key);
        this.maybeNotifyTopic(key, null, value == null ? null : value.getVersion());
    }

    @Override
    public Object get(Object key, long txTimestamp) {
        Expirable value = (Expirable)this.cache.get(key);
        return value == null ? null : value.getValue(txTimestamp);
    }

    public long getElementCountInMemory() {
        return this.cache.size();
    }

    public String getName() {
        return this.name;
    }

    public RegionFactory getRegionFactory() {
        return this.regionFactory;
    }

    public long getSizeInMemory() {
        return 0L;
    }

    @Override
    public boolean put(Object key, Object value, long txTimestamp, Object version) {
        Value newValue = new Value(version, this.nextTimestamp(), value);
        this.cache.put(key, newValue);
        return true;
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) {
        this.maybeNotifyTopic(key, null, null);
    }

    @Override
    public long nextTimestamp() {
        return this.hazelcastInstance == null ? Clock.currentTimeMillis() : HazelcastTimestamper.nextTimestamp(this.hazelcastInstance);
    }

    protected Object createMessage(Object key, Object value, Object currentVersion) {
        return new Invalidation(key, currentVersion);
    }

    protected void maybeInvalidate(Object messageObject) {
        Invalidation invalidation = (Invalidation)messageObject;
        Object key = invalidation.getKey();
        if (key == null) {
            this.cache.clear();
        } else if (this.versionComparator == null) {
            this.cache.remove(key);
        } else {
            Expirable value = (Expirable)this.cache.get(key);
            if (value != null) {
                this.maybeInvalidateVersionedEntity(key, value, invalidation.getVersion());
            }
        }
    }

    void cleanup() {
        boolean limitSize;
        int maxSize = this.evictionConfig.getMaxSize();
        long timeToLive = this.evictionConfig.getTimeToLive().toMillis();
        boolean bl = limitSize = maxSize > 0 && maxSize != Integer.MAX_VALUE;
        if (limitSize || timeToLive > 0L) {
            List<EvictionEntry> entries = this.searchEvictableEntries(timeToLive, limitSize);
            int diff = this.cache.size() - maxSize;
            int evictionRate = this.calculateEvictionRate(diff, maxSize);
            if (evictionRate > 0 && entries != null) {
                this.evictEntries(entries, evictionRate);
            }
        }
    }

    void maybeNotifyTopic(Object key, Object value, Object version) {
        if (this.topic != null) {
            this.topic.publish(this.createMessage(key, value, version));
        }
    }

    private int calculateEvictionRate(int diff, int maxSize) {
        return diff >= 0 ? diff + (int)((float)maxSize * 0.2f) : 0;
    }

    private MessageListener<Object> createMessageListener() {
        return new MessageListener<Object>(){

            public void onMessage(Message<Object> message) {
                if (message.getPublishingMember() == null || LocalRegionCache.this.hazelcastInstance == null || !message.getPublishingMember().equals(LocalRegionCache.this.hazelcastInstance.getCluster().getLocalMember())) {
                    LocalRegionCache.this.maybeInvalidate(message.getMessageObject());
                }
            }
        };
    }

    private void evictEntries(List<EvictionEntry> entries, int evictionRate) {
        entries.sort(null);
        int removed = 0;
        for (EvictionEntry entry : entries) {
            if (!this.cache.remove(entry.key, entry.value) || ++removed != evictionRate) continue;
            break;
        }
    }

    private Comparator findVersionComparator(DomainDataRegionConfig regionConfig) {
        if (regionConfig == null) {
            return null;
        }
        for (EntityDataCachingConfig entityConfig : regionConfig.getEntityCaching()) {
            if (!entityConfig.isVersioned()) continue;
            try {
                return (Comparator)entityConfig.getVersionComparatorAccess().get();
            }
            catch (Throwable throwable) {
                this.log.warning("Unable to get version comparator", throwable);
                return null;
            }
        }
        for (CollectionDataCachingConfig collectionConfig : regionConfig.getCollectionCaching()) {
            if (!collectionConfig.isVersioned()) continue;
            return collectionConfig.getOwnerVersionComparator();
        }
        return null;
    }

    private void maybeInvalidateVersionedEntity(Object key, Expirable value, Object newVersion) {
        if (newVersion == null) {
            this.cache.remove(key);
        } else {
            AbstractReadWriteAccess.Lockable cachedItem = (AbstractReadWriteAccess.Lockable)value.getValue();
            if (cachedItem.isWriteable(this.nextTimestamp(), newVersion, this.versionComparator)) {
                this.cache.remove(key, value);
            }
        }
    }

    private List<EvictionEntry> searchEvictableEntries(long timeToLive, boolean limitSize) {
        ArrayList<EvictionEntry> entries = null;
        Iterator iter = this.cache.entrySet().iterator();
        long now = this.nextTimestamp();
        while (iter.hasNext()) {
            Map.Entry e = iter.next();
            Object k = e.getKey();
            Expirable expirable = (Expirable)e.getValue();
            if (expirable instanceof ExpiryMarker) continue;
            Value v = (Value)expirable;
            if (timeToLive > 0L && v.getTimestamp() + timeToLive < now) {
                iter.remove();
                continue;
            }
            if (!limitSize) continue;
            if (entries == null) {
                entries = new ArrayList<EvictionEntry>(this.cache.size());
            }
            entries.add(new EvictionEntry(k, v));
        }
        return entries;
    }

    public static interface EvictionConfig {
        public Duration getTimeToLive();

        public int getMaxSize();

        public static EvictionConfig create(final MapConfig mapConfig) {
            return new EvictionConfig(){

                @Override
                public Duration getTimeToLive() {
                    return mapConfig == null ? Duration.ofMillis(CacheEnvironment.getDefaultCacheTimeoutInMillis()) : Duration.ofSeconds(mapConfig.getTimeToLiveSeconds());
                }

                @Override
                public int getMaxSize() {
                    return mapConfig == null ? 100000 : mapConfig.getMaxSizeConfig().getSize();
                }
            };
        }
    }

    private static final class EvictionEntry
    implements Comparable<EvictionEntry> {
        final Object key;
        final Value value;

        private EvictionEntry(Object key, Value value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int compareTo(EvictionEntry o) {
            long thisVal = this.value.getTimestamp();
            long anotherVal = o.value.getTimestamp();
            return Long.compare(thisVal, anotherVal);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            EvictionEntry that = (EvictionEntry)o;
            return (this.key == null ? that.key == null : this.key.equals(that.key)) && (this.value == null ? that.value == null : this.value.equals(that.value));
        }

        public int hashCode() {
            return this.key == null ? 0 : this.key.hashCode();
        }
    }
}

