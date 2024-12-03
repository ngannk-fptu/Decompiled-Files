/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.terracotta.toolkit.cache.ToolkitCacheListener
 *  org.terracotta.toolkit.cluster.ClusterNode
 *  org.terracotta.toolkit.collections.ToolkitMap
 *  org.terracotta.toolkit.concurrent.locks.ToolkitLock
 *  org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock
 *  org.terracotta.toolkit.config.Configuration
 *  org.terracotta.toolkit.feature.NonStopFeature
 *  org.terracotta.toolkit.internal.cache.BufferingToolkitCache
 *  org.terracotta.toolkit.internal.cache.ToolkitValueComparator
 *  org.terracotta.toolkit.internal.cache.VersionUpdateListener
 *  org.terracotta.toolkit.internal.cache.VersionedValue
 *  org.terracotta.toolkit.nonstop.NonStopException
 *  org.terracotta.toolkit.search.QueryBuilder
 *  org.terracotta.toolkit.search.attribute.ToolkitAttributeExtractor
 */
package org.terracotta.modules.ehcache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.NonstopConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.modules.ehcache.wan.Watchable;
import org.terracotta.toolkit.cache.ToolkitCacheListener;
import org.terracotta.toolkit.cluster.ClusterNode;
import org.terracotta.toolkit.collections.ToolkitMap;
import org.terracotta.toolkit.concurrent.locks.ToolkitLock;
import org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock;
import org.terracotta.toolkit.config.Configuration;
import org.terracotta.toolkit.feature.NonStopFeature;
import org.terracotta.toolkit.internal.cache.BufferingToolkitCache;
import org.terracotta.toolkit.internal.cache.ToolkitValueComparator;
import org.terracotta.toolkit.internal.cache.VersionUpdateListener;
import org.terracotta.toolkit.internal.cache.VersionedValue;
import org.terracotta.toolkit.nonstop.NonStopException;
import org.terracotta.toolkit.search.QueryBuilder;
import org.terracotta.toolkit.search.attribute.ToolkitAttributeExtractor;

public class WanAwareToolkitCache<K, V>
implements BufferingToolkitCache<K, V>,
Watchable {
    private static final Logger LOGGER = LoggerFactory.getLogger(WanAwareToolkitCache.class);
    private static final String CACHE_ACTIVE_KEY = "WAN-CACHE-ACTIVE";
    private static final String ORCHESTRATOR_ALIVE_KEY = "ORCHESTRATOR-ALIVE";
    private static final String ORCHESTRATOR_MODE = "ORCHESTRATOR-MODE";
    private static final String REPLICATION_MODE = "REPLICATION-MODE";
    private final BufferingToolkitCache<K, V> delegate;
    private final ConcurrentMap<String, Serializable> configMap;
    private final NonStopFeature nonStop;
    private final ToolkitLock configMapLock;
    private final ToolkitLock activeLock;
    private final CacheConfiguration cacheConfiguration;
    private final boolean masterCache;
    private final boolean bidirectional;

    public WanAwareToolkitCache(BufferingToolkitCache<K, V> delegate, ToolkitMap<String, Serializable> configMap, NonStopFeature nonStop, ToolkitLock activeLock, CacheConfiguration cacheConfiguration, boolean masterCache, boolean bidirectional) {
        this(delegate, (ConcurrentMap<String, Serializable>)configMap, nonStop, configMap.getReadWriteLock().writeLock(), activeLock, cacheConfiguration, masterCache, bidirectional);
    }

    WanAwareToolkitCache(BufferingToolkitCache<K, V> delegate, ConcurrentMap<String, Serializable> configMap, NonStopFeature nonStop, ToolkitLock configMapLock, ToolkitLock activeLock, CacheConfiguration cacheConfiguration, boolean masterCache, boolean bidirectional) {
        this.delegate = delegate;
        this.configMap = configMap;
        this.nonStop = nonStop;
        this.configMapLock = configMapLock;
        this.activeLock = activeLock;
        this.cacheConfiguration = cacheConfiguration;
        this.masterCache = masterCache;
        this.bidirectional = bidirectional;
        configMap.putIfAbsent(CACHE_ACTIVE_KEY, Boolean.valueOf(false));
        configMap.putIfAbsent(ORCHESTRATOR_ALIVE_KEY, Boolean.valueOf(false));
        configMap.putIfAbsent(ORCHESTRATOR_MODE, (Serializable)((Object)(masterCache ? "Master" : "Replica")));
        configMap.putIfAbsent(REPLICATION_MODE, (Serializable)((Object)(bidirectional ? "BIDIRECTIONAL" : "UNIDIRECTIONAL")));
    }

    public boolean isReady() {
        Boolean active = (Boolean)this.configMap.get(CACHE_ACTIVE_KEY);
        if (this.isMasterCache()) {
            return active != null && active != false;
        }
        return active != null && active != false && (this.isOrchestratorAlive() || !this.bidirectional);
    }

    public boolean activate() {
        boolean updated = this.setState(true);
        this.notifyClients();
        return updated;
    }

    public boolean deactivate() {
        return this.setState(false);
    }

    private boolean setState(boolean active) {
        return this.configMap.replace(CACHE_ACTIVE_KEY, Boolean.valueOf(!active), Boolean.valueOf(active));
    }

    public Map<Object, Set<ClusterNode>> getNodesWithKeys(Set portableKeys) {
        this.waitIfRequired();
        return this.delegate.getNodesWithKeys(portableKeys);
    }

    public void unlockedPutNoReturn(K k, V v, int createTime, int customTTI, int customTTL) {
        this.waitIfRequired();
        this.delegate.unlockedPutNoReturn(k, v, createTime, customTTI, customTTL);
    }

    public void unlockedRemoveNoReturn(Object k) {
        this.waitIfRequired();
        this.delegate.unlockedRemoveNoReturn(k);
    }

    public V unlockedGet(Object k, boolean quiet) {
        this.waitIfRequired();
        return (V)this.delegate.unlockedGet(k, quiet);
    }

    public Map<K, V> unlockedGetAll(Collection<K> keys, boolean quiet) {
        this.waitIfRequired();
        return this.delegate.unlockedGetAll(keys, quiet);
    }

    public void removeAll(Set<K> keys) {
        this.waitIfRequired();
        this.delegate.removeAll(keys);
    }

    public V put(K key, V value, int createTimeInSecs, int customMaxTTISeconds, int customMaxTTLSeconds) {
        this.waitIfRequired();
        return (V)this.delegate.put(key, value, createTimeInSecs, customMaxTTISeconds, customMaxTTLSeconds);
    }

    public V putIfAbsent(K key, V value, long createTimeInSecs, int maxTTISeconds, int maxTTLSeconds) {
        this.waitIfRequired();
        return (V)this.delegate.putIfAbsent(key, value, createTimeInSecs, maxTTISeconds, maxTTLSeconds);
    }

    public void putNoReturn(K key, V value, long createTimeInSecs, int maxTTISeconds, int maxTTLSeconds) {
        this.waitIfRequired();
        this.delegate.putNoReturn(key, value, createTimeInSecs, maxTTISeconds, maxTTLSeconds);
    }

    public Map<K, V> getAllQuiet(Collection<K> keys) {
        this.waitIfRequired();
        return this.delegate.getAllQuiet(keys);
    }

    public V getQuiet(Object key) {
        this.waitIfRequired();
        return (V)this.delegate.getQuiet(key);
    }

    public Map<K, V> getAll(Collection<? extends K> keys) {
        this.waitIfRequired();
        return this.delegate.getAll(keys);
    }

    public void putNoReturn(K key, V value) {
        this.waitIfRequired();
        this.delegate.putNoReturn(key, value);
    }

    public void removeNoReturn(Object key) {
        this.waitIfRequired();
        this.delegate.removeNoReturn(key);
    }

    public V putIfAbsent(K key, V value) {
        this.waitIfRequired();
        return (V)this.delegate.putIfAbsent(key, value);
    }

    public boolean remove(Object key, Object value) {
        this.waitIfRequired();
        return this.delegate.remove(key, value);
    }

    public boolean replace(K key, V oldValue, V newValue) {
        this.waitIfRequired();
        return this.delegate.replace(key, oldValue, newValue);
    }

    public V replace(K key, V value) {
        this.waitIfRequired();
        return (V)this.delegate.replace(key, value);
    }

    public int size() {
        this.waitIfRequired();
        return this.delegate.size();
    }

    public int quickSize() {
        this.waitIfRequired();
        return this.delegate.quickSize();
    }

    public boolean isEmpty() {
        this.waitIfRequired();
        return this.delegate.isEmpty();
    }

    public boolean containsKey(Object key) {
        this.waitIfRequired();
        return this.delegate.containsKey(key);
    }

    public boolean containsValue(Object value) {
        this.waitIfRequired();
        return this.delegate.containsValue(value);
    }

    public V get(Object key) {
        this.waitIfRequired();
        return (V)this.delegate.get(key);
    }

    public V put(K key, V value) {
        this.waitIfRequired();
        return (V)this.delegate.put(key, value);
    }

    public V remove(Object key) {
        this.waitIfRequired();
        return (V)this.delegate.remove(key);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        this.waitIfRequired();
        this.delegate.putAll(m);
    }

    public void clear() {
        this.waitIfRequired();
        this.delegate.clear();
    }

    public void quickClear() {
        this.waitIfRequired();
        this.delegate.quickClear();
    }

    public Set<K> keySet() {
        this.waitIfRequired();
        return this.delegate.keySet();
    }

    public Collection<V> values() {
        this.waitIfRequired();
        return this.delegate.values();
    }

    public Set<Map.Entry<K, V>> entrySet() {
        this.waitIfRequired();
        return this.delegate.entrySet();
    }

    public void destroy() {
        this.waitIfRequired();
        this.delegate.destroy();
    }

    public boolean remove(Object key, Object value, ToolkitValueComparator<V> comparator) {
        this.waitIfRequired();
        return this.delegate.remove(key, value, comparator);
    }

    public boolean replace(K key, V oldValue, V newValue, ToolkitValueComparator<V> comparator) {
        this.waitIfRequired();
        return this.delegate.replace(key, oldValue, newValue, comparator);
    }

    public boolean equals(Object o) {
        return this.delegate.equals(o);
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    public boolean isDestroyed() {
        return this.delegate.isDestroyed();
    }

    public void setAttributeExtractor(ToolkitAttributeExtractor<K, V> attrExtractor) {
        this.delegate.setAttributeExtractor(attrExtractor);
    }

    public QueryBuilder createQueryBuilder() {
        return this.delegate.createQueryBuilder();
    }

    public boolean isBulkLoadEnabled() {
        return this.delegate.isBulkLoadEnabled();
    }

    public boolean isNodeBulkLoadEnabled() {
        return this.delegate.isNodeBulkLoadEnabled();
    }

    public void setNodeBulkLoadEnabled(boolean enabledBulkLoad) {
        this.delegate.setNodeBulkLoadEnabled(enabledBulkLoad);
    }

    public void waitUntilBulkLoadComplete() throws InterruptedException {
        this.delegate.waitUntilBulkLoadComplete();
    }

    public String getName() {
        return this.delegate.getName();
    }

    public void clearLocalCache() {
        this.delegate.clearLocalCache();
    }

    public V unsafeLocalGet(Object key) {
        return (V)this.delegate.unsafeLocalGet(key);
    }

    public boolean containsLocalKey(Object key) {
        return this.delegate.containsLocalKey(key);
    }

    public int localSize() {
        return this.delegate.localSize();
    }

    public Set<K> localKeySet() {
        return this.delegate.localKeySet();
    }

    public long localOnHeapSizeInBytes() {
        return this.delegate.localOnHeapSizeInBytes();
    }

    public long localOffHeapSizeInBytes() {
        return this.delegate.localOffHeapSizeInBytes();
    }

    public int localOnHeapSize() {
        return this.delegate.localOnHeapSize();
    }

    public int localOffHeapSize() {
        return this.delegate.localOffHeapSize();
    }

    public boolean containsKeyLocalOnHeap(Object key) {
        return this.delegate.containsKeyLocalOnHeap(key);
    }

    public boolean containsKeyLocalOffHeap(Object key) {
        return this.delegate.containsKeyLocalOffHeap(key);
    }

    public void disposeLocally() {
        this.delegate.disposeLocally();
    }

    public ToolkitReadWriteLock createLockForKey(K key) {
        return this.delegate.createLockForKey(key);
    }

    public void setConfigField(String name, Serializable value) {
        this.delegate.setConfigField(name, value);
    }

    public Configuration getConfiguration() {
        return this.delegate.getConfiguration();
    }

    public void addListener(ToolkitCacheListener<K> listener) {
        this.delegate.addListener(listener);
    }

    public void putIfAbsentVersioned(K key, V value, long version) {
        this.delegate.putIfAbsentVersioned(key, value, version);
    }

    public void putVersioned(K key, V value, long version) {
        this.delegate.putVersioned(key, value, version);
    }

    public void putVersioned(K key, V value, long version, int createTimeInSecs, int customMaxTTISeconds, int customMaxTTLSeconds) {
        this.delegate.putVersioned(key, value, version, createTimeInSecs, customMaxTTISeconds, customMaxTTLSeconds);
    }

    public void putIfAbsentVersioned(K key, V value, long version, int createTimeInSecs, int customMaxTTISeconds, int customMaxTTLSeconds) {
        this.delegate.putIfAbsentVersioned(key, value, version, createTimeInSecs, customMaxTTISeconds, customMaxTTLSeconds);
    }

    public void unlockedPutNoReturnVersioned(K k, V v, long version, int createTime, int customTTI, int customTTL) {
        this.delegate.unlockedPutNoReturnVersioned(k, v, version, createTime, customTTI, customTTL);
    }

    public void removeVersioned(Object key, long version) {
        this.delegate.removeVersioned(key, version);
    }

    public void registerVersionUpdateListener(VersionUpdateListener listener) {
        this.delegate.registerVersionUpdateListener(listener);
    }

    public void unregisterVersionUpdateListener(VersionUpdateListener listener) {
        this.delegate.unregisterVersionUpdateListener(listener);
    }

    public Set<K> keySetForSegment(int segmentIndex) {
        return this.delegate.keySetForSegment(segmentIndex);
    }

    public VersionedValue<V> getVersionedValue(Object key) {
        return this.delegate.getVersionedValue(key);
    }

    public Map<K, VersionedValue<V>> getAllVersioned(Collection<K> keys) {
        return this.delegate.getAllVersioned(keys);
    }

    public void removeListener(ToolkitCacheListener<K> listener) {
        this.delegate.removeListener(listener);
    }

    public void unlockedRemoveNoReturnVersioned(Object key, long version) {
        this.delegate.unlockedRemoveNoReturnVersioned(key, version);
    }

    public void startBuffering() {
        this.delegate.startBuffering();
    }

    public boolean isBuffering() {
        return this.delegate.isBuffering();
    }

    public void stopBuffering() {
        this.delegate.stopBuffering();
    }

    public void flushBuffer() {
        this.delegate.flushBuffer();
    }

    public void clearVersioned() {
        this.delegate.clearVersioned();
    }

    private void waitIfRequired() {
        this.checkImmediateTimeout();
        if (!this.isReady()) {
            LOGGER.info("Cache '{}' not active. Waiting for the Orchestrator to mark it active", (Object)this.delegate.getName());
            this.waitUntilActive();
            LOGGER.info("Cache '{}' is now active", (Object)this.delegate.getName());
        }
    }

    void waitUntilActive() {
        boolean interrupted = false;
        this.configMapLock.lock();
        try {
            while (!this.isReady()) {
                this.checkImmediateTimeout();
                try {
                    this.configMapLock.getCondition().await();
                }
                catch (InterruptedException e) {
                    if (this.nonStop.isTimedOut()) {
                        LOGGER.error("Operation timed-out while waiting for the cache '{}' to become active", (Object)this.delegate.getName());
                        throw new NonStopException("Cache '" + this.delegate.getName() + "' not active currently.");
                    }
                    interrupted = true;
                }
            }
        }
        finally {
            this.configMapLock.unlock();
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void checkImmediateTimeout() {
        if (this.isImmediateNonStopTimeout() && !this.isMasterCache() && !this.isOrchestratorAlive() && this.bidirectional) {
            throw new NonStopException("Orchestrator for cache '" + this.name() + "' is not alive.");
        }
    }

    private boolean isMasterCache() {
        return this.masterCache;
    }

    boolean isOrchestratorAlive() {
        Boolean orchestratorLive = (Boolean)this.configMap.get(ORCHESTRATOR_ALIVE_KEY);
        return orchestratorLive != null && orchestratorLive != false;
    }

    void notifyClients() {
        this.configMapLock.lock();
        try {
            this.configMapLock.getCondition().signalAll();
        }
        finally {
            this.configMapLock.unlock();
        }
    }

    public void setUnlimitedCapacity() {
        LOGGER.info("Setting cache '{}' to be unlimited as it is a Replica.", (Object)this.delegate.getName());
        this.setConfigField("maxTTLSeconds", Integer.valueOf(0));
        this.setConfigField("maxTTISeconds", Integer.valueOf(0));
    }

    @Override
    public void goLive() {
        int sleepTime = 1 + (int)(Math.random() * 3.0);
        while (!this.activeLock.isHeldByCurrentThread()) {
            try {
                TimeUnit.SECONDS.sleep(sleepTime);
                this.activeLock.lock();
                this.markOrchestratorAlive();
            }
            catch (Exception e) {
                LOGGER.error("Exception occurred while waiting for active lock for cache '{}'", (Object)this.getName(), (Object)e);
            }
        }
    }

    @Override
    public void die() {
    }

    boolean markOrchestratorDead() {
        if (this.configMap.replace(ORCHESTRATOR_ALIVE_KEY, Boolean.valueOf(true), Boolean.valueOf(false))) {
            this.notifyClients();
            if (this.bidirectional) {
                LOGGER.error("Orchestrator is not running for cache '{}'. Marking it as dead.", (Object)this.getName());
            } else {
                LOGGER.warn("Orchestrator is not running for cache '{}'. Cache remains operational, but it won't receive any subsequent updates over WAN.", (Object)this.getName());
            }
            return true;
        }
        return false;
    }

    void markOrchestratorAlive() {
        this.configMap.put(ORCHESTRATOR_ALIVE_KEY, Boolean.valueOf(true));
        this.notifyClients();
    }

    @Override
    public boolean probeLiveness() {
        if (this.activeLock.tryLock()) {
            try {
                this.markOrchestratorDead();
                boolean bl = false;
                return bl;
            }
            finally {
                this.activeLock.unlock();
            }
        }
        return true;
    }

    @Override
    public String name() {
        return this.getName();
    }

    private boolean isImmediateNonStopTimeout() {
        if (this.cacheConfiguration.getTerracottaConfiguration() == null) {
            return false;
        }
        NonstopConfiguration nonstopConfig = this.cacheConfiguration.getTerracottaConfiguration().getNonstopConfiguration();
        return nonstopConfig != null && nonstopConfig.isImmediateTimeout();
    }
}

