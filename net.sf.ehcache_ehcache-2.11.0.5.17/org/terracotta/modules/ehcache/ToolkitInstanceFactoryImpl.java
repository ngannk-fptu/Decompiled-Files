/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.terracotta.toolkit.Toolkit
 *  org.terracotta.toolkit.ToolkitFeatureType
 *  org.terracotta.toolkit.ToolkitObjectType
 *  org.terracotta.toolkit.builder.ToolkitCacheConfigBuilder
 *  org.terracotta.toolkit.builder.ToolkitStoreConfigBuilder
 *  org.terracotta.toolkit.cache.ToolkitCache
 *  org.terracotta.toolkit.collections.ToolkitMap
 *  org.terracotta.toolkit.concurrent.locks.ToolkitLock
 *  org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock
 *  org.terracotta.toolkit.config.Configuration
 *  org.terracotta.toolkit.events.ToolkitNotifier
 *  org.terracotta.toolkit.feature.NonStopFeature
 *  org.terracotta.toolkit.internal.ToolkitInternal
 *  org.terracotta.toolkit.internal.ToolkitLogger
 *  org.terracotta.toolkit.internal.cache.BufferingToolkitCache
 *  org.terracotta.toolkit.internal.cache.ToolkitCacheInternal
 *  org.terracotta.toolkit.internal.collections.ToolkitListInternal
 *  org.terracotta.toolkit.nonstop.NonStopConfiguration
 *  org.terracotta.toolkit.nonstop.NonStopConfigurationRegistry
 *  org.terracotta.toolkit.store.ToolkitConfigFields$Consistency
 */
package org.terracotta.modules.ehcache;

import com.terracotta.entity.ClusteredEntityManager;
import com.terracotta.entity.ehcache.ClusteredCache;
import com.terracotta.entity.ehcache.ClusteredCacheConfiguration;
import com.terracotta.entity.ehcache.ClusteredCacheManager;
import com.terracotta.entity.ehcache.ClusteredCacheManagerConfiguration;
import com.terracotta.entity.ehcache.EhcacheEntitiesNaming;
import com.terracotta.entity.ehcache.ToolkitBackedClusteredCache;
import com.terracotta.entity.ehcache.ToolkitBackedClusteredCacheManager;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.config.TerracottaClientConfiguration;
import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.config.generator.ConfigurationUtil;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.transaction.Decision;
import net.sf.ehcache.transaction.TransactionID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.modules.ehcache.TerracottaToolkitBuilder;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.WanAwareToolkitCache;
import org.terracotta.modules.ehcache.async.AsyncConfig;
import org.terracotta.modules.ehcache.collections.SerializationHelper;
import org.terracotta.modules.ehcache.collections.SerializedToolkitCache;
import org.terracotta.modules.ehcache.event.CacheDisposalNotification;
import org.terracotta.modules.ehcache.event.CacheEventNotificationMsg;
import org.terracotta.modules.ehcache.store.CacheConfigChangeNotificationMsg;
import org.terracotta.modules.ehcache.store.ToolkitNonStopConfiguration;
import org.terracotta.modules.ehcache.store.nonstop.ToolkitNonstopDisableConfig;
import org.terracotta.modules.ehcache.transaction.ClusteredSoftLockIDKey;
import org.terracotta.modules.ehcache.transaction.SerializedReadCommittedClusteredSoftLock;
import org.terracotta.modules.ehcache.wan.WANUtil;
import org.terracotta.modules.ehcache.wan.Watchable;
import org.terracotta.modules.ehcache.wan.Watchdog;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.ToolkitFeatureType;
import org.terracotta.toolkit.ToolkitObjectType;
import org.terracotta.toolkit.builder.ToolkitCacheConfigBuilder;
import org.terracotta.toolkit.builder.ToolkitStoreConfigBuilder;
import org.terracotta.toolkit.cache.ToolkitCache;
import org.terracotta.toolkit.collections.ToolkitMap;
import org.terracotta.toolkit.concurrent.locks.ToolkitLock;
import org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock;
import org.terracotta.toolkit.events.ToolkitNotifier;
import org.terracotta.toolkit.feature.NonStopFeature;
import org.terracotta.toolkit.internal.ToolkitInternal;
import org.terracotta.toolkit.internal.ToolkitLogger;
import org.terracotta.toolkit.internal.cache.BufferingToolkitCache;
import org.terracotta.toolkit.internal.cache.ToolkitCacheInternal;
import org.terracotta.toolkit.internal.collections.ToolkitListInternal;
import org.terracotta.toolkit.nonstop.NonStopConfiguration;
import org.terracotta.toolkit.nonstop.NonStopConfigurationRegistry;
import org.terracotta.toolkit.store.ToolkitConfigFields;

public class ToolkitInstanceFactoryImpl
implements ToolkitInstanceFactory {
    public static final Logger LOGGER = LoggerFactory.getLogger(ToolkitInstanceFactoryImpl.class);
    private static final String CONFIG_LOGGER_NAME = "com.terracotta.ehcache.config";
    public static final String DELIMITER = "|";
    private static final String EVENT_NOTIFIER_SUFFIX = "event-notifier";
    private static final String DISPOSAL_NOTIFIER_SUFFIX = "disposal-notifier";
    private static final String EHCACHE_NAME_PREFIX = "__tc_clustered-ehcache";
    private static final String CONFIG_NOTIFIER_SUFFIX = "config-notifier";
    private static final String EHCACHE_TXNS_DECISION_STATE_MAP_NAME = "__tc_clustered-ehcache|txnsDecision";
    private static final String ALL_SOFT_LOCKS_MAP_SUFFIX = "softLocks";
    private static final String NEW_SOFT_LOCKS_LIST_SUFFIX = "newSoftLocks";
    private static final String LOCK_TAG = "::LOCK";
    static final String CLUSTERED_STORE_CONFIG_MAP = "__tc_clustered-ehcache|configMap";
    private static final String EHCACHE_TXNS_SOFTLOCK_WRITE_LOCK_NAME = "__tc_clustered-ehcache|softWriteLock";
    private static final String EHCACHE_TXNS_SOFTLOCK_FREEZE_LOCK_NAME = "__tc_clustered-ehcache|softFreezeLock";
    private static final String EHCACHE_TXNS_SOFTLOCK_NOTIFIER_LOCK_NAME = "__tc_clustered-ehcache|softNotifierLock";
    public static final int RETRY_MARK_IN_USE_AFTER_REJOIN = 5;
    protected final Toolkit toolkit;
    private final WANUtil wanUtil;
    private final ClusteredEntityManager clusteredEntityManager;
    private volatile ClusteredCacheManager clusteredCacheManagerEntity;
    private final EntityNamesHolder entityNames;
    private final Watchdog wanWatchdog;

    public ToolkitInstanceFactoryImpl(TerracottaClientConfiguration terracottaClientConfiguration, String productId, String cacheManagerName, ClassLoader loader) {
        this.toolkit = ToolkitInstanceFactoryImpl.createTerracottaToolkit(terracottaClientConfiguration, productId, cacheManagerName, loader);
        this.updateDefaultNonStopConfig(this.toolkit);
        this.clusteredEntityManager = new ClusteredEntityManager(this.toolkit);
        this.entityNames = new EntityNamesHolder();
        this.wanUtil = new WANUtil(this);
        this.wanWatchdog = Watchdog.create();
    }

    public ToolkitInstanceFactoryImpl(TerracottaClientConfiguration terracottaClientConfiguration, String cacheManagerName, ClassLoader loader) {
        this(terracottaClientConfiguration, null, cacheManagerName, loader);
    }

    ToolkitInstanceFactoryImpl(Toolkit toolkit, ClusteredEntityManager clusteredEntityManager) {
        this.toolkit = toolkit;
        this.clusteredEntityManager = clusteredEntityManager;
        this.entityNames = new EntityNamesHolder();
        this.wanUtil = new WANUtil(this);
        this.wanWatchdog = Watchdog.create();
    }

    ToolkitInstanceFactoryImpl(Toolkit toolkit, ClusteredEntityManager clusteredEntityManager, WANUtil util, Watchdog wanWatchdog) {
        this.toolkit = toolkit;
        this.clusteredEntityManager = clusteredEntityManager;
        this.entityNames = new EntityNamesHolder();
        this.wanUtil = util;
        this.wanWatchdog = wanWatchdog;
    }

    private void updateDefaultNonStopConfig(Toolkit toolkitParam) {
        ToolkitNonstopDisableConfig disableNonStop = new ToolkitNonstopDisableConfig();
        NonStopConfigurationRegistry nonStopConfigurationRegistry = ((NonStopFeature)toolkitParam.getFeature(ToolkitFeatureType.NONSTOP)).getNonStopConfigurationRegistry();
        for (ToolkitObjectType t : ToolkitObjectType.values()) {
            try {
                nonStopConfigurationRegistry.registerForType((NonStopConfiguration)disableNonStop, new ToolkitObjectType[]{t});
            }
            catch (UnsupportedOperationException e) {
                if (t == ToolkitObjectType.BARRIER || t == ToolkitObjectType.BLOCKING_QUEUE) continue;
                throw e;
            }
        }
    }

    private static Toolkit createTerracottaToolkit(TerracottaClientConfiguration terracottaClientConfiguration, String productId, String cacheManagerName, ClassLoader loader) {
        TerracottaToolkitBuilder terracottaClientBuilder = new TerracottaToolkitBuilder();
        EhcacheTcConfig ehcacheTcConfig = EhcacheTcConfig.create(terracottaClientConfiguration);
        switch (ehcacheTcConfig.type) {
            case URL: {
                terracottaClientBuilder.setTCConfigUrl(ehcacheTcConfig.tcConfigUrlOrSnippet);
                break;
            }
            case EMBEDDED_TC_CONFIG: 
            case FILE: {
                terracottaClientBuilder.setTCConfigSnippet(ehcacheTcConfig.tcConfigUrlOrSnippet);
            }
        }
        terracottaClientBuilder.addTunnelledMBeanDomain("net.sf.ehcache");
        terracottaClientBuilder.addTunnelledMBeanDomain("org.terracotta.wan");
        terracottaClientBuilder.setRejoinEnabled(terracottaClientConfiguration.isRejoin());
        terracottaClientBuilder.setProductId(productId);
        terracottaClientBuilder.setClientName(cacheManagerName);
        terracottaClientBuilder.setClassLoader(loader);
        return terracottaClientBuilder.buildToolkit();
    }

    @Override
    public void waitForOrchestrator(String cacheManagerName) {
        this.wanUtil.waitForOrchestrator(cacheManagerName);
    }

    @Override
    public void markCacheWanDisabled(String cacheManagerName, String cacheName) {
        this.wanUtil.markCacheWanDisabled(cacheManagerName, cacheName);
    }

    @Override
    public Toolkit getToolkit() {
        return this.toolkit;
    }

    @Override
    public ToolkitCacheInternal<String, Serializable> getOrCreateToolkitCache(Ehcache cache) {
        String cacheManagerName = ToolkitInstanceFactoryImpl.getCacheManagerName(cache);
        String cacheName = cache.getName();
        Object toolkitCache = this.getOrCreateRegularToolkitCache(cacheManagerName, cacheName, cache.getCacheConfiguration());
        if (this.wanUtil.isWanEnabledCache(cacheManagerName, cacheName)) {
            boolean replicaCache = this.wanUtil.isCacheReplica(cacheManagerName, cacheName);
            boolean bidirectional = this.wanUtil.isCacheBidirectional(cacheManagerName, cacheName);
            toolkitCache = this.createWanAwareToolkitCache(cacheManagerName, cacheName, (ToolkitCacheInternal<String, Serializable>)toolkitCache, cache.getCacheConfiguration(), !replicaCache, bidirectional);
            if (replicaCache) {
                LOGGER.info("Pinning the Cache '{}' belonging to Cache Manager '{}' and setting its TTI and TTL values to zero as it is a WAN Replica Cache. This cache's capacity will be controlled by its Master cache.", (Object)cacheName, (Object)cacheManagerName);
                PinningConfiguration pinningConfiguration = new PinningConfiguration();
                pinningConfiguration.setStore(PinningConfiguration.Store.INCACHE.toString());
                cache.getCacheConfiguration().addPinning(pinningConfiguration);
                cache.getCacheConfiguration().setMaxEntriesInCache(0L);
                cache.getCacheConfiguration().setTimeToLiveSeconds(0L);
                cache.getCacheConfiguration().setTimeToIdleSeconds(0L);
            }
            cache.getCacheConfiguration().freezeConfiguration();
            this.wanWatchdog.watch((Watchable)toolkitCache);
        }
        return toolkitCache;
    }

    @Override
    public WanAwareToolkitCache<String, Serializable> getOrCreateWanAwareToolkitCache(String cacheManagerName, String cacheName, CacheConfiguration ehcacheConfig, boolean masterCache, boolean bidirectional) {
        ToolkitCacheInternal<String, Serializable> toolkitCache = this.getOrCreateRegularToolkitCache(cacheManagerName, cacheName, ehcacheConfig);
        return this.createWanAwareToolkitCache(cacheManagerName, cacheName, toolkitCache, ehcacheConfig, masterCache, bidirectional);
    }

    private WanAwareToolkitCache<String, Serializable> createWanAwareToolkitCache(String cacheManagerName, String cacheName, ToolkitCacheInternal<String, Serializable> toolkitCache, CacheConfiguration cacheConfiguration, boolean masterCache, boolean bidirectional) {
        String fullyQualifiedCacheName = EhcacheEntitiesNaming.getToolkitCacheNameFor(cacheManagerName, cacheName);
        ToolkitMap<String, Serializable> configMap = this.getOrCreateConfigMap(fullyQualifiedCacheName);
        return new WanAwareToolkitCache<String, Serializable>((BufferingToolkitCache)toolkitCache, configMap, (NonStopFeature)this.toolkit.getFeature(ToolkitFeatureType.NONSTOP), this.toolkit.getLock(toolkitCache.getName() + LOCK_TAG), cacheConfiguration, masterCache, bidirectional);
    }

    private ToolkitCacheInternal<String, Serializable> getOrCreateRegularToolkitCache(String cacheManagerName, String cacheName, CacheConfiguration ehcacheConfig) {
        org.terracotta.toolkit.config.Configuration toolkitCacheConfig = ToolkitInstanceFactoryImpl.createClusteredCacheConfig(ehcacheConfig, cacheManagerName);
        String fullyQualifiedCacheName = EhcacheEntitiesNaming.getToolkitCacheNameFor(cacheManagerName, cacheName);
        this.addNonStopConfigForCache(ehcacheConfig, fullyQualifiedCacheName);
        ToolkitCacheInternal<String, Serializable> toolkitCache = this.getOrCreateToolkitCache(fullyQualifiedCacheName, toolkitCacheConfig);
        this.addCacheEntityInfo(cacheName, ehcacheConfig, fullyQualifiedCacheName);
        return toolkitCache;
    }

    private ToolkitCacheInternal<String, Serializable> getOrCreateToolkitCache(String fullyQualifiedCacheName, org.terracotta.toolkit.config.Configuration toolkitCacheConfig) {
        return (ToolkitCacheInternal)this.toolkit.getCache(fullyQualifiedCacheName, toolkitCacheConfig, Serializable.class);
    }

    @Override
    public ToolkitNotifier<CacheConfigChangeNotificationMsg> getOrCreateConfigChangeNotifier(Ehcache cache) {
        return this.getOrCreateConfigChangeNotifier(cache.getCacheManager().getName(), cache.getName());
    }

    private ToolkitNotifier<CacheConfigChangeNotificationMsg> getOrCreateConfigChangeNotifier(String cacheManagerName, String cacheName) {
        String notifierName = EhcacheEntitiesNaming.getToolkitCacheNameFor(cacheManagerName, cacheName) + "|config-notifier";
        ToolkitNotifier notifier = this.toolkit.getNotifier(notifierName, CacheConfigChangeNotificationMsg.class);
        this.addCacheMetaInfo(cacheName, ToolkitObjectType.NOTIFIER, notifierName);
        return notifier;
    }

    @Override
    public ToolkitNotifier<CacheEventNotificationMsg> getOrCreateCacheEventNotifier(Ehcache cache) {
        return this.getOrCreateCacheEventNotifier(cache.getCacheManager().getName(), cache.getName());
    }

    @Override
    public ToolkitNotifier<CacheDisposalNotification> getOrCreateCacheDisposalNotifier(Ehcache cache) {
        return this.toolkit.getNotifier(EhcacheEntitiesNaming.getToolkitCacheNameFor(cache.getCacheManager().getName(), cache.getName()) + "|disposal-notifier", CacheDisposalNotification.class);
    }

    private ToolkitNotifier<CacheEventNotificationMsg> getOrCreateCacheEventNotifier(String cacheManagerName, String cacheName) {
        String notifierName = EhcacheEntitiesNaming.getToolkitCacheNameFor(cacheManagerName, cacheName) + "|event-notifier";
        ToolkitNotifier notifier = this.toolkit.getNotifier(notifierName, CacheEventNotificationMsg.class);
        this.addCacheMetaInfo(cacheName, ToolkitObjectType.NOTIFIER, notifierName);
        return notifier;
    }

    private static org.terracotta.toolkit.config.Configuration createClusteredCacheConfig(CacheConfiguration ehcacheConfig, String cacheManagerName) {
        ToolkitCacheConfigBuilder builder = new ToolkitCacheConfigBuilder();
        TerracottaConfiguration terracottaConfiguration = ehcacheConfig.getTerracottaConfiguration();
        builder.maxTTISeconds((int)ehcacheConfig.getTimeToIdleSeconds());
        builder.maxTTLSeconds((int)ehcacheConfig.getTimeToLiveSeconds());
        builder.localCacheEnabled(terracottaConfiguration.isLocalCacheEnabled());
        if (ehcacheConfig.getMaxEntriesInCache() != 0L) {
            if (ehcacheConfig.getMaxEntriesInCache() > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Values greater than Integer.MAX_VALUE are not currently supported.");
            }
            builder.maxTotalCount((int)ehcacheConfig.getMaxEntriesInCache());
        }
        if (terracottaConfiguration.isSynchronousWrites()) {
            builder.consistency(ToolkitConfigFields.Consistency.SYNCHRONOUS_STRONG);
        } else if (terracottaConfiguration.getConsistency() == TerracottaConfiguration.Consistency.EVENTUAL) {
            builder.consistency(ToolkitConfigFields.Consistency.EVENTUAL);
        } else {
            builder.consistency(ToolkitConfigFields.Consistency.STRONG);
        }
        if (terracottaConfiguration.getConcurrency() == 0) {
            builder.concurrency(ToolkitInstanceFactoryImpl.calculateCorrectConcurrency(ehcacheConfig));
        } else {
            builder.concurrency(terracottaConfiguration.getConcurrency());
        }
        builder.localCacheEnabled(terracottaConfiguration.isLocalCacheEnabled());
        builder.configField("localStoreManagerName", (Serializable)((Object)cacheManagerName));
        builder.pinnedInLocalMemory(ToolkitInstanceFactoryImpl.isPinnedInLocalMemory(ehcacheConfig));
        builder.evictionEnabled(!ToolkitInstanceFactoryImpl.isPinnedInCache(ehcacheConfig));
        builder.maxCountLocalHeap((int)ehcacheConfig.getMaxEntriesLocalHeap());
        builder.maxBytesLocalHeap(ehcacheConfig.getMaxBytesLocalHeap());
        builder.maxBytesLocalOffheap(ehcacheConfig.getMaxBytesLocalOffHeap());
        builder.offheapEnabled(ehcacheConfig.isOverflowToOffHeap());
        builder.compressionEnabled(terracottaConfiguration.isCompressionEnabled());
        builder.copyOnReadEnabled(ehcacheConfig.isCopyOnRead());
        return builder.build();
    }

    private static boolean isPinnedInCache(CacheConfiguration ehcacheConfig) {
        return ehcacheConfig.getPinningConfiguration() != null && ehcacheConfig.getPinningConfiguration().getStore() == PinningConfiguration.Store.INCACHE;
    }

    private static int calculateCorrectConcurrency(CacheConfiguration cacheConfiguration) {
        int maxElementOnDisk = cacheConfiguration.getMaxElementsOnDisk();
        if (maxElementOnDisk <= 0 || maxElementOnDisk >= 256) {
            return 256;
        }
        int concurrency = 1;
        while (concurrency * 2 <= maxElementOnDisk) {
            concurrency *= 2;
        }
        return concurrency;
    }

    private static boolean isPinnedInLocalMemory(CacheConfiguration ehcacheConfig) {
        return ehcacheConfig.getPinningConfiguration() != null && ehcacheConfig.getPinningConfiguration().getStore() == PinningConfiguration.Store.LOCALMEMORY;
    }

    @Override
    public String getFullyQualifiedCacheName(Ehcache cache) {
        return EhcacheEntitiesNaming.getToolkitCacheNameFor(ToolkitInstanceFactoryImpl.getCacheManagerName(cache), cache.getName());
    }

    private static String getCacheManagerName(Ehcache cache) {
        String cacheMgrName = cache.getCacheManager().isNamed() ? cache.getCacheManager().getName() : "__DEFAULT__";
        return cacheMgrName;
    }

    @Override
    public ToolkitLock getOrCreateStoreLock(Ehcache cache) {
        return this.toolkit.getLock(this.getFullyQualifiedCacheName(cache) + "|storeRWLock");
    }

    @Override
    public ToolkitMap<String, AttributeExtractor> getOrCreateExtractorsMap(String cacheManagerName, String cacheName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ToolkitMap<String, String> getOrCreateAttributeMap(String cacheManagerName, String cacheName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shutdown() {
        if (this.clusteredCacheManagerEntity != null) {
            try {
                this.clusteredCacheManagerEntity.releaseUse();
            }
            catch (Exception e) {
                LOGGER.debug("Exception occurred while releasing clustered cache manager entity use", (Throwable)e);
            }
        }
        this.clusteredEntityManager.dispose();
        this.toolkit.shutdown();
    }

    @Override
    public SerializedToolkitCache<ClusteredSoftLockIDKey, SerializedReadCommittedClusteredSoftLock> getOrCreateAllSoftLockMap(String cacheManagerName, String cacheName) {
        org.terracotta.toolkit.config.Configuration config = new ToolkitStoreConfigBuilder().consistency(ToolkitConfigFields.Consistency.STRONG).build();
        String softLockCacheName = EhcacheEntitiesNaming.getToolkitCacheNameFor(cacheManagerName, cacheName) + "|softLocks";
        ToolkitCache map = this.toolkit.getCache(softLockCacheName, config, SerializedReadCommittedClusteredSoftLock.class);
        this.addCacheMetaInfo(cacheName, ToolkitObjectType.CACHE, softLockCacheName);
        return new SerializedToolkitCache<ClusteredSoftLockIDKey, SerializedReadCommittedClusteredSoftLock>(map);
    }

    @Override
    public ToolkitMap<SerializedReadCommittedClusteredSoftLock, Integer> getOrCreateNewSoftLocksSet(String cacheManagerName, String cacheName) {
        String softLockMapName = EhcacheEntitiesNaming.getToolkitCacheNameFor(cacheManagerName, cacheName) + "|newSoftLocks";
        ToolkitMap softLockMap = this.toolkit.getMap(softLockMapName, SerializedReadCommittedClusteredSoftLock.class, Integer.class);
        this.addCacheMetaInfo(cacheName, ToolkitObjectType.MAP, softLockMapName);
        return softLockMap;
    }

    @Override
    public ToolkitMap<String, AsyncConfig> getOrCreateAsyncConfigMap() {
        return this.toolkit.getMap(EhcacheEntitiesNaming.getAsyncConfigMapName(), String.class, AsyncConfig.class);
    }

    @Override
    public ToolkitMap<String, Set<String>> getOrCreateAsyncListNamesMap(String fullAsyncName, String cacheName) {
        ToolkitMap asyncListNames = this.toolkit.getMap(fullAsyncName, String.class, Set.class);
        this.addCacheMetaInfo(cacheName, ToolkitObjectType.MAP, fullAsyncName);
        this.addKeyRemoveInfo(cacheName, EhcacheEntitiesNaming.getAsyncConfigMapName(), fullAsyncName);
        return asyncListNames;
    }

    @Override
    public ToolkitListInternal getAsyncProcessingBucket(String bucketName, String cacheName) {
        ToolkitListInternal toolkitList = (ToolkitListInternal)this.toolkit.getList(bucketName, null);
        this.addCacheMetaInfo(cacheName, ToolkitObjectType.LIST, bucketName);
        return toolkitList;
    }

    @Override
    public ToolkitMap<String, Serializable> getOrCreateClusteredStoreConfigMap(String cacheManagerName, String cacheName) {
        String configMapName = EhcacheEntitiesNaming.getToolkitCacheNameFor(cacheManagerName, cacheName);
        ToolkitMap<String, Serializable> configMap = this.getOrCreateConfigMap(configMapName);
        this.addCacheMetaInfo(cacheName, ToolkitObjectType.MAP, configMapName);
        return configMap;
    }

    private ToolkitMap<String, Serializable> getOrCreateConfigMap(String fullyQualifiedCacheName) {
        return this.toolkit.getMap(EhcacheEntitiesNaming.getToolkitCacheConfigMapName(fullyQualifiedCacheName), String.class, Serializable.class);
    }

    @Override
    public SerializedToolkitCache<TransactionID, Decision> getOrCreateTransactionCommitStateMap(String cacheManagerName) {
        org.terracotta.toolkit.config.Configuration config = new ToolkitStoreConfigBuilder().consistency(ToolkitConfigFields.Consistency.SYNCHRONOUS_STRONG).build();
        ToolkitCache map = this.toolkit.getCache(cacheManagerName + "|__tc_clustered-ehcache|txnsDecision", config, Decision.class);
        return new SerializedToolkitCache<TransactionID, Decision>(map);
    }

    @Override
    public ToolkitLock getSoftLockWriteLock(String cacheManagerName, String cacheName, TransactionID transactionID, Object key) {
        return this.toolkit.getLock(EhcacheEntitiesNaming.getToolkitCacheNameFor(cacheManagerName, cacheName) + DELIMITER + ToolkitInstanceFactoryImpl.serializeToString(transactionID) + DELIMITER + ToolkitInstanceFactoryImpl.serializeToString(key) + "|__tc_clustered-ehcache|softWriteLock");
    }

    @Override
    public ToolkitLock getLockForCache(Ehcache cache, String lockName) {
        return this.toolkit.getLock(this.getFullyQualifiedCacheName(cache) + DELIMITER + lockName);
    }

    private static String serializeToString(Object serializable) {
        try {
            return SerializationHelper.serializeToString(serializable);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ToolkitReadWriteLock getSoftLockFreezeLock(String cacheManagerName, String cacheName, TransactionID transactionID, Object key) {
        return this.toolkit.getReadWriteLock(EhcacheEntitiesNaming.getToolkitCacheNameFor(cacheManagerName, cacheName) + DELIMITER + ToolkitInstanceFactoryImpl.serializeToString(transactionID) + DELIMITER + ToolkitInstanceFactoryImpl.serializeToString(key) + "|__tc_clustered-ehcache|softFreezeLock");
    }

    @Override
    public ToolkitReadWriteLock getSoftLockNotifierLock(String cacheManagerName, String cacheName, TransactionID transactionID, Object key) {
        return this.toolkit.getReadWriteLock(EhcacheEntitiesNaming.getToolkitCacheNameFor(cacheManagerName, cacheName) + DELIMITER + ToolkitInstanceFactoryImpl.serializeToString(transactionID) + DELIMITER + ToolkitInstanceFactoryImpl.serializeToString(key) + "|__tc_clustered-ehcache|softNotifierLock");
    }

    @Override
    public boolean destroy(String cacheManagerName, String cacheName) {
        this.getOrCreateAllSoftLockMap(cacheManagerName, cacheName).destroy();
        this.getOrCreateNewSoftLocksSet(cacheManagerName, cacheName).destroy();
        this.getOrCreateCacheEventNotifier(cacheManagerName, cacheName).destroy();
        this.getOrCreateConfigChangeNotifier(cacheManagerName, cacheName).destroy();
        this.getOrCreateToolkitCache(EhcacheEntitiesNaming.getToolkitCacheNameFor(cacheManagerName, cacheName), new ToolkitCacheConfigBuilder().maxCountLocalHeap(1).maxBytesLocalOffheap(0L).build()).destroy();
        ToolkitMap<String, Serializable> clusteredStoreConfigMap = this.getOrCreateClusteredStoreConfigMap(cacheManagerName, cacheName);
        boolean existed = !clusteredStoreConfigMap.isEmpty();
        clusteredStoreConfigMap.destroy();
        return existed;
    }

    protected void addNonStopConfigForCache(CacheConfiguration ehcacheConfig, String fullyQualifiedCacheName) {
        TerracottaConfiguration terracottaConfiguration = ehcacheConfig.getTerracottaConfiguration();
        ToolkitNonStopConfiguration nonstopConfiguration = new ToolkitNonStopConfiguration(terracottaConfiguration.getNonstopConfiguration());
        ((NonStopFeature)this.toolkit.getFeature(ToolkitFeatureType.NONSTOP)).getNonStopConfigurationRegistry().registerForInstance((NonStopConfiguration)nonstopConfiguration, fullyQualifiedCacheName, ToolkitObjectType.CACHE);
    }

    @Override
    public void removeNonStopConfigforCache(Ehcache cache) {
        ((NonStopFeature)this.toolkit.getFeature(ToolkitFeatureType.NONSTOP)).getNonStopConfigurationRegistry().deregisterForInstance(this.getFullyQualifiedCacheName(cache), ToolkitObjectType.CACHE);
    }

    protected void addCacheMetaInfo(String cacheName, ToolkitObjectType type, String dsName) {
        ToolkitBackedClusteredCacheManager tbccm = (ToolkitBackedClusteredCacheManager)this.clusteredCacheManagerEntity;
        tbccm.addCacheMetaInfo(cacheName, type, dsName);
    }

    private void addKeyRemoveInfo(String cacheName, String toolkitMapName, String keytoBeRemoved) {
        ToolkitBackedClusteredCacheManager tbccm = (ToolkitBackedClusteredCacheManager)this.clusteredCacheManagerEntity;
        tbccm.addKeyRemoveInfo(cacheName, toolkitMapName, keytoBeRemoved);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void linkClusteredCacheManager(String cacheManagerName, Configuration configuration) {
        Objects.requireNonNull(cacheManagerName);
        if (this.clusteredCacheManagerEntity == null) {
            try {
                this.logCacheManagerConfigInTerracottaClientLogs(cacheManagerName, configuration);
                ClusteredCacheManager clusteredCacheManager = this.clusteredEntityManager.getRootEntity(cacheManagerName, ClusteredCacheManager.class);
                ToolkitReadWriteLock cmRWLock = this.clusteredEntityManager.getEntityLock(EhcacheEntitiesNaming.getCacheManagerLockNameFor(cacheManagerName));
                ToolkitLock cmWriteLock = cmRWLock.writeLock();
                while (clusteredCacheManager == null) {
                    if (cmWriteLock.tryLock()) {
                        try {
                            clusteredCacheManager = this.createClusteredCacheManagerEntity(cacheManagerName, configuration);
                            continue;
                        }
                        finally {
                            cmWriteLock.unlock();
                            continue;
                        }
                    }
                    clusteredCacheManager = this.clusteredEntityManager.getRootEntity(cacheManagerName, ClusteredCacheManager.class);
                }
                this.clusteredCacheManagerEntity = clusteredCacheManager;
                this.entityNames.setCacheManagerName(cacheManagerName);
            }
            catch (RuntimeException re) {
                this.entityNames.linkFailure = re;
                throw re;
            }
        }
    }

    private void logCacheManagerConfigInTerracottaClientLogs(String cacheManagerName, Configuration configuration) {
        ToolkitLogger logger = ((ToolkitInternal)this.toolkit).getLogger(CONFIG_LOGGER_NAME);
        if (logger.isInfoEnabled()) {
            try {
                logger.info((Object)("Configuration for clustered cache manager " + cacheManagerName + ":\n" + this.convertConfigurationToXMLString(configuration, cacheManagerName, true)));
            }
            catch (Exception e) {
                logger.warn((Object)("Exception while trying to log configuration for clustered cache manager " + cacheManagerName), (Throwable)e);
            }
        }
    }

    private ClusteredCacheManager createClusteredCacheManagerEntity(String cacheManagerName, Configuration configuration) {
        String xmlConfig = this.convertConfigurationToXMLString(configuration, cacheManagerName, true);
        ClusteredCacheManager clusteredCacheManager = new ToolkitBackedClusteredCacheManager(cacheManagerName, new ClusteredCacheManagerConfiguration(xmlConfig));
        ClusteredCacheManager existing = this.clusteredEntityManager.addRootEntityIfAbsent(cacheManagerName, ClusteredCacheManager.class, clusteredCacheManager);
        if (existing != null) {
            clusteredCacheManager = existing;
        }
        return clusteredCacheManager;
    }

    @Override
    public ToolkitMap<String, Serializable> getOrCreateCacheManagerMetaInfoMap(String cacheManagerName) {
        String configMapName = EhcacheEntitiesNaming.getCacheManagerConfigMapName(cacheManagerName);
        ToolkitMap configMap = this.toolkit.getMap(configMapName, String.class, Serializable.class);
        return configMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void addCacheEntityInfo(String cacheName, CacheConfiguration ehcacheConfig, String toolkitCacheName) {
        if (this.clusteredCacheManagerEntity == null) {
            throw new IllegalStateException(String.format("ClusteredCacheManger entity not configured for cache %s", cacheName));
        }
        this.logCacheConfigInTerracottaClientLogs(cacheName, ehcacheConfig);
        ClusteredCache cacheEntity = this.clusteredCacheManagerEntity.getCache(cacheName);
        if (cacheEntity == null) {
            ToolkitReadWriteLock cacheRWLock = this.clusteredCacheManagerEntity.getCacheLock(cacheName);
            ToolkitLock cacheWriteLock = cacheRWLock.writeLock();
            while (cacheEntity == null) {
                if (cacheWriteLock.tryLock()) {
                    try {
                        cacheEntity = this.createClusteredCacheEntity(cacheName, ehcacheConfig, toolkitCacheName);
                        continue;
                    }
                    finally {
                        cacheWriteLock.unlock();
                        continue;
                    }
                }
                cacheEntity = this.clusteredCacheManagerEntity.getCache(cacheName);
            }
        }
        this.clusteredCacheManagerEntity.markCacheInUse(cacheEntity);
        this.entityNames.addCacheName(cacheName);
    }

    private void logCacheConfigInTerracottaClientLogs(String cacheName, CacheConfiguration ehcacheConfig) {
        ToolkitLogger logger = ((ToolkitInternal)this.toolkit).getLogger(CONFIG_LOGGER_NAME);
        if (logger.isInfoEnabled()) {
            try {
                logger.info((Object)("Client configuration for clustered cache named " + cacheName + ":\n(clustered properties may differ in runtime cache depending on configuration used at creation time)\n" + this.convertCacheConfigurationToXMLString(ehcacheConfig)));
            }
            catch (Exception e) {
                logger.warn((Object)("Exception while trying to log configuration for clustered cache " + cacheName), (Throwable)e);
            }
        }
    }

    private ClusteredCache createClusteredCacheEntity(String cacheName, CacheConfiguration ehcacheConfig, String toolkitCacheName) {
        ClusteredCacheConfiguration clusteredConfiguration = this.createClusteredCacheConfiguration(ehcacheConfig);
        ClusteredCache cacheEntity = new ToolkitBackedClusteredCache(cacheName, clusteredConfiguration, toolkitCacheName);
        ClusteredCache existing = this.clusteredCacheManagerEntity.addCacheIfAbsent(cacheName, cacheEntity);
        if (existing != null) {
            cacheEntity = existing;
        }
        return cacheEntity;
    }

    private ClusteredCacheConfiguration createClusteredCacheConfiguration(CacheConfiguration ehcacheConfig) {
        String xmlConfig = this.convertCacheConfigurationToXMLString(ehcacheConfig);
        return new ClusteredCacheConfiguration(xmlConfig);
    }

    private String convertCacheConfigurationToXMLString(CacheConfiguration ehcacheConfig) {
        Configuration configuration = this.parseCacheManagerConfiguration(((ClusteredCacheManagerConfiguration)this.clusteredCacheManagerEntity.getConfiguration()).getConfigurationAsText());
        return ConfigurationUtil.generateCacheConfigurationText(configuration, ehcacheConfig);
    }

    @Override
    public void unlinkCache(String cacheName) {
        this.entityNames.removeCacheName(cacheName);
        ClusteredCache cacheEntity = this.clusteredCacheManagerEntity.getCache(cacheName);
        this.clusteredCacheManagerEntity.releaseCacheUse(cacheEntity);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clusterRejoined() {
        EntityNamesHolder entityNamesHolder = this.entityNames;
        synchronized (entityNamesHolder) {
            String cacheManagerName = this.entityNames.cacheManagerName;
            if (cacheManagerName == null) {
                LOGGER.error("Cache Manager {} linking to the entity manager failed - shutting down to prevent any further use of clustered features", (Object)cacheManagerName, (Object)this.entityNames.linkFailure);
                this.shutdown();
                return;
            }
            ClusteredCacheManager clusteredCacheManager = this.clusteredEntityManager.getRootEntity(cacheManagerName, ClusteredCacheManager.class);
            if (clusteredCacheManager == null) {
                LOGGER.error("Cache Manager {} has been destroyed by some other node - shutting down to prevent any further use of clustered features", (Object)cacheManagerName);
                this.shutdown();
            } else {
                try {
                    clusteredCacheManager.releaseUse();
                }
                catch (Exception e) {
                    LOGGER.trace("Exception trying to release cache manager {} after rejoin", (Object)this.entityNames.cacheManagerName);
                }
                for (String cacheName : this.entityNames.getCacheNames()) {
                    ClusteredCache cacheEntity = this.clusteredCacheManagerEntity.getCache(cacheName);
                    if (cacheEntity == null) continue;
                    try {
                        this.clusteredCacheManagerEntity.releaseCacheUse(cacheEntity);
                    }
                    catch (Exception e2) {
                        LOGGER.trace("Exception trying to release cache {} after rejoin", (Object)cacheName);
                    }
                }
                boolean success = false;
                for (int retryCount = 0; retryCount < 5; ++retryCount) {
                    try {
                        clusteredCacheManager.markInUse();
                        success = this.clusteredEntityManager.getRootEntity(cacheManagerName, ClusteredCacheManager.class) != null;
                        break;
                    }
                    catch (Exception e) {
                        try {
                            TimeUnit.SECONDS.sleep(1L);
                        }
                        catch (InterruptedException e2) {
                            // empty catch block
                        }
                        continue;
                    }
                }
                if (!success) {
                    LOGGER.error("Unable to mark cache manager {} in use - shutting down to prevent any further use of clustered features", (Object)cacheManagerName);
                    this.shutdown();
                } else {
                    for (String cacheName : this.entityNames.getCacheNames()) {
                        boolean successCache = false;
                        int retryCountCache = 0;
                        while (!successCache && retryCountCache < 5) {
                            ClusteredCache cacheEntity = this.clusteredCacheManagerEntity.getCache(cacheName);
                            if (cacheEntity == null) {
                                LOGGER.error("Cache " + cacheName + " has been destroyed by some other node");
                                successCache = true;
                                continue;
                            }
                            try {
                                this.clusteredCacheManagerEntity.markCacheInUse(cacheEntity);
                                successCache = true;
                            }
                            catch (Exception e) {
                                try {
                                    TimeUnit.SECONDS.sleep(1L);
                                }
                                catch (InterruptedException interruptedException) {
                                    // empty catch block
                                }
                                ++retryCountCache;
                            }
                        }
                    }
                }
            }
        }
    }

    private String convertConfigurationToXMLString(Configuration configuration, String cacheManagerName, boolean stripCacheConfigs) {
        Configuration targetConfiguration = this.cloneConfiguration(configuration);
        targetConfiguration.setName(cacheManagerName);
        if (stripCacheConfigs) {
            targetConfiguration.getCacheConfigurations().clear();
        }
        return ConfigurationUtil.generateCacheManagerConfigurationText(targetConfiguration);
    }

    private Configuration cloneConfiguration(Configuration configuration) {
        String tmp = ConfigurationUtil.generateCacheManagerConfigurationText(configuration);
        Configuration targetConfiguration = this.parseCacheManagerConfiguration(tmp);
        return targetConfiguration;
    }

    private Configuration parseCacheManagerConfiguration(String xmlCacheManagerConfig) {
        Configuration targetConfiguration = ConfigurationFactory.parseConfiguration(new BufferedInputStream(new ByteArrayInputStream(xmlCacheManagerConfig.getBytes())));
        return targetConfiguration;
    }

    private static class EhcacheTcConfig {
        private final Type type;
        private final String tcConfigUrlOrSnippet;

        private EhcacheTcConfig(Type type, String config) {
            this.type = type;
            this.tcConfigUrlOrSnippet = config;
        }

        public static EhcacheTcConfig create(TerracottaClientConfiguration config) {
            if (config.isUrlConfig()) {
                String urlOrFilePath = config.getUrl();
                if (EhcacheTcConfig.isFile(urlOrFilePath)) {
                    return new EhcacheTcConfig(Type.FILE, EhcacheTcConfig.slurpFile(urlOrFilePath));
                }
                if (EhcacheTcConfig.isValidURL(urlOrFilePath)) {
                    return new EhcacheTcConfig(Type.EMBEDDED_TC_CONFIG, EhcacheTcConfig.fetchConfigFromURL(urlOrFilePath));
                }
                return new EhcacheTcConfig(Type.URL, urlOrFilePath);
            }
            return new EhcacheTcConfig(Type.EMBEDDED_TC_CONFIG, config.getEmbeddedConfig());
        }

        private static String slurpFile(String urlOrFilePath) {
            try {
                return EhcacheTcConfig.fetchConfigFromStream(new FileInputStream(urlOrFilePath));
            }
            catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        private static boolean isFile(String urlOrFilePath) {
            File file = new File(urlOrFilePath);
            return file.exists() && file.isFile();
        }

        private static String fetchConfigFromURL(String urlOrFilePath) {
            try {
                return EhcacheTcConfig.fetchConfigFromStream(new URL(urlOrFilePath).openStream());
            }
            catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private static String fetchConfigFromStream(InputStream inputStream) {
            try {
                StringBuilder builder = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                return builder.toString();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private static boolean isValidURL(String urlOrFilePath) {
            try {
                new URL(urlOrFilePath);
                return true;
            }
            catch (MalformedURLException e) {
                return false;
            }
        }

        private static enum Type {
            URL,
            EMBEDDED_TC_CONFIG,
            FILE;

        }
    }

    private class EntityNamesHolder {
        volatile RuntimeException linkFailure;
        private String cacheManagerName;
        private final Set<String> cacheNames = new HashSet<String>();

        private EntityNamesHolder() {
        }

        private synchronized void setCacheManagerName(String cacheMgrName) {
            if (this.cacheManagerName == null) {
                this.cacheManagerName = cacheMgrName;
                ToolkitInstanceFactoryImpl.this.clusteredCacheManagerEntity.markInUse();
            }
        }

        private synchronized void addCacheName(String cacheName) {
            this.cacheNames.add(cacheName);
        }

        private synchronized void removeCacheName(String cacheName) {
            this.cacheNames.remove(cacheName);
        }

        private Set<String> getCacheNames() {
            return Collections.unmodifiableSet(this.cacheNames);
        }
    }
}

