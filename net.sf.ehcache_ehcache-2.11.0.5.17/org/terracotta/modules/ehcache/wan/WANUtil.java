/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.terracotta.toolkit.concurrent.locks.ToolkitLock
 */
package org.terracotta.modules.ehcache.wan;

import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.wan.IllegalConfigurationException;
import org.terracotta.toolkit.concurrent.locks.ToolkitLock;

public class WANUtil {
    private static final int WAIT_INTERVAL_FOR_ORCHESTRATOR_IN_SECONDS = 60;
    private static final Logger LOGGER = LoggerFactory.getLogger(WANUtil.class);
    private static final String WAN_PREFIX = "__WAN__";
    private static final String LOCK_PREFIX = "__WAN__LOCK";
    private static final String WAN_ENABLED_CACHE_ENTRY = "__WAN__ENABLED_CACHE";
    private static final String REPLICA_CACHE_FLAG = "IS_REPLICA";
    private static final String META_DATA_AVAILABLE_FLAG = "WAN_META_DATA_AVAILABLE";
    private static final String BIDIRECTIONAL_FLAG = "IS_BIDIRECTIONAL";
    private static final String WAN_CURRENT_ORCHESTRATOR = "__WAN__CURRENT_ORCHESTRATOR";
    private final ToolkitInstanceFactory factory;

    public WANUtil(ToolkitInstanceFactory factory) {
        this.factory = factory;
    }

    public void markWANReady(String cacheManagerName) {
        this.getCacheManagerConfigMap(cacheManagerName).put(META_DATA_AVAILABLE_FLAG, Boolean.TRUE);
        this.notifyClients(cacheManagerName);
    }

    public void clearWANReady(String cacheManagerName) {
        this.getCacheManagerConfigMap(cacheManagerName).put(META_DATA_AVAILABLE_FLAG, Boolean.FALSE);
    }

    public boolean isWANReady(String cacheManagerName) {
        Boolean value = (Boolean)this.getCacheManagerConfigMap(cacheManagerName).get(META_DATA_AVAILABLE_FLAG);
        return value == null ? false : value;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void waitForOrchestrator(String cacheManagerName) {
        if (!this.isWANReady(cacheManagerName)) {
            LOGGER.info("Waiting for the Orchestrator...");
            ToolkitLock toolkitLock = this.factory.getToolkit().getLock(LOCK_PREFIX + cacheManagerName);
            toolkitLock.lock();
            try {
                while (!this.isWANReady(cacheManagerName)) {
                    try {
                        boolean orchRunning = toolkitLock.getCondition().await(60L, TimeUnit.SECONDS);
                        if (orchRunning) continue;
                        LOGGER.error("No Orchestrator Running. We can not proceed further without an Orchestrator.");
                    }
                    catch (InterruptedException e) {
                        LOGGER.warn("Interrupted while waiting for the Orchestrator to be running.", (Throwable)e);
                    }
                }
            }
            finally {
                toolkitLock.unlock();
            }
        }
        LOGGER.info("Orchestrator is available for the CacheManager '{}'", (Object)cacheManagerName);
    }

    public void markCacheWanEnabled(String cacheManagerName, String cacheName) {
        ConcurrentMap<String, Serializable> cacheConfigMap = this.getCacheConfigMap(cacheManagerName, cacheName);
        Boolean existingValue = (Boolean)cacheConfigMap.putIfAbsent(WAN_ENABLED_CACHE_ENTRY, Boolean.TRUE);
        if (existingValue != null && existingValue.equals(Boolean.FALSE)) {
            LOGGER.error("A Client with cache '{}' exists with non WAN configuration. Please check your client's ehcache.xml and add 'wanEnabledTSA = true'", (Object)cacheName);
            throw new IllegalConfigurationException("Cache '" + cacheName + "' is already marked as disabled for WAN");
        }
        LOGGER.info("Marked the cache '{}' wan enabled for CacheManager '{}'", (Object)cacheName, (Object)cacheManagerName);
    }

    public void markCacheAsReplica(String cacheManagerName, String cacheName) {
        ConcurrentMap<String, Serializable> cacheConfigMap = this.getCacheConfigMap(cacheManagerName, cacheName);
        cacheConfigMap.put(REPLICA_CACHE_FLAG, Boolean.TRUE);
        LOGGER.info("Cache '{}' in CacheManager '{}' has been marked as a Replica", (Object)cacheName, (Object)cacheManagerName);
    }

    public void markCacheAsMaster(String cacheManagerName, String cacheName) {
        ConcurrentMap<String, Serializable> cacheConfigMap = this.getCacheConfigMap(cacheManagerName, cacheName);
        cacheConfigMap.put(REPLICA_CACHE_FLAG, Boolean.FALSE);
        LOGGER.info("Cache '{}' in CacheManager '{}' has been marked as a Master", (Object)cacheName, (Object)cacheManagerName);
    }

    public boolean isCacheReplica(String cacheManagerName, String cacheName) {
        if (cacheName == null || cacheManagerName == null) {
            throw new IllegalArgumentException("Invalid arguments: CacheManagerName- " + cacheManagerName + " and CacheName- " + cacheName);
        }
        return (Boolean)this.getCacheConfigMap(cacheManagerName, cacheName).get(REPLICA_CACHE_FLAG);
    }

    public void markCacheAsBidirectional(String cacheManagerName, String cacheName) {
        ConcurrentMap<String, Serializable> cacheConfigMap = this.getCacheConfigMap(cacheManagerName, cacheName);
        cacheConfigMap.put(BIDIRECTIONAL_FLAG, Boolean.TRUE);
        LOGGER.info("Cache '{}' in CacheManager '{}' has been marked as BIDIRECTIONAL", (Object)cacheName, (Object)cacheManagerName);
    }

    public void markCacheAsUnidirectional(String cacheManagerName, String cacheName) {
        ConcurrentMap<String, Serializable> cacheConfigMap = this.getCacheConfigMap(cacheManagerName, cacheName);
        cacheConfigMap.put(BIDIRECTIONAL_FLAG, Boolean.FALSE);
        LOGGER.info("Cache '{}' in CacheManager '{}' has been marked as UNIDIRECTIONAL", (Object)cacheName, (Object)cacheManagerName);
    }

    public void addCurrentOrchestrator(String cacheManagerName, String cacheName, String orchestrator) {
        ConcurrentMap<String, Serializable> cacheConfigMap = this.getCacheConfigMap(cacheManagerName, cacheName);
        cacheConfigMap.put(WAN_CURRENT_ORCHESTRATOR, (Serializable)((Object)orchestrator));
        LOGGER.info("Added '{}' as orchestrator for Cache '{}' in CacheManager '{}'", new Object[]{orchestrator, cacheName, cacheManagerName});
    }

    public boolean isCacheBidirectional(String cacheManagerName, String cacheName) {
        if (cacheName == null || cacheManagerName == null) {
            throw new IllegalArgumentException("Invalid arguments: CacheManagerName- " + cacheManagerName + " and CacheName- " + cacheName);
        }
        return (Boolean)this.getCacheConfigMap(cacheManagerName, cacheName).get(BIDIRECTIONAL_FLAG);
    }

    public void markCacheWanDisabled(String cacheManagerName, String cacheName) {
        ConcurrentMap<String, Serializable> cacheConfigMap = this.getCacheConfigMap(cacheManagerName, cacheName);
        Boolean existingValue = (Boolean)cacheConfigMap.putIfAbsent(WAN_ENABLED_CACHE_ENTRY, Boolean.FALSE);
        if (existingValue != null && existingValue.equals(Boolean.TRUE)) {
            LOGGER.error("A WAN Orchestrator already exists for cache '{}'. This client should be wan-enabled. Please check your client's ehcache.xml and add 'wanEnabledTSA = true'", (Object)cacheName);
            throw new IllegalConfigurationException("Cache '" + cacheName + "' is already marked as enabled for WAN");
        }
        LOGGER.debug("Marked the cache '{}' wan disabled for CacheManager '{}'", (Object)cacheName, (Object)cacheManagerName);
    }

    public boolean isWanEnabledCache(String cacheManagerName, String cacheName) {
        if (cacheName == null || cacheManagerName == null) {
            throw new IllegalArgumentException("Invalid arguments: CacheManagerName- " + cacheManagerName + " and CacheName- " + cacheName);
        }
        Boolean value = (Boolean)this.getCacheConfigMap(cacheManagerName, cacheName).get(WAN_ENABLED_CACHE_ENTRY);
        return value == null ? false : value;
    }

    public void cleanUpCacheMetaData(String cacheManagerName, String cacheName) {
        ConcurrentMap<String, Serializable> cacheConfigMap = this.getCacheConfigMap(cacheManagerName, cacheName);
        cacheConfigMap.remove(WAN_ENABLED_CACHE_ENTRY);
        cacheConfigMap.remove(REPLICA_CACHE_FLAG);
        LOGGER.info("Cleaned up the metadata for cache '{}' for CacheManager '{}'", (Object)cacheName, (Object)cacheManagerName);
    }

    void notifyClients(String cacheManagerName) {
        ToolkitLock toolkitLock = this.factory.getToolkit().getLock(LOCK_PREFIX + cacheManagerName);
        toolkitLock.lock();
        try {
            toolkitLock.getCondition().signalAll();
        }
        finally {
            toolkitLock.unlock();
        }
    }

    ConcurrentMap<String, Serializable> getCacheConfigMap(String cacheManagerName, String cacheName) {
        return this.factory.getOrCreateClusteredStoreConfigMap(cacheManagerName, cacheName);
    }

    ConcurrentMap<String, Serializable> getCacheManagerConfigMap(String cacheManagerName) {
        return this.factory.getOrCreateCacheManagerMetaInfoMap(cacheManagerName);
    }
}

