/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import net.sf.ehcache.CacheClusterStateStatisticsListener;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.CacheQuery;
import net.sf.ehcache.Disposable;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.FeaturesManager;
import net.sf.ehcache.LoaderTimeoutException;
import net.sf.ehcache.Status;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.bootstrap.BootstrapCacheLoaderFactory;
import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.cluster.ClusterScheme;
import net.sf.ehcache.cluster.ClusterSchemeNotAvailableException;
import net.sf.ehcache.concurrent.CacheLockProvider;
import net.sf.ehcache.concurrent.LockType;
import net.sf.ehcache.concurrent.StripedReadWriteLockSync;
import net.sf.ehcache.concurrent.Sync;
import net.sf.ehcache.config.AbstractCacheConfigurationListener;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheWriterConfiguration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.InvalidConfigurationException;
import net.sf.ehcache.config.NonstopConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.config.SearchAttribute;
import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.constructs.nonstop.concurrency.LockOperationTimedOutNonstopException;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.exceptionhandler.CacheExceptionHandler;
import net.sf.ehcache.extension.CacheExtension;
import net.sf.ehcache.extension.CacheExtensionFactory;
import net.sf.ehcache.loader.CacheLoader;
import net.sf.ehcache.loader.CacheLoaderFactory;
import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.pool.SizeOfEngine;
import net.sf.ehcache.pool.impl.BoundedPool;
import net.sf.ehcache.pool.impl.FromLargestCachePoolEvictor;
import net.sf.ehcache.pool.impl.UnboundedPool;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.aggregator.AggregatorInstance;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.attribute.DynamicAttributesExtractor;
import net.sf.ehcache.search.attribute.UnknownAttributeException;
import net.sf.ehcache.search.expression.BaseCriteria;
import net.sf.ehcache.statistics.StatisticBuilder;
import net.sf.ehcache.statistics.StatisticsGateway;
import net.sf.ehcache.store.CopyingCacheStore;
import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.LegacyStoreWrapper;
import net.sf.ehcache.store.LruMemoryStore;
import net.sf.ehcache.store.MemoryStore;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import net.sf.ehcache.store.Policy;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.StoreListener;
import net.sf.ehcache.store.StoreQuery;
import net.sf.ehcache.store.TerracottaStore;
import net.sf.ehcache.store.TerracottaTransactionalCopyingCacheStore;
import net.sf.ehcache.store.TxCopyingCacheStore;
import net.sf.ehcache.store.compound.ReadWriteSerializationCopyStrategy;
import net.sf.ehcache.store.disk.DiskStore;
import net.sf.ehcache.store.disk.StoreUpdateException;
import net.sf.ehcache.terracotta.InternalEhcache;
import net.sf.ehcache.terracotta.TerracottaNotRunningException;
import net.sf.ehcache.transaction.AbstractTransactionStore;
import net.sf.ehcache.transaction.SoftLockManager;
import net.sf.ehcache.transaction.TransactionIDFactory;
import net.sf.ehcache.transaction.local.JtaLocalTransactionStore;
import net.sf.ehcache.transaction.local.LocalTransactionStore;
import net.sf.ehcache.transaction.manager.TransactionManagerLookup;
import net.sf.ehcache.transaction.xa.XATransactionStore;
import net.sf.ehcache.util.ClassLoaderUtil;
import net.sf.ehcache.util.NamedThreadFactory;
import net.sf.ehcache.util.PropertyUtil;
import net.sf.ehcache.util.TimeUtil;
import net.sf.ehcache.util.VmUtils;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.CacheWriterFactory;
import net.sf.ehcache.writer.CacheWriterManager;
import net.sf.ehcache.writer.CacheWriterManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.context.annotations.ContextAttribute;
import org.terracotta.statistics.Statistic;
import org.terracotta.statistics.StatisticsManager;
import org.terracotta.statistics.observer.OperationObserver;

public class Cache
implements InternalEhcache,
StoreListener {
    public static final String DEFAULT_CACHE_NAME = "default";
    public static final String NET_SF_EHCACHE_DISABLED = "net.sf.ehcache.disabled";
    public static final String NET_SF_EHCACHE_USE_CLASSIC_LRU = "net.sf.ehcache.use.classic.lru";
    public static final long DEFAULT_EXPIRY_THREAD_INTERVAL_SECONDS = 120L;
    private static final Logger LOG = LoggerFactory.getLogger((String)Cache.class.getName());
    private static InetAddress localhost;
    private static final int BACK_OFF_TIME_MILLIS = 50;
    private static final int EXECUTOR_KEEP_ALIVE_TIME = 60000;
    private static final int EXECUTOR_MAXIMUM_POOL_SIZE;
    private static final int EXECUTOR_CORE_POOL_SIZE = 1;
    private static final String EHCACHE_CLUSTERREDSTORE_MAX_CONCURRENCY_PROP = "ehcache.clusteredStore.maxConcurrency";
    private static final int DEFAULT_EHCACHE_CLUSTERREDSTORE_MAX_CONCURRENCY = 4096;
    private volatile boolean disabled = Boolean.getBoolean("net.sf.ehcache.disabled");
    private final boolean useClassicLru = Boolean.getBoolean("net.sf.ehcache.use.classic.lru");
    private final CacheStatus cacheStatus = new CacheStatus();
    private final CacheConfiguration configuration;
    private volatile Store compoundStore;
    private volatile CacheLockProvider lockProvider;
    private volatile RegisteredEventListeners registeredEventListeners;
    private final List<CacheExtension> registeredCacheExtensions = new CopyOnWriteArrayList<CacheExtension>();
    private final String guid = this.createGuid();
    private volatile CacheManager cacheManager;
    private volatile BootstrapCacheLoader bootstrapCacheLoader;
    private volatile CacheExceptionHandler cacheExceptionHandler;
    private final List<CacheLoader> registeredCacheLoaders = new CopyOnWriteArrayList<CacheLoader>();
    private volatile CacheWriterManager cacheWriterManager;
    private final AtomicBoolean cacheWriterManagerInitFlag = new AtomicBoolean(false);
    private final ReentrantLock cacheWriterManagerInitLock = new ReentrantLock();
    private volatile CacheWriter registeredCacheWriter;
    private final OperationObserver<CacheOperationOutcomes.GetOutcome> getObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.GetOutcome.class).named("get")).of(this)).tag(new String[]{"cache"})).build();
    private final OperationObserver<CacheOperationOutcomes.PutOutcome> putObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.PutOutcome.class).named("put")).of(this)).tag(new String[]{"cache"})).build();
    private final OperationObserver<CacheOperationOutcomes.RemoveOutcome> removeObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.RemoveOutcome.class).named("remove")).of(this)).tag(new String[]{"cache"})).build();
    private final OperationObserver<CacheOperationOutcomes.GetAllOutcome> getAllObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.GetAllOutcome.class).named("getAll")).of(this)).tag(new String[]{"cache", "bulk"})).build();
    private final OperationObserver<CacheOperationOutcomes.PutAllOutcome> putAllObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.PutAllOutcome.class).named("putAll")).of(this)).tag(new String[]{"cache", "bulk"})).build();
    private final OperationObserver<CacheOperationOutcomes.RemoveAllOutcome> removeAllObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.RemoveAllOutcome.class).named("removeAll")).of(this)).tag(new String[]{"cache", "bulk"})).build();
    private final OperationObserver<CacheOperationOutcomes.SearchOutcome> searchObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.SearchOutcome.class).named("search")).of(this)).tag(new String[]{"cache"})).build();
    private final OperationObserver<CacheOperationOutcomes.ReplaceOneArgOutcome> replace1Observer = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.ReplaceOneArgOutcome.class).named("replace1")).of(this)).tag(new String[]{"cache"})).build();
    private final OperationObserver<CacheOperationOutcomes.ReplaceTwoArgOutcome> replace2Observer = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.ReplaceTwoArgOutcome.class).named("replace2")).of(this)).tag(new String[]{"cache"})).build();
    private final OperationObserver<CacheOperationOutcomes.PutIfAbsentOutcome> putIfAbsentObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.PutIfAbsentOutcome.class).named("putIfAbsent")).of(this)).tag(new String[]{"cache"})).build();
    private final OperationObserver<CacheOperationOutcomes.RemoveElementOutcome> removeElementObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.RemoveElementOutcome.class).named("removeElement")).of(this)).tag(new String[]{"cache"})).build();
    private volatile ExecutorService executorService;
    private volatile TransactionManagerLookup transactionManagerLookup;
    private volatile boolean allowDisable = true;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private volatile ElementValueComparator elementValueComparator;
    private StatisticsGateway statistics;
    private CacheClusterStateStatisticsListener clusterStateListener = null;
    private AbstractCacheConfigurationListener configListener;

    public Cache(CacheConfiguration cacheConfiguration) {
        this(cacheConfiguration, null, (BootstrapCacheLoader)null);
    }

    public Cache(CacheConfiguration cacheConfiguration, RegisteredEventListeners registeredEventListeners, BootstrapCacheLoader bootstrapCacheLoader) {
        ClassLoader loader = cacheConfiguration.getClassLoader();
        this.cacheStatus.changeState(Status.STATUS_UNINITIALISED);
        this.configuration = cacheConfiguration.clone();
        this.configuration.validateCompleteConfiguration();
        this.registeredEventListeners = registeredEventListeners == null ? new RegisteredEventListeners(this) : registeredEventListeners;
        RegisteredEventListeners listeners = this.getCacheEventNotificationService();
        Cache.registerCacheListeners(this.configuration, listeners, loader);
        Cache.registerCacheExtensions(this.configuration, this, loader);
        this.bootstrapCacheLoader = null == bootstrapCacheLoader ? Cache.createBootstrapCacheLoader(this.configuration.getBootstrapCacheLoaderFactoryConfiguration(), loader) : bootstrapCacheLoader;
        Cache.registerCacheLoaders(this.configuration, this, loader);
        Cache.registerCacheWriter(this.configuration, this, loader);
    }

    public Cache(String name, int maxElementsInMemory, boolean overflowToDisk, boolean eternal, long timeToLiveSeconds, long timeToIdleSeconds) {
        this(new CacheConfiguration(name, maxElementsInMemory).overflowToDisk(overflowToDisk).eternal(eternal).timeToLiveSeconds(timeToLiveSeconds).timeToIdleSeconds(timeToIdleSeconds));
    }

    public Cache(String name, int maxElementsInMemory, boolean overflowToDisk, boolean eternal, long timeToLiveSeconds, long timeToIdleSeconds, boolean diskPersistent, long diskExpiryThreadIntervalSeconds) {
        this(new CacheConfiguration(name, maxElementsInMemory).overflowToDisk(overflowToDisk).eternal(eternal).timeToLiveSeconds(timeToLiveSeconds).timeToIdleSeconds(timeToIdleSeconds).diskPersistent(diskPersistent).diskExpiryThreadIntervalSeconds(diskExpiryThreadIntervalSeconds));
        LOG.warn("An API change between ehcache-1.1 and ehcache-1.2 results in the persistence path being set to " + DiskStoreConfiguration.getDefaultPath() + " when the ehcache-1.1 constructor is used. Please change to the 1.2 constructor.");
    }

    public Cache(String name, int maxElementsInMemory, MemoryStoreEvictionPolicy memoryStoreEvictionPolicy, boolean overflowToDisk, String diskStorePath, boolean eternal, long timeToLiveSeconds, long timeToIdleSeconds, boolean diskPersistent, long diskExpiryThreadIntervalSeconds, RegisteredEventListeners registeredEventListeners) {
        this(new CacheConfiguration(name, maxElementsInMemory).memoryStoreEvictionPolicy(memoryStoreEvictionPolicy).overflowToDisk(overflowToDisk).eternal(eternal).timeToLiveSeconds(timeToLiveSeconds).timeToIdleSeconds(timeToIdleSeconds).diskPersistent(diskPersistent).diskExpiryThreadIntervalSeconds(diskExpiryThreadIntervalSeconds), registeredEventListeners, null);
    }

    public Cache(String name, int maxElementsInMemory, MemoryStoreEvictionPolicy memoryStoreEvictionPolicy, boolean overflowToDisk, String diskStorePath, boolean eternal, long timeToLiveSeconds, long timeToIdleSeconds, boolean diskPersistent, long diskExpiryThreadIntervalSeconds, RegisteredEventListeners registeredEventListeners, BootstrapCacheLoader bootstrapCacheLoader) {
        this(new CacheConfiguration(name, maxElementsInMemory).memoryStoreEvictionPolicy(memoryStoreEvictionPolicy).overflowToDisk(overflowToDisk).eternal(eternal).timeToLiveSeconds(timeToLiveSeconds).timeToIdleSeconds(timeToIdleSeconds).diskPersistent(diskPersistent).diskExpiryThreadIntervalSeconds(diskExpiryThreadIntervalSeconds), registeredEventListeners, bootstrapCacheLoader);
    }

    public Cache(String name, int maxElementsInMemory, MemoryStoreEvictionPolicy memoryStoreEvictionPolicy, boolean overflowToDisk, String diskStorePath, boolean eternal, long timeToLiveSeconds, long timeToIdleSeconds, boolean diskPersistent, long diskExpiryThreadIntervalSeconds, RegisteredEventListeners registeredEventListeners, BootstrapCacheLoader bootstrapCacheLoader, int maxElementsOnDisk) {
        this(new CacheConfiguration(name, maxElementsInMemory).memoryStoreEvictionPolicy(memoryStoreEvictionPolicy).overflowToDisk(overflowToDisk).eternal(eternal).timeToLiveSeconds(timeToLiveSeconds).timeToIdleSeconds(timeToIdleSeconds).diskPersistent(diskPersistent).diskExpiryThreadIntervalSeconds(diskExpiryThreadIntervalSeconds).maxElementsOnDisk(maxElementsOnDisk), registeredEventListeners, bootstrapCacheLoader);
    }

    public Cache(String name, int maxElementsInMemory, MemoryStoreEvictionPolicy memoryStoreEvictionPolicy, boolean overflowToDisk, String diskStorePath, boolean eternal, long timeToLiveSeconds, long timeToIdleSeconds, boolean diskPersistent, long diskExpiryThreadIntervalSeconds, RegisteredEventListeners registeredEventListeners, BootstrapCacheLoader bootstrapCacheLoader, int maxElementsOnDisk, int diskSpoolBufferSizeMB) {
        this(new CacheConfiguration(name, maxElementsInMemory).memoryStoreEvictionPolicy(memoryStoreEvictionPolicy).overflowToDisk(overflowToDisk).eternal(eternal).timeToLiveSeconds(timeToLiveSeconds).timeToIdleSeconds(timeToIdleSeconds).diskPersistent(diskPersistent).diskExpiryThreadIntervalSeconds(diskExpiryThreadIntervalSeconds).maxElementsOnDisk(maxElementsOnDisk).diskSpoolBufferSizeMB(diskSpoolBufferSizeMB), registeredEventListeners, bootstrapCacheLoader);
    }

    public Cache(String name, int maxElementsInMemory, MemoryStoreEvictionPolicy memoryStoreEvictionPolicy, boolean overflowToDisk, String diskStorePath, boolean eternal, long timeToLiveSeconds, long timeToIdleSeconds, boolean diskPersistent, long diskExpiryThreadIntervalSeconds, RegisteredEventListeners registeredEventListeners, BootstrapCacheLoader bootstrapCacheLoader, int maxElementsOnDisk, int diskSpoolBufferSizeMB, boolean clearOnFlush) {
        this(new CacheConfiguration(name, maxElementsInMemory).memoryStoreEvictionPolicy(memoryStoreEvictionPolicy).overflowToDisk(overflowToDisk).eternal(eternal).timeToLiveSeconds(timeToLiveSeconds).timeToIdleSeconds(timeToIdleSeconds).diskPersistent(diskPersistent).diskExpiryThreadIntervalSeconds(diskExpiryThreadIntervalSeconds).maxElementsOnDisk(maxElementsOnDisk).diskSpoolBufferSizeMB(diskSpoolBufferSizeMB).clearOnFlush(clearOnFlush), registeredEventListeners, bootstrapCacheLoader);
    }

    public Cache(String name, int maxElementsInMemory, MemoryStoreEvictionPolicy memoryStoreEvictionPolicy, boolean overflowToDisk, String diskStorePath, boolean eternal, long timeToLiveSeconds, long timeToIdleSeconds, boolean diskPersistent, long diskExpiryThreadIntervalSeconds, RegisteredEventListeners registeredEventListeners, BootstrapCacheLoader bootstrapCacheLoader, int maxElementsOnDisk, int diskSpoolBufferSizeMB, boolean clearOnFlush, boolean isTerracottaClustered, boolean terracottaCoherentReads) {
        this(new CacheConfiguration(name, maxElementsInMemory).memoryStoreEvictionPolicy(memoryStoreEvictionPolicy).overflowToDisk(overflowToDisk).eternal(eternal).timeToLiveSeconds(timeToLiveSeconds).timeToIdleSeconds(timeToIdleSeconds).diskPersistent(diskPersistent).diskExpiryThreadIntervalSeconds(diskExpiryThreadIntervalSeconds).maxElementsOnDisk(maxElementsOnDisk).diskSpoolBufferSizeMB(diskSpoolBufferSizeMB).clearOnFlush(clearOnFlush).terracotta(new TerracottaConfiguration().clustered(isTerracottaClustered).coherentReads(terracottaCoherentReads)), registeredEventListeners, bootstrapCacheLoader);
    }

    Cache(CacheConfiguration config, Store compoundStore, RegisteredEventListeners listeners) {
        this.configuration = config;
        this.compoundStore = compoundStore;
        this.registeredEventListeners = listeners;
        this.cacheStatus.changeState(Status.STATUS_ALIVE);
    }

    private Cache(Cache original) throws CloneNotSupportedException {
        if (original.compoundStore != null) {
            throw new CloneNotSupportedException("Cannot clone an initialized cache.");
        }
        ClassLoader loader = original.configuration.getClassLoader();
        this.configuration = original.configuration.clone();
        this.cacheStatus.changeState(Status.STATUS_UNINITIALISED);
        this.configuration.getCopyStrategyConfiguration().setCopyStrategyInstance(null);
        this.elementValueComparator = this.configuration.getElementValueComparatorConfiguration().createElementComparatorInstance(this.configuration, loader);
        for (PropertyChangeListener propertyChangeListener : original.propertyChangeSupport.getPropertyChangeListeners()) {
            this.addPropertyChangeListener(propertyChangeListener);
        }
        RegisteredEventListeners registeredEventListenersFromOriginal = original.getCacheEventNotificationService();
        if (registeredEventListenersFromOriginal == null || registeredEventListenersFromOriginal.getCacheEventListeners().size() == 0) {
            this.registeredEventListeners = new RegisteredEventListeners(this);
        } else {
            this.registeredEventListeners = new RegisteredEventListeners(this);
            Set<CacheEventListener> cacheEventListeners = original.registeredEventListeners.getCacheEventListeners();
            Iterator<CacheEventListener> iterator = cacheEventListeners.iterator();
            while (iterator.hasNext()) {
                CacheEventListener cacheEventListener1;
                CacheEventListener cacheEventListener = cacheEventListener1 = iterator.next();
                CacheEventListener cacheEventListenerClone = (CacheEventListener)cacheEventListener.clone();
                this.registeredEventListeners.registerListener(cacheEventListenerClone);
            }
        }
        for (CacheExtension registeredCacheExtension : original.registeredCacheExtensions) {
            this.registerCacheExtension(registeredCacheExtension.clone(this));
        }
        for (CacheLoader registeredCacheLoader : original.registeredCacheLoaders) {
            this.registerCacheLoader(registeredCacheLoader.clone(this));
        }
        if (original.registeredCacheWriter != null) {
            this.registerCacheWriter(this.registeredCacheWriter.clone(this));
        }
        if (original.bootstrapCacheLoader != null) {
            BootstrapCacheLoader bootstrapCacheLoaderClone = (BootstrapCacheLoader)original.bootstrapCacheLoader.clone();
            this.setBootstrapCacheLoader(bootstrapCacheLoaderClone);
        }
    }

    private static void registerCacheListeners(CacheConfiguration cacheConfiguration, RegisteredEventListeners registeredEventListeners, ClassLoader loader) {
        List cacheEventListenerConfigurations = cacheConfiguration.getCacheEventListenerConfigurations();
        for (Object cacheEventListenerConfiguration : cacheEventListenerConfigurations) {
            CacheConfiguration.CacheEventListenerFactoryConfiguration factoryConfiguration = (CacheConfiguration.CacheEventListenerFactoryConfiguration)cacheEventListenerConfiguration;
            CacheEventListener cacheEventListener = Cache.createCacheEventListener(factoryConfiguration, loader);
            registeredEventListeners.registerListener(cacheEventListener, factoryConfiguration.getListenFor());
        }
    }

    private static void registerCacheExtensions(CacheConfiguration cacheConfiguration, Ehcache cache, ClassLoader loader) {
        List cacheExtensionConfigurations = cacheConfiguration.getCacheExtensionConfigurations();
        for (Object cacheExtensionConfiguration : cacheExtensionConfigurations) {
            CacheConfiguration.CacheExtensionFactoryConfiguration factoryConfiguration = (CacheConfiguration.CacheExtensionFactoryConfiguration)cacheExtensionConfiguration;
            CacheExtension cacheExtension = Cache.createCacheExtension(factoryConfiguration, cache, loader);
            cache.registerCacheExtension(cacheExtension);
        }
    }

    private static void registerCacheLoaders(CacheConfiguration cacheConfiguration, Ehcache cache, ClassLoader loader) {
        List cacheLoaderConfigurations = cacheConfiguration.getCacheLoaderConfigurations();
        for (Object cacheLoaderConfiguration : cacheLoaderConfigurations) {
            CacheConfiguration.CacheLoaderFactoryConfiguration factoryConfiguration = (CacheConfiguration.CacheLoaderFactoryConfiguration)cacheLoaderConfiguration;
            CacheLoader cacheLoader = Cache.createCacheLoader(factoryConfiguration, cache, loader);
            cache.registerCacheLoader(cacheLoader);
        }
    }

    private static void registerCacheWriter(CacheConfiguration cacheConfiguration, Ehcache cache, ClassLoader loader) {
        CacheWriterConfiguration config = cacheConfiguration.getCacheWriterConfiguration();
        if (config != null) {
            CacheWriter cacheWriter = Cache.createCacheWriter(config, cache, loader);
            cache.registerCacheWriter(cacheWriter);
        }
    }

    private static CacheEventListener createCacheEventListener(CacheConfiguration.CacheEventListenerFactoryConfiguration factoryConfiguration, ClassLoader loader) {
        String className = null;
        CacheEventListener cacheEventListener = null;
        if (factoryConfiguration != null) {
            className = factoryConfiguration.getFullyQualifiedClassPath();
        }
        if (className == null) {
            LOG.debug("CacheEventListener factory not configured. Skipping...");
        } else {
            CacheEventListenerFactory factory = (CacheEventListenerFactory)ClassLoaderUtil.createNewInstance(loader, className);
            Properties properties = PropertyUtil.parseProperties(factoryConfiguration.getProperties(), factoryConfiguration.getPropertySeparator());
            cacheEventListener = factory.createCacheEventListener(properties);
        }
        return cacheEventListener;
    }

    private static CacheExtension createCacheExtension(CacheConfiguration.CacheExtensionFactoryConfiguration factoryConfiguration, Ehcache cache, ClassLoader loader) {
        String className = null;
        CacheExtension cacheExtension = null;
        if (factoryConfiguration != null) {
            className = factoryConfiguration.getFullyQualifiedClassPath();
        }
        if (className == null) {
            LOG.debug("CacheExtension factory not configured. Skipping...");
        } else {
            CacheExtensionFactory factory = (CacheExtensionFactory)ClassLoaderUtil.createNewInstance(loader, className);
            Properties properties = PropertyUtil.parseProperties(factoryConfiguration.getProperties(), factoryConfiguration.getPropertySeparator());
            cacheExtension = factory.createCacheExtension(cache, properties);
        }
        return cacheExtension;
    }

    private static CacheLoader createCacheLoader(CacheConfiguration.CacheLoaderFactoryConfiguration factoryConfiguration, Ehcache cache, ClassLoader loader) {
        String className = null;
        CacheLoader cacheLoader = null;
        if (factoryConfiguration != null) {
            className = factoryConfiguration.getFullyQualifiedClassPath();
        }
        if (className == null) {
            LOG.debug("CacheLoader factory not configured. Skipping...");
        } else {
            CacheLoaderFactory factory = (CacheLoaderFactory)ClassLoaderUtil.createNewInstance(loader, className);
            Properties properties = PropertyUtil.parseProperties(factoryConfiguration.getProperties(), factoryConfiguration.getPropertySeparator());
            cacheLoader = factory.createCacheLoader(cache, properties);
        }
        return cacheLoader;
    }

    private static CacheWriter createCacheWriter(CacheWriterConfiguration config, Ehcache cache, ClassLoader loader) {
        String className = null;
        CacheWriter cacheWriter = null;
        CacheWriterConfiguration.CacheWriterFactoryConfiguration factoryConfiguration = config.getCacheWriterFactoryConfiguration();
        if (factoryConfiguration != null) {
            className = factoryConfiguration.getFullyQualifiedClassPath();
        }
        if (null == className) {
            LOG.debug("CacheWriter factory not configured. Skipping...");
        } else {
            CacheWriterFactory factory = (CacheWriterFactory)ClassLoaderUtil.createNewInstance(loader, className);
            Properties properties = PropertyUtil.parseProperties(factoryConfiguration.getProperties(), factoryConfiguration.getPropertySeparator());
            if (null == properties) {
                properties = new Properties();
            }
            cacheWriter = factory.createCacheWriter(cache, properties);
        }
        return cacheWriter;
    }

    private static final BootstrapCacheLoader createBootstrapCacheLoader(CacheConfiguration.BootstrapCacheLoaderFactoryConfiguration factoryConfiguration, ClassLoader loader) throws CacheException {
        String className = null;
        BootstrapCacheLoader bootstrapCacheLoader = null;
        if (factoryConfiguration != null) {
            className = factoryConfiguration.getFullyQualifiedClassPath();
        }
        if (className != null && className.length() != 0) {
            BootstrapCacheLoaderFactory factory = (BootstrapCacheLoaderFactory)ClassLoaderUtil.createNewInstance(loader, className);
            Properties properties = PropertyUtil.parseProperties(factoryConfiguration.getProperties(), factoryConfiguration.getPropertySeparator());
            return factory.createBootstrapCacheLoader(properties);
        }
        LOG.debug("No BootstrapCacheLoaderFactory class specified. Skipping...");
        return bootstrapCacheLoader;
    }

    public TransactionManagerLookup getTransactionManagerLookup() {
        return this.transactionManagerLookup;
    }

    @Override
    public void setTransactionManagerLookup(TransactionManagerLookup lookup) {
        TransactionManagerLookup oldValue = this.getTransactionManagerLookup();
        this.transactionManagerLookup = lookup;
        this.firePropertyChange("TransactionManagerLookup", oldValue, lookup);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void initialise() {
        Cache cache = this;
        synchronized (cache) {
            Store store;
            Pool onDiskPool;
            Pool onHeapPool;
            final ClassLoader loader = this.getCacheConfiguration().getClassLoader();
            if (loader != this.cacheManager.getConfiguration().getClassLoader() && !this.getName().startsWith("local_shadow_cache_for_")) {
                throw new CacheException("This cache (" + this.getName() + ") is configurated with a different classloader reference than its containing cache manager");
            }
            if (!this.cacheStatus.canInitialize()) {
                throw new IllegalStateException("Cannot initialise the " + this.configuration.getName() + " cache because its status is not STATUS_UNINITIALISED");
            }
            if (this.configuration.getMaxBytesLocalHeap() > 0L) {
                LOG.warn("Size based cache capacity constraints at heap tier (maxBytesLocalHeap) is deprecated now and not expected to work from Java 17 onwards. Consider maxEntriesLocalHeap instead");
                FromLargestCachePoolEvictor evictor = new FromLargestCachePoolEvictor();
                SizeOfEngine sizeOfEngine = this.cacheManager.createSizeOfEngine(this);
                onHeapPool = new BoundedPool(this.configuration.getMaxBytesLocalHeap(), evictor, sizeOfEngine);
            } else {
                onHeapPool = this.getCacheManager() != null && this.getCacheManager().getConfiguration().isMaxBytesLocalHeapSet() ? this.getCacheManager().getOnHeapPool() : new UnboundedPool();
            }
            if (this.configuration.getMaxBytesLocalDisk() > 0L) {
                FromLargestCachePoolEvictor evictor = new FromLargestCachePoolEvictor();
                onDiskPool = new BoundedPool(this.configuration.getMaxBytesLocalDisk(), evictor, null);
            } else {
                onDiskPool = this.getCacheManager() != null && this.getCacheManager().getConfiguration().isMaxBytesLocalDiskSet() ? this.getCacheManager().getOnDiskPool() : new UnboundedPool();
            }
            this.configListener = new AbstractCacheConfigurationListener(){

                @Override
                public void maxBytesLocalHeapChanged(long oldValue, long newValue) {
                    onHeapPool.setMaxSize(newValue);
                }

                @Override
                public void maxBytesLocalDiskChanged(long oldValue, long newValue) {
                    onDiskPool.setMaxSize(newValue);
                }
            };
            this.configuration.addConfigurationListener(this.configListener);
            if (this.isTerracottaClustered()) {
                this.checkClusteredConfig();
                int maxConcurrency = Integer.getInteger(EHCACHE_CLUSTERREDSTORE_MAX_CONCURRENCY_PROP, 4096);
                if (this.getCacheConfiguration().getTerracottaConfiguration().getConcurrency() > maxConcurrency) {
                    throw new InvalidConfigurationException("Maximum supported concurrency for Terracotta clustered caches is " + maxConcurrency + ". Please reconfigure cache '" + this.getName() + "' with concurrency value <= " + maxConcurrency + " or use system property 'ehcache.clusteredStore.maxConcurrency' to override the default");
                }
                this.elementValueComparator = this.configuration.getElementValueComparatorConfiguration().createElementComparatorInstance(this.configuration, loader);
                Callable<TerracottaStore> callable = new Callable<TerracottaStore>(){

                    @Override
                    public TerracottaStore call() throws Exception {
                        Cache.this.cacheManager.getClusteredInstanceFactory().linkClusteredCacheManager(Cache.this.cacheManager.getName(), Cache.this.cacheManager.getConfiguration());
                        Store tempStore = null;
                        try {
                            tempStore = Cache.this.cacheManager.createTerracottaStore(Cache.this);
                        }
                        catch (IllegalArgumentException e) {
                            Cache.this.handleExceptionInTerracottaStoreCreation(e);
                        }
                        if (!(tempStore instanceof TerracottaStore)) {
                            throw new CacheException("CacheManager should create instances of TerracottaStore for Terracotta Clustered caches instead of - " + (tempStore == null ? "null" : tempStore.getClass().getName()));
                        }
                        CacheConfiguration.TransactionalMode clusteredTransactionalMode = ((TerracottaStore)tempStore).getTransactionalMode();
                        if (clusteredTransactionalMode != null && !clusteredTransactionalMode.equals((Object)Cache.this.getCacheConfiguration().getTransactionalMode())) {
                            throw new InvalidConfigurationException("Transactional mode cannot be changed on clustered caches. Please reconfigure cache '" + Cache.this.getName() + "' with transactionalMode = " + clusteredTransactionalMode);
                        }
                        TerracottaStore terracottaStore = Cache.this.makeClusteredTransactionalIfNeeded((TerracottaStore)tempStore, Cache.this.elementValueComparator, loader);
                        if (Cache.this.isSearchable()) {
                            HashMap<String, AttributeExtractor> extractors = new HashMap<String, AttributeExtractor>();
                            for (SearchAttribute sa : Cache.this.configuration.getSearchAttributes().values()) {
                                extractors.put(sa.getName(), sa.constructExtractor(loader));
                            }
                            terracottaStore.setAttributeExtractors(extractors);
                        }
                        return terracottaStore;
                    }
                };
                NonstopConfiguration nonstopConfig = this.getCacheConfiguration().getTerracottaConfiguration().getNonstopConfiguration();
                if (nonstopConfig != null) {
                    nonstopConfig.freezeConfig();
                }
                store = this.cacheManager.getClusteredInstanceFactory().createNonStopStore(callable, this);
                this.clusterStateListener = new CacheClusterStateStatisticsListener(this);
                this.getCacheCluster().addTopologyListener(this.clusterStateListener);
            } else {
                FeaturesManager featuresManager = this.cacheManager.getFeaturesManager();
                if (featuresManager == null) {
                    if (this.configuration.isOverflowToOffHeap()) {
                        throw new CacheException("Cache " + this.configuration.getName() + " cannot be configured because the enterprise features manager could not be found. You must use an enterprise version of Ehcache to successfully enable overflowToOffHeap.");
                    }
                    PersistenceConfiguration persistence = this.configuration.getPersistenceConfiguration();
                    if (persistence != null && PersistenceConfiguration.Strategy.LOCALRESTARTABLE.equals((Object)persistence.getStrategy())) {
                        throw new CacheException("Cache " + this.configuration.getName() + " cannot be configured because the enterprise features manager could not be found. You must use an enterprise version of Ehcache to successfully enable enterprise persistence.");
                    }
                    if (this.useClassicLru && this.configuration.getMemoryStoreEvictionPolicy().equals(MemoryStoreEvictionPolicy.LRU)) {
                        DiskStore disk = this.createDiskStore();
                        store = new LegacyStoreWrapper(new LruMemoryStore(this, disk), disk, this.registeredEventListeners, this.configuration);
                    } else {
                        store = this.configuration.isOverflowToDisk() ? DiskStore.createCacheStore(this, onHeapPool, onDiskPool) : MemoryStore.create(this, onHeapPool);
                    }
                } else {
                    try {
                        store = featuresManager.createStore(this, onHeapPool, onDiskPool);
                    }
                    catch (IllegalStateException e) {
                        throw new CacheException(e.getMessage(), e);
                    }
                }
                store = this.handleTransactionalAndCopy(store, loader);
            }
            this.compoundStore = store;
            if (!this.isTerracottaClustered() && this.isSearchable()) {
                HashMap<String, AttributeExtractor> extractors = new HashMap<String, AttributeExtractor>();
                for (SearchAttribute sa : this.configuration.getSearchAttributes().values()) {
                    extractors.put(sa.getName(), sa.constructExtractor(loader));
                }
                this.compoundStore.setAttributeExtractors(extractors);
            }
            this.cacheWriterManager = this.configuration.getCacheWriterConfiguration().getWriteMode().createWriterManager(this, this.compoundStore);
            StatisticsManager.associate(this).withChild(this.cacheWriterManager);
            this.cacheStatus.changeState(Status.STATUS_ALIVE);
            this.initialiseRegisteredCacheWriter();
            this.initialiseCacheWriterManager(false);
            this.initialiseRegisteredCacheExtensions();
            this.initialiseRegisteredCacheLoaders();
            Object context = this.compoundStore.getInternalContext();
            this.lockProvider = context instanceof CacheLockProvider ? (CacheLockProvider)context : new StripedReadWriteLockSync(2048);
            StatisticsManager.associate(this).withChild(this.compoundStore);
            this.statistics = new StatisticsGateway(this, this.cacheManager.getStatisticsExecutor());
        }
        if (!this.isTerracottaClustered()) {
            this.compoundStore.addStoreListener(this);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initialised cache: " + this.configuration.getName());
        }
        if (this.disabled) {
            LOG.warn("Cache: " + this.configuration.getName() + " is disabled because the net.sf.ehcache.disabled property was set to true. No elements will be added to the cache.");
        }
    }

    private Store handleTransactionalAndCopy(Store store, ClassLoader loader) {
        Store wrappedStore;
        if (this.configuration.getTransactionalMode().isTransactional()) {
            this.elementValueComparator = TxCopyingCacheStore.wrap(this.configuration.getElementValueComparatorConfiguration().createElementComparatorInstance(this.configuration, loader), this.configuration);
            wrappedStore = TxCopyingCacheStore.wrapTxStore(this.makeTransactional(store), this.configuration);
        } else {
            this.elementValueComparator = CopyingCacheStore.wrapIfCopy(this.configuration.getElementValueComparatorConfiguration().createElementComparatorInstance(this.configuration, loader), this.configuration);
            wrappedStore = CopyingCacheStore.wrapIfCopy(store, this.configuration);
        }
        return wrappedStore;
    }

    private void handleExceptionInTerracottaStoreCreation(IllegalArgumentException e) {
        if (e.getMessage().contains("copyOnReadEnabled")) {
            throw new InvalidConfigurationException("Conflict in configuration for clustered cache " + this.getName() + " . Source is either copyOnRead or transactional mode setting.");
        }
        throw new InvalidConfigurationException("Conflict in configuration for clustered cache " + this.getName() + " : " + e.getMessage());
    }

    private void checkClusteredConfig() {
        TerracottaConfiguration.Consistency consistency = this.getCacheConfiguration().getTerracottaConfiguration().getConsistency();
        boolean coherent = this.getCacheConfiguration().getTerracottaConfiguration().isCoherent();
        if (this.getCacheConfiguration().getTerracottaConfiguration().isSynchronousWrites() && consistency == TerracottaConfiguration.Consistency.EVENTUAL) {
            throw new InvalidConfigurationException("Terracotta clustered caches with eventual consistency and synchronous writes are not supported yet. You can fix this by either making the cache in 'strong' consistency mode (<terracotta consistency=\"strong\"/>) or turning off synchronous writes.");
        }
        if (this.getCacheConfiguration().getTransactionalMode().isTransactional() && consistency == TerracottaConfiguration.Consistency.EVENTUAL) {
            throw new InvalidConfigurationException("Consistency should be " + TerracottaConfiguration.Consistency.STRONG + " when cache is configured with transactions enabled. You can fix this by either making the cache in 'strong' consistency mode (<terracotta consistency=\"strong\"/>) or turning off transactions.");
        }
        if (this.getCacheConfiguration().getTransactionalMode().isTransactional() && !this.getCacheConfiguration().getTransactionalMode().equals((Object)CacheConfiguration.TransactionalMode.XA_STRICT) && this.getCacheConfiguration().getTerracottaConfiguration().isNonstopEnabled()) {
            LOG.warn("Cache: " + this.configuration.getName() + " configured both NonStop and transactional non xa_strict. NonStop features won't work for this cache!");
        }
        if (coherent && consistency == TerracottaConfiguration.Consistency.EVENTUAL || !coherent && consistency == TerracottaConfiguration.Consistency.STRONG) {
            throw new InvalidConfigurationException("Coherent and consistency attribute values are conflicting. Please remove the coherent attribute as its deprecated.");
        }
    }

    private AbstractTransactionStore makeTransactional(Store store) {
        AbstractTransactionStore wrappedStore;
        if (this.configuration.isXaStrictTransactional()) {
            if (this.transactionManagerLookup.getTransactionManager() == null) {
                throw new CacheException("You've configured cache " + this.cacheManager.getName() + "." + this.configuration.getName() + " to be transactional, but no TransactionManager could be found!");
            }
            if (this.configuration.isTerracottaClustered()) {
                this.configuration.getTerracottaConfiguration().setCacheXA(true);
            }
            SoftLockManager softLockManager = this.cacheManager.createSoftLockManager(this);
            TransactionIDFactory transactionIDFactory = this.cacheManager.getOrCreateTransactionIDFactory();
            wrappedStore = new XATransactionStore(this.transactionManagerLookup, softLockManager, transactionIDFactory, this, store, this.elementValueComparator);
        } else if (this.configuration.isXaTransactional()) {
            SoftLockManager softLockManager = this.cacheManager.createSoftLockManager(this);
            LocalTransactionStore localTransactionStore = new LocalTransactionStore(this.getCacheManager().getTransactionController(), this.getCacheManager().getOrCreateTransactionIDFactory(), softLockManager, this, store, this.elementValueComparator);
            wrappedStore = new JtaLocalTransactionStore(localTransactionStore, this.transactionManagerLookup, this.cacheManager.getTransactionController());
        } else if (this.configuration.isLocalTransactional()) {
            SoftLockManager softLockManager = this.cacheManager.createSoftLockManager(this);
            wrappedStore = new LocalTransactionStore(this.getCacheManager().getTransactionController(), this.getCacheManager().getOrCreateTransactionIDFactory(), softLockManager, this, store, this.elementValueComparator);
        } else {
            throw new IllegalStateException("Method should called only with a transactional configuration");
        }
        return wrappedStore;
    }

    private TerracottaStore makeClusteredTransactionalIfNeeded(TerracottaStore store, ElementValueComparator comparator, ClassLoader loader) {
        TerracottaStore wrappedStore;
        if (this.configuration.getTransactionalMode().isTransactional()) {
            if (this.configuration.isXaStrictTransactional()) {
                if (this.transactionManagerLookup.getTransactionManager() == null) {
                    throw new CacheException("You've configured cache " + this.cacheManager.getName() + "." + this.configuration.getName() + " to be transactional, but no TransactionManager could be found!");
                }
                if (this.configuration.isTerracottaClustered()) {
                    this.configuration.getTerracottaConfiguration().setCacheXA(true);
                }
                SoftLockManager softLockManager = this.cacheManager.createSoftLockManager(this);
                TransactionIDFactory transactionIDFactory = this.cacheManager.getOrCreateTransactionIDFactory();
                wrappedStore = new XATransactionStore(this.transactionManagerLookup, softLockManager, transactionIDFactory, this, store, comparator);
            } else if (this.configuration.isXaTransactional()) {
                SoftLockManager softLockManager = this.cacheManager.createSoftLockManager(this);
                LocalTransactionStore localTransactionStore = new LocalTransactionStore(this.getCacheManager().getTransactionController(), this.getCacheManager().getOrCreateTransactionIDFactory(), softLockManager, this, store, comparator);
                wrappedStore = new JtaLocalTransactionStore(localTransactionStore, this.transactionManagerLookup, this.cacheManager.getTransactionController());
            } else if (this.configuration.isLocalTransactional()) {
                SoftLockManager softLockManager = this.cacheManager.createSoftLockManager(this);
                wrappedStore = new LocalTransactionStore(this.getCacheManager().getTransactionController(), this.getCacheManager().getOrCreateTransactionIDFactory(), softLockManager, this, store, comparator);
            } else {
                throw new IllegalStateException("Should not get there");
            }
            wrappedStore = new TerracottaTransactionalCopyingCacheStore(wrappedStore, new ReadWriteSerializationCopyStrategy(), loader);
        } else {
            wrappedStore = store;
        }
        return wrappedStore;
    }

    private CacheCluster getCacheCluster() {
        CacheCluster cacheCluster;
        try {
            cacheCluster = this.getCacheManager().getCluster(ClusterScheme.TERRACOTTA);
        }
        catch (ClusterSchemeNotAvailableException e) {
            LOG.info("Terracotta ClusterScheme is not available, using ClusterScheme.NONE");
            cacheCluster = this.getCacheManager().getCluster(ClusterScheme.NONE);
        }
        return cacheCluster;
    }

    private void initialiseCacheWriterManager(boolean imperative) throws CacheException {
        if (!this.cacheWriterManagerInitFlag.get()) {
            this.cacheWriterManagerInitLock.lock();
            try {
                if (!this.cacheWriterManagerInitFlag.get()) {
                    if (this.cacheWriterManager != null && this.registeredCacheWriter != null) {
                        this.cacheWriterManager.init(this);
                        this.cacheWriterManagerInitFlag.set(true);
                    } else if (imperative) {
                        throw new CacheException("Cache: " + this.configuration.getName() + " was being used with cache writer features, but it wasn't properly registered beforehand.");
                    }
                }
            }
            finally {
                this.cacheWriterManagerInitLock.unlock();
            }
        }
    }

    @Override
    public CacheWriterManager getWriterManager() {
        return this.cacheWriterManager;
    }

    protected DiskStore createDiskStore() {
        if (this.isDiskStore()) {
            return DiskStore.create(this);
        }
        return null;
    }

    protected boolean isDiskStore() {
        return this.configuration.isOverflowToDisk();
    }

    public boolean isTerracottaClustered() {
        return this.configuration.isTerracottaClustered();
    }

    @Override
    public void bootstrap() {
        if (!this.disabled && this.bootstrapCacheLoader != null) {
            this.bootstrapCacheLoader.load(this);
        }
    }

    @Override
    public final void put(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.put(element, false);
    }

    @Override
    public void putAll(Collection<Element> elements) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.putAll(elements, false);
    }

    @Override
    public final void put(Element element, boolean doNotNotifyCacheReplicators) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.putInternal(element, doNotNotifyCacheReplicators, false);
    }

    private void putAll(Collection<Element> elements, boolean doNotNotifyCacheReplicators) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.putAllInternal(elements, doNotNotifyCacheReplicators);
    }

    @Override
    public void putWithWriter(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.putInternal(element, false, true);
    }

    private void putInternal(Element element, boolean doNotNotifyCacheReplicators, boolean useCacheWriter) {
        this.putObserver.begin();
        if (useCacheWriter) {
            this.initialiseCacheWriterManager(true);
        }
        this.checkStatus();
        if (this.disabled) {
            this.putObserver.end(CacheOperationOutcomes.PutOutcome.IGNORED);
            return;
        }
        if (element == null) {
            if (doNotNotifyCacheReplicators) {
                LOG.debug("Element from replicated put is null. This happens because the element is a SoftReference and it has been collected. Increase heap memory on the JVM or set -Xms to be the same as -Xmx to avoid this problem.");
            }
            this.putObserver.end(CacheOperationOutcomes.PutOutcome.IGNORED);
            return;
        }
        if (element.getObjectKey() == null) {
            this.putObserver.end(CacheOperationOutcomes.PutOutcome.IGNORED);
            return;
        }
        element.resetAccessStatistics();
        this.applyDefaultsToElementWithoutLifespanSet(element);
        this.backOffIfDiskSpoolFull();
        element.updateUpdateStatistics();
        boolean elementExists = false;
        if (useCacheWriter) {
            boolean notifyListeners = true;
            try {
                elementExists = !this.compoundStore.putWithWriter(element, this.cacheWriterManager);
            }
            catch (StoreUpdateException e) {
                elementExists = e.isUpdate();
                notifyListeners = this.configuration.getCacheWriterConfiguration().getNotifyListenersOnException();
                RuntimeException cause = e.getCause();
                if (cause instanceof CacheWriterManagerException) {
                    throw ((CacheWriterManagerException)cause).getCause();
                }
                throw cause;
            }
            finally {
                if (notifyListeners) {
                    this.notifyPutInternalListeners(element, doNotNotifyCacheReplicators, elementExists);
                }
            }
        } else {
            elementExists = !this.compoundStore.put(element);
            this.notifyPutInternalListeners(element, doNotNotifyCacheReplicators, elementExists);
        }
        this.putObserver.end(elementExists ? CacheOperationOutcomes.PutOutcome.UPDATED : CacheOperationOutcomes.PutOutcome.ADDED);
    }

    private void putAllInternal(Collection<Element> elements, boolean doNotNotifyCacheReplicators) {
        this.putAllObserver.begin();
        this.checkStatus();
        if (this.disabled || elements.isEmpty()) {
            this.putAllObserver.end(CacheOperationOutcomes.PutAllOutcome.IGNORED);
            return;
        }
        this.backOffIfDiskSpoolFull();
        this.compoundStore.putAll(elements);
        for (Element element : elements) {
            element.resetAccessStatistics();
            this.applyDefaultsToElementWithoutLifespanSet(element);
            this.notifyPutInternalListeners(element, doNotNotifyCacheReplicators, false);
        }
        this.putAllObserver.end(CacheOperationOutcomes.PutAllOutcome.COMPLETED);
    }

    private void notifyPutInternalListeners(Element element, boolean doNotNotifyCacheReplicators, boolean elementExists) {
        if (elementExists) {
            this.registeredEventListeners.notifyElementUpdated(element, doNotNotifyCacheReplicators);
        } else {
            this.registeredEventListeners.notifyElementPut(element, doNotNotifyCacheReplicators);
        }
    }

    private void backOffIfDiskSpoolFull() {
        if (this.compoundStore.bufferFull()) {
            try {
                Thread.sleep(50L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void applyDefaultsToElementWithoutLifespanSet(Element element) {
        if (!element.isLifespanSet()) {
            element.setLifespanDefaults(TimeUtil.convertTimeToInt(this.configuration.getTimeToIdleSeconds()), TimeUtil.convertTimeToInt(this.configuration.getTimeToLiveSeconds()), this.configuration.isEternal());
        }
    }

    @Override
    public final void putQuiet(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        this.checkStatus();
        if (this.disabled) {
            return;
        }
        if (element == null || element.getObjectKey() == null) {
            return;
        }
        this.applyDefaultsToElementWithoutLifespanSet(element);
        this.compoundStore.put(element);
    }

    @Override
    public final Element get(Serializable key) throws IllegalStateException, CacheException {
        return this.get((Object)key);
    }

    @Override
    public final Element get(Object key) throws IllegalStateException, CacheException {
        this.getObserver.begin();
        this.checkStatus();
        if (this.disabled) {
            this.getObserver.end(CacheOperationOutcomes.GetOutcome.MISS_NOT_FOUND);
            return null;
        }
        Element element = this.compoundStore.get(key);
        if (element == null) {
            this.getObserver.end(CacheOperationOutcomes.GetOutcome.MISS_NOT_FOUND);
            return null;
        }
        if (this.isExpired(element)) {
            this.tryRemoveImmediately(key, true);
            this.getObserver.end(CacheOperationOutcomes.GetOutcome.MISS_EXPIRED);
            return null;
        }
        if (!this.skipUpdateAccessStatistics(element)) {
            element.updateAccessStatistics();
        }
        this.getObserver.end(CacheOperationOutcomes.GetOutcome.HIT);
        return element;
    }

    @Override
    public Map<Object, Element> getAll(Collection<?> keys) throws IllegalStateException, CacheException {
        this.getAllObserver.begin();
        this.checkStatus();
        if (this.disabled) {
            return null;
        }
        if (keys.isEmpty()) {
            this.getAllObserver.end(CacheOperationOutcomes.GetAllOutcome.ALL_HIT, 0L, 0L);
            return Collections.EMPTY_MAP;
        }
        Map<Object, Element> elements = this.compoundStore.getAll(keys);
        HashSet<Object> expired = new HashSet<Object>();
        for (Map.Entry<Object, Element> entry : elements.entrySet()) {
            Object key = entry.getKey();
            Element element = entry.getValue();
            if (element == null) continue;
            if (this.isExpired(element)) {
                this.tryRemoveImmediately(key, true);
                expired.add(key);
                continue;
            }
            element.updateAccessStatistics();
        }
        if (!expired.isEmpty()) {
            try {
                elements.keySet().removeAll(expired);
            }
            catch (UnsupportedOperationException e) {
                elements = new HashMap<Object, Element>(elements);
                elements.keySet().removeAll(expired);
            }
        }
        int requests = keys.size();
        int hits = elements.size();
        if (hits == 0) {
            this.getAllObserver.end(CacheOperationOutcomes.GetAllOutcome.ALL_MISS, 0L, requests);
        } else if (requests == hits) {
            this.getAllObserver.end(CacheOperationOutcomes.GetAllOutcome.ALL_HIT, requests, 0L);
        } else {
            this.getAllObserver.end(CacheOperationOutcomes.GetAllOutcome.PARTIAL, hits, requests - hits);
        }
        return elements;
    }

    @Override
    public Element getWithLoader(Object key, CacheLoader loader, Object loaderArgument) throws CacheException {
        Element element = this.get(key);
        if (element != null) {
            return element;
        }
        if (this.registeredCacheLoaders.size() == 0 && loader == null) {
            return null;
        }
        try {
            Object value;
            element = this.getQuiet(key);
            if (element != null) {
                return element;
            }
            long cacheLoaderTimeoutMillis = this.configuration.getCacheLoaderTimeoutMillis();
            if (cacheLoaderTimeoutMillis > 0L) {
                Future<AtomicReference<Object>> future = this.asynchronousLoad(key, loader, loaderArgument);
                value = future.get(cacheLoaderTimeoutMillis, TimeUnit.MILLISECONDS).get();
            } else {
                value = this.loadValueUsingLoader(key, loader, loaderArgument);
            }
            if (value == null) {
                return this.getQuiet(key);
            }
            Element newElement = new Element(key, value);
            this.put(newElement, false);
            Element fromCache = this.getQuiet(key);
            if (fromCache == null) {
                return newElement;
            }
            return fromCache;
        }
        catch (TimeoutException e) {
            throw new LoaderTimeoutException("Timeout on load for key " + key, e);
        }
        catch (Exception e) {
            throw new CacheException("Exception on load for key " + key, e);
        }
    }

    @Override
    public void load(Object key) throws CacheException {
        if (this.registeredCacheLoaders.size() == 0) {
            LOG.debug("The CacheLoader is null. Returning.");
            return;
        }
        boolean existsOnCall = this.isKeyInCache(key);
        if (existsOnCall) {
            LOG.debug("The key {} exists in the cache. Returning.", key);
            return;
        }
        this.asynchronousPut(key, null, null);
    }

    @Override
    public Map getAllWithLoader(Collection keys, Object loaderArgument) throws CacheException {
        if (keys == null) {
            return new HashMap(0);
        }
        HashMap map = new HashMap(keys.size());
        ArrayList missingKeys = new ArrayList(keys.size());
        if (this.registeredCacheLoaders.size() > 0) {
            Object key = null;
            try {
                map = new HashMap(keys.size());
                for (Object key1 : keys) {
                    key = key1;
                    Element element = this.get(key);
                    if (element == null) {
                        missingKeys.add(key);
                        continue;
                    }
                    map.put(key, element.getObjectValue());
                }
                Future future = this.asynchronousLoadAll(missingKeys, loaderArgument);
                long cacheLoaderTimeoutMillis = this.configuration.getCacheLoaderTimeoutMillis();
                if (cacheLoaderTimeoutMillis > 0L) {
                    try {
                        future.get(cacheLoaderTimeoutMillis, TimeUnit.MILLISECONDS);
                    }
                    catch (TimeoutException e) {
                        throw new LoaderTimeoutException("Timeout on load for key " + key, e);
                    }
                } else {
                    future.get();
                }
                for (Object missingKey : missingKeys) {
                    key = missingKey;
                    Element element = this.get(key);
                    if (element != null) {
                        map.put(key, element.getObjectValue());
                        continue;
                    }
                    map.put(key, null);
                }
            }
            catch (InterruptedException e) {
                throw new CacheException(e.getMessage() + " for key " + key, e);
            }
            catch (ExecutionException e) {
                throw new CacheException(e.getMessage() + " for key " + key, e);
            }
        } else {
            for (Object key : keys) {
                Element element = this.get(key);
                if (element != null) {
                    map.put(key, element.getObjectValue());
                    continue;
                }
                map.put(key, null);
            }
        }
        return map;
    }

    @Override
    public void loadAll(Collection keys, Object argument) throws CacheException {
        if (this.registeredCacheLoaders.size() == 0) {
            LOG.debug("The CacheLoader is null. Returning.");
            return;
        }
        if (keys == null) {
            return;
        }
        this.asynchronousLoadAll(keys, argument);
    }

    @Override
    public final Element getQuiet(Serializable key) throws IllegalStateException, CacheException {
        return this.getQuiet((Object)key);
    }

    @Override
    public final Element getQuiet(Object key) throws IllegalStateException, CacheException {
        this.checkStatus();
        Element element = this.compoundStore.getQuiet(key);
        if (element == null) {
            return null;
        }
        if (this.isExpired(element)) {
            this.tryRemoveImmediately(key, false);
            return null;
        }
        return element;
    }

    @Override
    public final List getKeys() throws IllegalStateException, CacheException {
        this.checkStatus();
        return this.compoundStore.getKeys();
    }

    @Override
    public final List getKeysWithExpiryCheck() throws IllegalStateException, CacheException {
        List allKeyList = this.getKeys();
        ArrayList nonExpiredKeys = new ArrayList(allKeyList.size());
        for (Object key : allKeyList) {
            Element element = this.getQuiet(key);
            if (element == null) continue;
            nonExpiredKeys.add(key);
        }
        nonExpiredKeys.trimToSize();
        return nonExpiredKeys;
    }

    @Override
    public final List getKeysNoDuplicateCheck() throws IllegalStateException {
        this.checkStatus();
        return this.getKeys();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    private void tryRemoveImmediately(Object key, boolean notifyListeners) {
        boolean writeLocked;
        Sync syncForKey;
        block12: {
            if (this.configuration.isTerracottaClustered()) {
                return;
            }
            syncForKey = ((CacheLockProvider)this.getInternalContext()).getSyncForKey(key);
            writeLocked = false;
            try {
                writeLocked = syncForKey.tryLock(LockType.WRITE, 0L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            catch (LockOperationTimedOutNonstopException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Try lock attempt failed, inline expiry will not happen. Exception: " + e);
                }
            }
            catch (Error e) {
                if (e.getClass().getName().equals("com.tc.exception.TCLockUpgradeNotSupportedError")) break block12;
                throw e;
            }
        }
        if (writeLocked) {
            try {
                this.removeInternal(key, true, notifyListeners, false, false);
            }
            finally {
                syncForKey.unlock(LockType.WRITE);
            }
        } else if (LOG.isDebugEnabled()) {
            LOG.debug(this.configuration.getName() + " cache: element " + key + " expired, but couldn't be inline evicted");
        }
    }

    private boolean skipUpdateAccessStatistics(Element element) {
        if (this.configuration.isFrozen()) {
            boolean forLifetime = element.isEternal();
            boolean forHeap = this.configuration.getMaxEntriesLocalHeap() > 0L || this.configuration.getMaxBytesLocalHeap() > 0L || this.getCacheManager().getConfiguration().isMaxBytesLocalHeapSet();
            boolean forDisk = this.configuration.isOverflowToDisk() && (this.configuration.getMaxEntriesLocalDisk() > 0L || this.configuration.getMaxBytesLocalDisk() > 0L || this.getCacheManager().getConfiguration().isMaxBytesLocalDiskSet());
            return !forLifetime && !forHeap && !forDisk;
        }
        return false;
    }

    @Override
    public final boolean remove(Serializable key) throws IllegalStateException {
        return this.remove((Object)key);
    }

    @Override
    public final boolean remove(Object key) throws IllegalStateException {
        return this.remove(key, false);
    }

    @Override
    public final Element removeAndReturnElement(Object key) throws IllegalStateException {
        this.removeObserver.begin();
        try {
            Element element = this.removeInternal(key, false, true, false, false);
            return element;
        }
        finally {
            this.removeObserver.end(CacheOperationOutcomes.RemoveOutcome.SUCCESS);
        }
    }

    @Override
    public void removeAll(Collection<?> keys) throws IllegalStateException {
        this.removeAll(keys, false);
    }

    @Override
    public final void removeAll(Collection<?> keys, boolean doNotNotifyCacheReplicators) throws IllegalStateException {
        this.removeAllInternal(keys, false, true, doNotNotifyCacheReplicators);
    }

    @Override
    public final boolean remove(Serializable key, boolean doNotNotifyCacheReplicators) throws IllegalStateException {
        return this.remove((Object)key, doNotNotifyCacheReplicators);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final boolean remove(Object key, boolean doNotNotifyCacheReplicators) throws IllegalStateException {
        this.removeObserver.begin();
        try {
            boolean bl = this.removeInternal(key, false, true, doNotNotifyCacheReplicators, false) != null;
            return bl;
        }
        finally {
            this.removeObserver.end(CacheOperationOutcomes.RemoveOutcome.SUCCESS);
        }
    }

    @Override
    public final boolean removeQuiet(Serializable key) throws IllegalStateException {
        return this.removeInternal(key, false, false, false, false) != null;
    }

    @Override
    public final boolean removeQuiet(Object key) throws IllegalStateException {
        return this.removeInternal(key, false, false, false, false) != null;
    }

    @Override
    public boolean removeWithWriter(Object key) throws IllegalStateException {
        this.removeObserver.begin();
        try {
            boolean bl = this.removeInternal(key, false, true, false, true) != null;
            return bl;
        }
        finally {
            this.removeObserver.end(CacheOperationOutcomes.RemoveOutcome.SUCCESS);
        }
    }

    private Element removeInternal(Object key, boolean expiry, boolean notifyListeners, boolean doNotNotifyCacheReplicators, boolean useCacheWriter) throws IllegalStateException {
        if (useCacheWriter) {
            this.initialiseCacheWriterManager(true);
        }
        this.checkStatus();
        Element elementFromStore = null;
        if (useCacheWriter) {
            try {
                elementFromStore = this.compoundStore.removeWithWriter(key, this.cacheWriterManager);
            }
            catch (CacheWriterManagerException e) {
                if (this.configuration.getCacheWriterConfiguration().getNotifyListenersOnException()) {
                    this.notifyRemoveInternalListeners(key, expiry, notifyListeners, doNotNotifyCacheReplicators, elementFromStore);
                }
                throw e.getCause();
            }
        } else {
            elementFromStore = this.compoundStore.remove(key);
        }
        this.notifyRemoveInternalListeners(key, expiry, notifyListeners, doNotNotifyCacheReplicators, elementFromStore);
        return elementFromStore;
    }

    private boolean notifyRemoveInternalListeners(Object key, boolean expiry, boolean notifyListeners, boolean doNotNotifyCacheReplicators, Element elementFromStore) {
        boolean removed = false;
        boolean removeNotified = false;
        if (elementFromStore != null) {
            if (expiry) {
                this.registeredEventListeners.notifyElementExpiry(elementFromStore, doNotNotifyCacheReplicators);
            } else if (notifyListeners) {
                removeNotified = true;
                this.registeredEventListeners.notifyElementRemoved(elementFromStore, doNotNotifyCacheReplicators);
            }
            removed = true;
        }
        if (notifyListeners && !expiry && !removeNotified) {
            Element syntheticElement = new Element(key, null);
            this.registeredEventListeners.notifyElementRemoved(syntheticElement, doNotNotifyCacheReplicators);
        }
        return removed;
    }

    private void removeAllInternal(Collection<?> keys, boolean expiry, boolean notifyListeners, boolean doNotNotifyCacheReplicators) throws IllegalStateException {
        this.removeAllObserver.begin();
        this.checkStatus();
        if (this.disabled || keys.isEmpty()) {
            this.removeAllObserver.end(CacheOperationOutcomes.RemoveAllOutcome.IGNORED);
            return;
        }
        this.compoundStore.removeAll(keys);
        for (Object key : keys) {
            Element syntheticElement = new Element(key, null);
            this.notifyRemoveInternalListeners(key, false, notifyListeners, doNotNotifyCacheReplicators, syntheticElement);
        }
        this.removeAllObserver.end(CacheOperationOutcomes.RemoveAllOutcome.COMPLETED);
    }

    @Override
    public void removeAll() throws IllegalStateException, CacheException {
        this.removeAll(false);
    }

    @Override
    public void removeAll(boolean doNotNotifyCacheReplicators) throws IllegalStateException, CacheException {
        this.checkStatus();
        this.compoundStore.removeAll();
        this.logOnRemoveAllIfPinnedCache();
        this.registeredEventListeners.notifyRemoveAll(doNotNotifyCacheReplicators);
    }

    private void logOnRemoveAllIfPinnedCache() {
        PinningConfiguration pinningConfiguration = this.getCacheConfiguration().getPinningConfiguration();
        if (pinningConfiguration != null && PinningConfiguration.Store.INCACHE.equals((Object)pinningConfiguration.getStore())) {
            LOG.warn("Data availability impacted:\n****************************************************************************************\n************************** removeAll called on a pinned cache **************************\n****************************************************************************************");
        }
    }

    @Override
    public synchronized void dispose() throws IllegalStateException {
        if (this.checkStatusAlreadyDisposed()) {
            return;
        }
        if (this.bootstrapCacheLoader != null && this.bootstrapCacheLoader instanceof Disposable) {
            ((Disposable)((Object)this.bootstrapCacheLoader)).dispose();
        }
        if (this.executorService != null) {
            this.executorService.shutdown();
        }
        this.disposeRegisteredCacheExtensions();
        this.disposeRegisteredCacheLoaders();
        if (this.clusterStateListener != null) {
            this.getCacheCluster().removeTopologyListener(this.clusterStateListener);
        }
        if (this.cacheWriterManager != null) {
            this.cacheWriterManager.dispose();
        }
        this.disposeRegisteredCacheWriter();
        this.registeredEventListeners.dispose();
        this.configuration.removeConfigurationListener(this.configListener);
        this.configListener = null;
        if (this.compoundStore != null) {
            this.compoundStore.removeStoreListener(this);
            this.compoundStore.dispose();
            this.compoundStore = null;
        }
        this.lockProvider = null;
        if (this.cacheStatus.isAlive() && this.isTerracottaClustered()) {
            this.getCacheManager().getClusteredInstanceFactory().unlinkCache(this.getName());
        }
        if (this.statistics != null) {
            this.statistics.dispose();
        }
        this.cacheStatus.changeState(Status.STATUS_SHUTDOWN);
    }

    private void initialiseRegisteredCacheExtensions() {
        for (CacheExtension cacheExtension : this.registeredCacheExtensions) {
            cacheExtension.init();
        }
    }

    private void disposeRegisteredCacheExtensions() {
        for (CacheExtension cacheExtension : this.registeredCacheExtensions) {
            cacheExtension.dispose();
        }
    }

    private void initialiseRegisteredCacheLoaders() {
        for (CacheLoader cacheLoader : this.registeredCacheLoaders) {
            cacheLoader.init();
        }
    }

    private void disposeRegisteredCacheLoaders() {
        for (CacheLoader cacheLoader : this.registeredCacheLoaders) {
            cacheLoader.dispose();
        }
    }

    private void initialiseRegisteredCacheWriter() {
        CacheWriter writer = this.registeredCacheWriter;
        if (writer != null) {
            writer.init();
        }
    }

    private void disposeRegisteredCacheWriter() {
        CacheWriter writer = this.registeredCacheWriter;
        if (writer != null) {
            writer.dispose();
        }
    }

    @Override
    public CacheConfiguration getCacheConfiguration() {
        return this.configuration;
    }

    @Override
    public final synchronized void flush() throws IllegalStateException, CacheException {
        this.checkStatus();
        try {
            this.compoundStore.flush();
        }
        catch (IOException e) {
            throw new CacheException("Unable to flush cache: " + this.configuration.getName() + ". Initial cause was " + e.getMessage(), e);
        }
    }

    @Override
    @Statistic(name="size", tags={"cache"})
    public final int getSize() throws IllegalStateException, CacheException {
        this.checkStatus();
        if (this.isTerracottaClustered()) {
            return this.compoundStore.getTerracottaClusteredSize();
        }
        return this.compoundStore.getSize();
    }

    @Override
    @Deprecated
    public final long calculateInMemorySize() throws IllegalStateException, CacheException {
        this.checkStatus();
        return this.getStatistics().getLocalHeapSizeInBytes();
    }

    @Override
    public boolean hasAbortedSizeOf() {
        this.checkStatus();
        return this.compoundStore.hasAbortedSizeOf();
    }

    @Override
    @Deprecated
    public final long calculateOffHeapSize() throws IllegalStateException, CacheException {
        this.checkStatus();
        return this.getStatistics().getLocalOffHeapSizeInBytes();
    }

    @Override
    @Deprecated
    public final long calculateOnDiskSize() throws IllegalStateException, CacheException {
        this.checkStatus();
        return this.getStatistics().getLocalDiskSizeInBytes();
    }

    @Override
    @Deprecated
    public final long getMemoryStoreSize() throws IllegalStateException {
        this.checkStatus();
        return this.getStatistics().getLocalHeapSize();
    }

    @Override
    @Deprecated
    public long getOffHeapStoreSize() throws IllegalStateException {
        this.checkStatus();
        return this.getStatistics().getLocalOffHeapSize();
    }

    @Override
    @Deprecated
    public final int getDiskStoreSize() throws IllegalStateException {
        this.checkStatus();
        if (this.isTerracottaClustered()) {
            return (int)this.getStatistics().getRemoteSize();
        }
        return (int)this.getStatistics().getLocalDiskSize();
    }

    @Override
    public final Status getStatus() {
        return this.cacheStatus.getStatus();
    }

    private void checkStatus() throws IllegalStateException {
        this.cacheStatus.checkAlive(this.configuration);
    }

    private boolean checkStatusAlreadyDisposed() throws IllegalStateException {
        return this.cacheStatus.isShutdown();
    }

    @Override
    @ContextAttribute(value="name")
    public final String getName() {
        return this.configuration.getName();
    }

    @Override
    public final void setName(String name) throws IllegalArgumentException {
        if (!this.cacheStatus.isUninitialized()) {
            throw new IllegalStateException("Only uninitialised caches can have their names set.");
        }
        this.configuration.setName(name);
    }

    @Override
    public String toString() {
        StringBuilder dump = new StringBuilder();
        dump.append("[").append(" name = ").append(this.configuration.getName()).append(" status = ").append(this.cacheStatus.getStatus()).append(" eternal = ").append(this.configuration.isEternal()).append(" overflowToDisk = ").append(this.configuration.isOverflowToDisk()).append(" maxEntriesLocalHeap = ").append(this.configuration.getMaxEntriesLocalHeap()).append(" maxEntriesLocalDisk = ").append(this.configuration.getMaxEntriesLocalDisk()).append(" memoryStoreEvictionPolicy = ").append(this.configuration.getMemoryStoreEvictionPolicy()).append(" timeToLiveSeconds = ").append(this.configuration.getTimeToLiveSeconds()).append(" timeToIdleSeconds = ").append(this.configuration.getTimeToIdleSeconds()).append(" persistence = ").append(this.configuration.getPersistenceConfiguration() == null ? "none" : this.configuration.getPersistenceConfiguration().getStrategy()).append(" diskExpiryThreadIntervalSeconds = ").append(this.configuration.getDiskExpiryThreadIntervalSeconds()).append(this.registeredEventListeners).append(" maxBytesLocalHeap = ").append(this.configuration.getMaxBytesLocalHeap()).append(" overflowToOffHeap = ").append(this.configuration.isOverflowToOffHeap()).append(" maxBytesLocalOffHeap = ").append(this.configuration.getMaxBytesLocalOffHeap()).append(" maxBytesLocalDisk = ").append(this.configuration.getMaxBytesLocalDisk()).append(" pinned = ").append(this.configuration.getPinningConfiguration() != null ? this.configuration.getPinningConfiguration().getStore().name() : "false").append(" ]");
        return dump.toString();
    }

    @Override
    public final boolean isExpired(Element element) throws IllegalStateException, NullPointerException {
        this.checkStatus();
        return element.isExpired(this.configuration);
    }

    @Override
    public final Cache clone() throws CloneNotSupportedException {
        return new Cache(this);
    }

    final Store getStore() throws IllegalStateException {
        this.checkStatus();
        return this.compoundStore;
    }

    public final Object getStoreMBean() {
        return this.getStore().getMBean();
    }

    @Override
    public final RegisteredEventListeners getCacheEventNotificationService() {
        return this.registeredEventListeners;
    }

    @Override
    public final boolean isElementInMemory(Serializable key) {
        return this.isElementInMemory((Object)key);
    }

    @Override
    public final boolean isElementInMemory(Object key) {
        this.checkStatus();
        return this.compoundStore.containsKeyInMemory(key);
    }

    public final boolean isElementOffHeap(Object key) {
        this.checkStatus();
        return this.compoundStore.containsKeyOffHeap(key);
    }

    @Override
    public final boolean isElementOnDisk(Serializable key) {
        return this.isElementOnDisk((Object)key);
    }

    @Override
    public final boolean isElementOnDisk(Object key) {
        this.checkStatus();
        return this.compoundStore.containsKeyOnDisk(key);
    }

    @Override
    public final String getGuid() {
        return this.guid;
    }

    @Override
    public final CacheManager getCacheManager() {
        return this.cacheManager;
    }

    @Override
    public void evictExpiredElements() {
        this.checkStatus();
        this.compoundStore.expireElements();
    }

    @Override
    public boolean isKeyInCache(Object key) {
        if (key == null) {
            return false;
        }
        return this.compoundStore.containsKey(key);
    }

    @Override
    public boolean isValueInCache(Object value) {
        for (Object key : this.getKeys()) {
            Serializable elementValue;
            Element element = this.get(key);
            if (element == null || !((elementValue = element.getValue()) == null ? value == null : elementValue.equals(value))) continue;
            return true;
        }
        return false;
    }

    @Override
    public StatisticsGateway getStatistics() throws IllegalStateException {
        this.checkStatus();
        return this.statistics;
    }

    @Override
    public void setCacheManager(CacheManager cacheManager) {
        CacheManager oldValue = this.getCacheManager();
        this.cacheManager = cacheManager;
        this.firePropertyChange("CacheManager", oldValue, cacheManager);
    }

    @Override
    public BootstrapCacheLoader getBootstrapCacheLoader() {
        return this.bootstrapCacheLoader;
    }

    @Override
    public void setBootstrapCacheLoader(BootstrapCacheLoader bootstrapCacheLoader) throws CacheException {
        if (!this.cacheStatus.isUninitialized()) {
            throw new CacheException("A bootstrap cache loader can only be set before the cache is initialized. " + this.configuration.getName());
        }
        BootstrapCacheLoader oldValue = this.getBootstrapCacheLoader();
        this.bootstrapCacheLoader = bootstrapCacheLoader;
        this.firePropertyChange("BootstrapCacheLoader", oldValue, bootstrapCacheLoader);
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof Ehcache)) {
            return false;
        }
        Ehcache other = (Ehcache)object;
        return this.guid.equals(other.getGuid());
    }

    public int hashCode() {
        return this.guid.hashCode();
    }

    private String createGuid() {
        StringBuilder buffer = new StringBuilder().append(localhost).append("-").append(UUID.randomUUID());
        return buffer.toString();
    }

    @Override
    public void registerCacheExtension(CacheExtension cacheExtension) {
        this.registeredCacheExtensions.add(cacheExtension);
    }

    @Override
    public List<CacheExtension> getRegisteredCacheExtensions() {
        return this.registeredCacheExtensions;
    }

    @Override
    public void unregisterCacheExtension(CacheExtension cacheExtension) {
        cacheExtension.dispose();
        this.registeredCacheExtensions.remove(cacheExtension);
    }

    @Override
    public void setCacheExceptionHandler(CacheExceptionHandler cacheExceptionHandler) {
        CacheExceptionHandler oldValue = this.getCacheExceptionHandler();
        this.cacheExceptionHandler = cacheExceptionHandler;
        this.firePropertyChange("CacheExceptionHandler", oldValue, cacheExceptionHandler);
    }

    @Override
    public CacheExceptionHandler getCacheExceptionHandler() {
        return this.cacheExceptionHandler;
    }

    @Override
    public void registerCacheLoader(CacheLoader cacheLoader) {
        this.registeredCacheLoaders.add(cacheLoader);
    }

    @Override
    public void unregisterCacheLoader(CacheLoader cacheLoader) {
        this.registeredCacheLoaders.remove(cacheLoader);
    }

    @Override
    public List<CacheLoader> getRegisteredCacheLoaders() {
        return this.registeredCacheLoaders;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void registerCacheWriter(CacheWriter cacheWriter) {
        Cache cache = this;
        synchronized (cache) {
            this.registeredCacheWriter = cacheWriter;
            if (this.cacheStatus.isAlive()) {
                this.initialiseRegisteredCacheWriter();
            }
        }
        this.initialiseCacheWriterManager(false);
    }

    @Override
    public void unregisterCacheWriter() {
        if (this.cacheWriterManagerInitFlag.get()) {
            throw new CacheException("Cache: " + this.configuration.getName() + " has its cache writer being unregistered after it was already initialised.");
        }
        this.registeredCacheWriter = null;
    }

    @Override
    public CacheWriter getRegisteredCacheWriter() {
        return this.registeredCacheWriter;
    }

    @Override
    public void registerDynamicAttributesExtractor(DynamicAttributesExtractor extractor) {
        this.configuration.setDynamicAttributesExtractor(extractor);
    }

    Future asynchronousPut(final Object key, final CacheLoader specificLoader, final Object argument) {
        return this.getExecutorService().submit(new Runnable(){

            @Override
            public void run() throws CacheException {
                try {
                    Object value;
                    boolean existsOnRun = Cache.this.isKeyInCache(key);
                    if (!existsOnRun && (value = Cache.this.loadValueUsingLoader(key, specificLoader, argument)) != null) {
                        Cache.this.put(new Element(key, value), false);
                    }
                }
                catch (RuntimeException e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Problem during load. Load will not be completed. Cause was " + e.getCause(), (Throwable)e);
                    }
                    throw new CacheException("Problem during load. Load will not be completed. Cause was " + e.getCause(), e);
                }
            }
        });
    }

    Future<AtomicReference<Object>> asynchronousLoad(final Object key, final CacheLoader specificLoader, final Object argument) {
        final AtomicReference result = new AtomicReference();
        return this.getExecutorService().submit(new Runnable(){

            @Override
            public void run() throws CacheException {
                try {
                    Object value;
                    boolean existsOnRun = Cache.this.isKeyInCache(key);
                    if (!existsOnRun && (value = Cache.this.loadValueUsingLoader(key, specificLoader, argument)) != null) {
                        result.set(value);
                    }
                }
                catch (RuntimeException e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Problem during load. Load will not be completed. Cause was " + e.getCause(), (Throwable)e);
                    }
                    throw new CacheException("Problem during load. Load will not be completed. Cause was " + e.getCause(), e);
                }
            }
        }, result);
    }

    private Object loadValueUsingLoader(Object key, CacheLoader specificLoader, Object argument) {
        Object value = null;
        if (specificLoader != null) {
            value = argument == null ? specificLoader.load(key) : specificLoader.load(key, argument);
        } else if (!this.registeredCacheLoaders.isEmpty()) {
            value = this.loadWithRegisteredLoaders(argument, key);
        }
        return value;
    }

    private Object loadWithRegisteredLoaders(Object argument, Object key) throws CacheException {
        Object value = null;
        if (argument == null) {
            CacheLoader registeredCacheLoader;
            Iterator<CacheLoader> iterator = this.registeredCacheLoaders.iterator();
            while (iterator.hasNext() && (value = (registeredCacheLoader = iterator.next()).load(key)) == null) {
            }
        } else {
            CacheLoader registeredCacheLoader;
            Iterator<CacheLoader> iterator = this.registeredCacheLoaders.iterator();
            while (iterator.hasNext() && (value = (registeredCacheLoader = iterator.next()).load(key, argument)) == null) {
            }
        }
        return value;
    }

    Future asynchronousLoadAll(final Collection keys, final Object argument) {
        return this.getExecutorService().submit(new Runnable(){

            @Override
            public void run() {
                block4: {
                    try {
                        HashSet<Object> nonLoadedKeys = new HashSet<Object>();
                        for (Object key : keys) {
                            if (Cache.this.isKeyInCache(key)) continue;
                            nonLoadedKeys.add(key);
                        }
                        Map map = Cache.this.loadWithRegisteredLoaders(argument, nonLoadedKeys);
                        for (Map.Entry e : map.entrySet()) {
                            Cache.this.put(new Element(e.getKey(), e.getValue()));
                        }
                    }
                    catch (Throwable e) {
                        if (!LOG.isErrorEnabled()) break block4;
                        LOG.error("Problem during load. Load will not be completed. Cause was " + e.getCause(), e);
                    }
                }
            }
        });
    }

    Map loadWithRegisteredLoaders(Object argument, Set<Object> nonLoadedKeys) {
        HashMap result = new HashMap();
        for (CacheLoader registeredCacheLoader : this.registeredCacheLoaders) {
            if (nonLoadedKeys.isEmpty()) break;
            Map resultForThisCacheLoader = null;
            resultForThisCacheLoader = argument == null ? registeredCacheLoader.loadAll(nonLoadedKeys) : registeredCacheLoader.loadAll(nonLoadedKeys, argument);
            if (resultForThisCacheLoader == null) continue;
            nonLoadedKeys.removeAll(resultForThisCacheLoader.keySet());
            result.putAll(resultForThisCacheLoader);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ExecutorService getExecutorService() {
        if (this.executorService == null) {
            Cache cache = this;
            synchronized (cache) {
                if (VmUtils.isInGoogleAppEngine()) {
                    this.executorService = new AbstractExecutorService(){

                        @Override
                        public void execute(Runnable command) {
                            command.run();
                        }

                        @Override
                        public List<Runnable> shutdownNow() {
                            return Collections.emptyList();
                        }

                        @Override
                        public void shutdown() {
                        }

                        @Override
                        public boolean isTerminated() {
                            return this.isShutdown();
                        }

                        @Override
                        public boolean isShutdown() {
                            return false;
                        }

                        @Override
                        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
                            return true;
                        }
                    };
                } else {
                    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, EXECUTOR_MAXIMUM_POOL_SIZE, 60000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("Cache Executor Service", true));
                    threadPoolExecutor.allowCoreThreadTimeOut(true);
                    this.executorService = threadPoolExecutor;
                }
            }
        }
        return this.executorService;
    }

    @Override
    public boolean isDisabled() {
        return this.disabled;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDisabled(boolean disabled) {
        if (this.allowDisable) {
            boolean oldValue = this.isDisabled();
            if (oldValue != disabled) {
                Cache cache = this;
                synchronized (cache) {
                    this.disabled = disabled;
                }
                this.firePropertyChange("Disabled", oldValue, disabled);
            }
        } else {
            throw new CacheException("Dynamic cache features are disabled");
        }
    }

    public Policy getMemoryStoreEvictionPolicy() {
        this.checkStatus();
        return this.compoundStore.getInMemoryEvictionPolicy();
    }

    public void setMemoryStoreEvictionPolicy(Policy policy) {
        this.checkStatus();
        Policy oldValue = this.getMemoryStoreEvictionPolicy();
        this.compoundStore.setInMemoryEvictionPolicy(policy);
        this.firePropertyChange("MemoryStoreEvictionPolicy", oldValue, policy);
    }

    @Override
    public Object getInternalContext() {
        this.checkStatus();
        return this.compoundStore.getInternalContext();
    }

    @Override
    public void disableDynamicFeatures() {
        this.configuration.freezeConfiguration();
        this.allowDisable = false;
    }

    @Override
    @Deprecated
    public boolean isClusterCoherent() {
        return !this.isClusterBulkLoadEnabled();
    }

    @Override
    @Deprecated
    public boolean isNodeCoherent() {
        return !this.isNodeBulkLoadEnabled();
    }

    @Override
    @Deprecated
    public void setNodeCoherent(boolean coherent) {
        this.setNodeBulkLoadEnabled(!coherent);
    }

    @Override
    @Deprecated
    public void waitUntilClusterCoherent() {
        this.waitUntilClusterBulkLoadComplete();
    }

    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null && this.propertyChangeSupport != null) {
            this.propertyChangeSupport.removePropertyChangeListener(listener);
            this.propertyChangeSupport.addPropertyChangeListener(listener);
        }
    }

    @Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null && this.propertyChangeSupport != null) {
            this.propertyChangeSupport.removePropertyChangeListener(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeSupport pcs;
        Cache cache = this;
        synchronized (cache) {
            pcs = this.propertyChangeSupport;
        }
        if (pcs != null && (oldValue != null || newValue != null)) {
            pcs.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    @Override
    public Element putIfAbsent(Element element) throws NullPointerException {
        return this.putIfAbsent(element, false);
    }

    @Override
    public Element putIfAbsent(Element element, boolean doNotNotifyCacheReplicators) throws NullPointerException {
        this.checkStatus();
        if (element.getObjectKey() == null) {
            throw new NullPointerException();
        }
        if (this.disabled) {
            return null;
        }
        this.putIfAbsentObserver.begin();
        this.getQuiet(element.getObjectKey());
        element.resetAccessStatistics();
        this.applyDefaultsToElementWithoutLifespanSet(element);
        this.backOffIfDiskSpoolFull();
        element.updateUpdateStatistics();
        Element result = this.compoundStore.putIfAbsent(element);
        if (result == null) {
            this.notifyPutInternalListeners(element, doNotNotifyCacheReplicators, false);
            this.putIfAbsentObserver.end(CacheOperationOutcomes.PutIfAbsentOutcome.SUCCESS);
        } else {
            this.putIfAbsentObserver.end(CacheOperationOutcomes.PutIfAbsentOutcome.FAILURE);
        }
        return result;
    }

    @Override
    public boolean removeElement(Element element) throws NullPointerException {
        this.checkStatus();
        if (element.getObjectKey() == null) {
            throw new NullPointerException();
        }
        if (this.disabled) {
            return false;
        }
        this.removeElementObserver.begin();
        this.getQuiet(element.getObjectKey());
        Element result = this.compoundStore.removeElement(element, this.elementValueComparator);
        this.removeElementObserver.end(result == null ? CacheOperationOutcomes.RemoveElementOutcome.FAILURE : CacheOperationOutcomes.RemoveElementOutcome.SUCCESS);
        this.notifyRemoveInternalListeners(element.getObjectKey(), false, true, false, result);
        return result != null;
    }

    @Override
    public boolean replace(Element old, Element element) throws NullPointerException, IllegalArgumentException {
        this.checkStatus();
        if (old.getObjectKey() == null || element.getObjectKey() == null) {
            throw new NullPointerException();
        }
        if (!old.getObjectKey().equals(element.getObjectKey())) {
            throw new IllegalArgumentException("The keys for the element arguments to replace must be equal");
        }
        if (this.disabled) {
            return false;
        }
        this.replace2Observer.begin();
        this.getQuiet(old.getObjectKey());
        element.resetAccessStatistics();
        this.applyDefaultsToElementWithoutLifespanSet(element);
        this.backOffIfDiskSpoolFull();
        boolean result = this.compoundStore.replace(old, element, this.elementValueComparator);
        if (result) {
            element.updateUpdateStatistics();
            this.notifyPutInternalListeners(element, false, true);
            this.replace2Observer.end(CacheOperationOutcomes.ReplaceTwoArgOutcome.SUCCESS);
        } else {
            this.replace2Observer.end(CacheOperationOutcomes.ReplaceTwoArgOutcome.FAILURE);
        }
        return result;
    }

    @Override
    public Element replace(Element element) throws NullPointerException {
        this.checkStatus();
        if (element.getObjectKey() == null) {
            throw new NullPointerException();
        }
        if (this.disabled) {
            return null;
        }
        this.replace1Observer.begin();
        this.getQuiet(element.getObjectKey());
        element.resetAccessStatistics();
        this.applyDefaultsToElementWithoutLifespanSet(element);
        this.backOffIfDiskSpoolFull();
        Element result = this.compoundStore.replace(element);
        if (result != null) {
            element.updateUpdateStatistics();
            this.notifyPutInternalListeners(element, false, true);
            this.replace1Observer.end(CacheOperationOutcomes.ReplaceOneArgOutcome.SUCCESS);
        } else {
            this.replace1Observer.end(CacheOperationOutcomes.ReplaceOneArgOutcome.FAILURE);
        }
        return result;
    }

    @Override
    public void clusterCoherent(boolean clusterCoherent) {
        this.firePropertyChange("ClusterCoherent", !clusterCoherent, clusterCoherent);
    }

    @Override
    public Set<Attribute> getSearchAttributes() throws CacheException {
        this.checkStatus();
        return this.compoundStore.getSearchAttributes();
    }

    @Override
    public <T> Attribute<T> getSearchAttribute(String attributeName) throws CacheException {
        this.checkStatus();
        Attribute searchAttribute = this.compoundStore.getSearchAttribute(attributeName);
        if (searchAttribute == null) {
            String msg = attributeName.equals(Query.KEY.getAttributeName()) ? "Key search attribute is disabled for cache [" + this.getName() + "]. It can be enabled with <searchable keys=\"true\"..." : (attributeName.equals(Query.VALUE.getAttributeName()) ? "Value search attribute is disabled for cache [" + this.getName() + "]. It can be enabled with <searchable values=\"true\"..." : "No such search attribute [" + attributeName + "] defined for this cache [" + this.getName() + "]");
            throw new CacheException(msg);
        }
        return searchAttribute;
    }

    @Override
    public Query createQuery() {
        if (!this.isSearchable()) {
            throw new CacheException("This cache is not configured for search");
        }
        return new CacheQuery(this);
    }

    Results executeQuery(StoreQuery query) throws SearchException {
        this.searchObserver.begin();
        try {
            this.validateSearchQuery(query);
            Results results = this.compoundStore.executeQuery(query);
            this.searchObserver.end(CacheOperationOutcomes.SearchOutcome.SUCCESS);
            return results;
        }
        catch (SearchException e) {
            this.searchObserver.end(CacheOperationOutcomes.SearchOutcome.EXCEPTION);
            throw e;
        }
    }

    @Override
    public boolean isSearchable() {
        return this.configuration.isSearchable();
    }

    protected Sync getLockForKey(Object key) {
        this.checkStatus();
        return this.lockProvider.getSyncForKey(key);
    }

    private void acquireLockOnKey(Object key, LockType lockType) {
        Sync s = this.getLockForKey(key);
        s.lock(lockType);
    }

    private void releaseLockOnKey(Object key, LockType lockType) {
        Sync s = this.getLockForKey(key);
        s.unlock(lockType);
    }

    @Override
    public void acquireReadLockOnKey(Object key) {
        this.acquireLockOnKey(key, LockType.READ);
    }

    @Override
    public void acquireWriteLockOnKey(Object key) {
        this.acquireLockOnKey(key, LockType.WRITE);
    }

    @Override
    public boolean tryReadLockOnKey(Object key, long timeout) throws InterruptedException {
        Sync s = this.getLockForKey(key);
        return s.tryLock(LockType.READ, timeout);
    }

    @Override
    public boolean tryWriteLockOnKey(Object key, long timeout) throws InterruptedException {
        Sync s = this.getLockForKey(key);
        return s.tryLock(LockType.WRITE, timeout);
    }

    @Override
    public void releaseReadLockOnKey(Object key) {
        this.releaseLockOnKey(key, LockType.READ);
    }

    @Override
    public void releaseWriteLockOnKey(Object key) {
        this.releaseLockOnKey(key, LockType.WRITE);
    }

    @Override
    public boolean isReadLockedByCurrentThread(Object key) throws UnsupportedOperationException {
        return this.getLockForKey(key).isHeldByCurrentThread(LockType.READ);
    }

    @Override
    public boolean isWriteLockedByCurrentThread(Object key) {
        return this.getLockForKey(key).isHeldByCurrentThread(LockType.WRITE);
    }

    @Override
    public boolean isClusterBulkLoadEnabled() throws UnsupportedOperationException, TerracottaNotRunningException {
        this.checkStatus();
        return !this.compoundStore.isClusterCoherent();
    }

    @Override
    public boolean isNodeBulkLoadEnabled() throws UnsupportedOperationException, TerracottaNotRunningException {
        this.checkStatus();
        return !this.compoundStore.isNodeCoherent();
    }

    @Override
    public void setNodeBulkLoadEnabled(boolean enabledBulkLoad) throws UnsupportedOperationException, TerracottaNotRunningException {
        boolean oldValue = this.isNodeBulkLoadEnabled();
        if (oldValue != enabledBulkLoad) {
            this.compoundStore.setNodeCoherent(!enabledBulkLoad);
            this.firePropertyChange("NodeCoherent", oldValue, enabledBulkLoad);
        }
    }

    @Override
    public void waitUntilClusterBulkLoadComplete() throws UnsupportedOperationException, TerracottaNotRunningException {
        this.checkStatus();
        try {
            this.compoundStore.waitUntilClusterCoherent();
        }
        catch (InterruptedException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void recalculateSize(Object key) {
        this.checkStatus();
        this.compoundStore.recalculateSize(key);
    }

    private void validateSearchQuery(StoreQuery query) throws SearchException {
        if (!query.requestsKeys() && !query.requestsValues() && query.requestedAttributes().isEmpty() && query.getAggregatorInstances().isEmpty()) {
            String msg = "No results specified. Please specify one or more of includeKeys(), includeValues(), includeAggregator() or includeAttribute()";
            throw new SearchException(msg);
        }
        Set<Attribute<Attribute<?>>> groupBy = query.groupByAttributes();
        if (!groupBy.isEmpty()) {
            if (groupBy.contains(Query.KEY)) {
                throw new SearchException("Explicit grouping by element key not supported.");
            }
            if (groupBy.contains(Query.VALUE)) {
                throw new SearchException("Grouping by element value not supported.");
            }
            if (!groupBy.containsAll(query.requestedAttributes())) {
                throw new SearchException("Some of the requested attributes not used in group by clause.");
            }
            for (StoreQuery.Ordering order : query.getOrdering()) {
                if (groupBy.contains(order.getAttribute())) continue;
                throw new SearchException("All ordering attributes must be present in group by clause.");
            }
            if (query.requestsValues() || query.requestsKeys()) {
                throw new SearchException("It is not possible to include keys or values with group by queries.");
            }
        }
        Set<Attribute> supportedAttributes = this.getSearchAttributes();
        Cache.checkSearchAttributes(groupBy, supportedAttributes, "Query.addGroupBy");
        HashSet requestedAttributes = new HashSet(query.requestedAttributes());
        requestedAttributes.remove(Query.KEY);
        requestedAttributes.remove(Query.VALUE);
        Cache.checkSearchAttributes(requestedAttributes, supportedAttributes, "Query.includeAttributes");
        BaseCriteria bc = (BaseCriteria)query.getCriteria();
        Cache.checkSearchAttributes(bc.getAttributes(), supportedAttributes, "Query.addCriteria");
        HashSet sortAttributes = new HashSet();
        for (StoreQuery.Ordering order : query.getOrdering()) {
            sortAttributes.add(order.getAttribute());
        }
        Cache.checkSearchAttributes(sortAttributes, supportedAttributes, "Query.addOrderBy");
        HashSet aggrAttributes = new HashSet();
        for (AggregatorInstance<?> a : query.getAggregatorInstances()) {
            Attribute<?> attr = a.getAttribute();
            if (attr == null) continue;
            aggrAttributes.add(attr);
        }
        Cache.checkSearchAttributes(aggrAttributes, supportedAttributes, "Query.includeAggregator");
    }

    private static void checkSearchAttributes(Set<Attribute<?>> requestedAttrs, Set<Attribute> supportedAttrs, String src) {
        for (Attribute<?> attribute : requestedAttrs) {
            if (attribute == null) {
                throw new NullPointerException("null attribute");
            }
            if (supportedAttrs.contains(attribute)) continue;
            throw new UnknownAttributeException("Search attribute referenced in " + src + " unknown: " + attribute);
        }
    }

    static {
        EXECUTOR_MAXIMUM_POOL_SIZE = Math.min(10, Runtime.getRuntime().availableProcessors());
        try {
            localhost = InetAddress.getLocalHost();
        }
        catch (UnknownHostException e) {
            LOG.error("Unable to set localhost. This prevents creation of a GUID. Cause was: " + e.getMessage(), (Throwable)e);
        }
        catch (NoClassDefFoundError e) {
            LOG.debug("InetAddress is being blocked by your runtime environment. e.g. Google App Engine. Ehcache will work as a local cache.");
        }
    }

    private static class CacheStatus {
        private volatile Status status = Status.STATUS_UNINITIALISED;

        private CacheStatus() {
        }

        public void checkAlive(CacheConfiguration configuration) {
            Status readStatus = this.status;
            if (readStatus != Status.STATUS_ALIVE) {
                throw new IllegalStateException("The " + configuration.getName() + " Cache is not alive (" + readStatus + ")");
            }
        }

        public boolean canInitialize() {
            return this.status == Status.STATUS_UNINITIALISED;
        }

        public void changeState(Status newState) {
            this.status = newState;
        }

        public Status getStatus() {
            return this.status;
        }

        public boolean isAlive() {
            return this.status == Status.STATUS_ALIVE;
        }

        public boolean isShutdown() {
            return this.status == Status.STATUS_SHUTDOWN;
        }

        public boolean isUninitialized() {
            return this.status == Status.STATUS_UNINITIALISED;
        }
    }
}

