/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.DiskStorePathManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.FeaturesManager;
import net.sf.ehcache.ObjectExistsException;
import net.sf.ehcache.Status;
import net.sf.ehcache.TransactionController;
import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.cluster.ClusterScheme;
import net.sf.ehcache.cluster.ClusterSchemeNotAvailableException;
import net.sf.ehcache.cluster.NoopCacheCluster;
import net.sf.ehcache.concurrent.ConcurrencyUtil;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.config.ConfigurationHelper;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.InvalidConfigurationException;
import net.sf.ehcache.config.ManagementRESTServiceConfiguration;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import net.sf.ehcache.config.generator.ConfigurationSource;
import net.sf.ehcache.config.generator.ConfigurationUtil;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheManagerEventListener;
import net.sf.ehcache.event.CacheManagerEventListenerRegistry;
import net.sf.ehcache.management.ManagementServerLoader;
import net.sf.ehcache.management.event.DelegatingManagementEventSink;
import net.sf.ehcache.management.event.ManagementEventSink;
import net.sf.ehcache.management.provider.MBeanRegistrationProvider;
import net.sf.ehcache.management.provider.MBeanRegistrationProviderException;
import net.sf.ehcache.management.provider.MBeanRegistrationProviderFactory;
import net.sf.ehcache.management.provider.MBeanRegistrationProviderFactoryImpl;
import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.pool.SizeOfEngine;
import net.sf.ehcache.pool.SizeOfEngineLoader;
import net.sf.ehcache.pool.impl.BalancedAccessEvictor;
import net.sf.ehcache.pool.impl.BoundedPool;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.terracotta.ClusteredInstanceFactory;
import net.sf.ehcache.terracotta.TerracottaClient;
import net.sf.ehcache.transaction.DelegatingTransactionIDFactory;
import net.sf.ehcache.transaction.ReadCommittedSoftLockFactory;
import net.sf.ehcache.transaction.SoftLockManager;
import net.sf.ehcache.transaction.SoftLockManagerImpl;
import net.sf.ehcache.transaction.TransactionIDFactory;
import net.sf.ehcache.transaction.manager.TransactionManagerLookup;
import net.sf.ehcache.transaction.xa.processor.XARequestProcessor;
import net.sf.ehcache.util.FailSafeTimer;
import net.sf.ehcache.util.PropertyUtil;
import net.sf.ehcache.writer.writebehind.WriteBehind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.statistics.StatisticsManager;

public class CacheManager {
    public static final String DEFAULT_NAME = "__DEFAULT__";
    public static final double ON_HEAP_THRESHOLD = 0.8;
    public static final List<CacheManager> ALL_CACHE_MANAGERS = new CopyOnWriteArrayList<CacheManager>();
    public static final String ENABLE_SHUTDOWN_HOOK_PROPERTY = "net.sf.ehcache.enableShutdownHook";
    private static final Logger LOG = LoggerFactory.getLogger(CacheManager.class);
    private static final long EVERY_WEEK = 604800000L;
    private static final long DELAY_UPDATE_CHECK = 1000L;
    private static final int POOL_SHUTDOWN_TIMEOUT_SECS = 60;
    private static volatile CacheManager singleton;
    private static final MBeanRegistrationProviderFactory MBEAN_REGISTRATION_PROVIDER_FACTORY;
    private static final String NO_DEFAULT_CACHE_ERROR_MSG = "Caches cannot be added by name when default cache config is not specified in the config. Please add a default cache config in the configuration.";
    private static final Map<String, CacheManager> CACHE_MANAGERS_MAP;
    private static final IdentityHashMap<CacheManager, String> CACHE_MANAGERS_REVERSE_MAP;
    private static final Map<String, CacheManager> INITIALIZING_CACHE_MANAGERS_MAP;
    private static final long LOCAL_TX_RECOVERY_THREAD_JOIN_TIMEOUT = 1000L;
    static final String LOCAL_CACHE_NAME_PREFIX = "local_shadow_cache_for_";
    protected volatile Status status;
    protected final CacheManagerEventListenerRegistry cacheManagerEventListenerRegistry = new CacheManagerEventListenerRegistry();
    protected Thread shutdownHook;
    private final ConcurrentMap<String, Ehcache> ehcaches = new ConcurrentHashMap<String, Ehcache>();
    private final Map<String, Ehcache> initializingCaches = new ConcurrentHashMap<String, Ehcache>();
    private Ehcache defaultCache;
    private DiskStorePathManager diskStorePathManager;
    private volatile FeaturesManager featuresManager;
    private MBeanRegistrationProvider mbeanRegistrationProvider;
    private FailSafeTimer cacheManagerTimer;
    private volatile TerracottaClient terracottaClient;
    private volatile TransactionManagerLookup transactionManagerLookup;
    private volatile TransactionController transactionController;
    private volatile Thread localTransactionsRecoveryThread;
    private final ConcurrentMap<String, SoftLockManager> softLockManagers = new ConcurrentHashMap<String, SoftLockManager>();
    private volatile Pool onHeapPool;
    private volatile Pool onDiskPool;
    private volatile Configuration.RuntimeCfg runtimeCfg;
    private volatile DelegatingTransactionIDFactory transactionIDFactory;
    private volatile ManagementEventSink managementEventSink;
    private String registeredMgmtSvrBind;
    private ScheduledExecutorService statisticsExecutor;

    public CacheManager(Configuration configuration) throws CacheException {
        this.status = Status.STATUS_UNINITIALISED;
        this.init(configuration, null, null, null);
    }

    public CacheManager(String configurationFileName) throws CacheException {
        this.status = Status.STATUS_UNINITIALISED;
        this.init(null, configurationFileName, null, null);
    }

    public CacheManager(URL configurationURL) throws CacheException {
        this.status = Status.STATUS_UNINITIALISED;
        this.init(null, null, configurationURL, null);
    }

    public CacheManager(InputStream configurationInputStream) throws CacheException {
        this.status = Status.STATUS_UNINITIALISED;
        this.init(null, null, null, configurationInputStream);
    }

    public CacheManager() throws CacheException {
        this.status = Status.STATUS_UNINITIALISED;
        this.init(null, null, null, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized void init(Configuration initialConfiguration, String configurationFileName, URL configurationURL, InputStream configurationInputStream) {
        Configuration configuration = initialConfiguration == null ? this.parseConfiguration(configurationFileName, configurationURL, configurationInputStream) : initialConfiguration;
        this.assertManagementRESTServiceConfigurationIsCorrect(configuration);
        this.assertNoCacheManagerExistsWithSameName(configuration);
        try {
            this.doInit(configuration);
        }
        catch (Throwable t) {
            if (this.terracottaClient != null) {
                this.terracottaClient.shutdown();
            }
            if (this.statisticsExecutor != null) {
                this.statisticsExecutor.shutdown();
            }
            if (this.featuresManager != null) {
                this.featuresManager.dispose();
            }
            if (this.diskStorePathManager != null) {
                this.diskStorePathManager.releaseLock();
            }
            if (this.cacheManagerTimer != null) {
                this.cacheManagerTimer.cancel();
                this.cacheManagerTimer.purge();
            }
            Class<CacheManager> clazz = CacheManager.class;
            synchronized (CacheManager.class) {
                String name = CACHE_MANAGERS_REVERSE_MAP.remove(this);
                CACHE_MANAGERS_MAP.remove(name);
                // ** MonitorExit[var7_7] (shouldn't be in output)
                ALL_CACHE_MANAGERS.remove(this);
                if (t instanceof CacheException) {
                    throw (CacheException)t;
                }
                throw new CacheException(t);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doInit(Configuration configuration) {
        BalancedAccessEvictor evictor;
        if (configuration.getTerracottaConfiguration() != null) {
            configuration.getTerracottaConfiguration().freezeConfig();
        }
        this.runtimeCfg = configuration.setupFor(this, DEFAULT_NAME);
        this.statisticsExecutor = Executors.newScheduledThreadPool(Integer.getInteger("net.sf.ehcache.CacheManager.statisticsExecutor.poolSize", 1), new ThreadFactory(){
            private AtomicInteger cnt = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "Statistics Thread-" + CacheManager.this.getName() + "-" + this.cnt.incrementAndGet());
                t.setDaemon(true);
                return t;
            }
        });
        if (configuration.isMaxBytesLocalHeapSet()) {
            evictor = new BalancedAccessEvictor();
            SizeOfEngine sizeOfEngine = this.createSizeOfEngine(null);
            this.onHeapPool = new BoundedPool(configuration.getMaxBytesLocalHeap(), evictor, sizeOfEngine);
        }
        if (configuration.isMaxBytesLocalDiskSet()) {
            evictor = new BalancedAccessEvictor();
            this.onDiskPool = new BoundedPool(configuration.getMaxBytesLocalDisk(), evictor, null);
        }
        boolean clustered = false;
        this.terracottaClient = new TerracottaClient(this, configuration.getTerracottaConfiguration());
        if (this.terracottaClient.createClusteredInstanceFactory()) {
            clustered = true;
            if (configuration.getCacheConfigurations().isEmpty()) {
                this.terracottaClient.getClusteredInstanceFactory().linkClusteredCacheManager(this.getName(), configuration);
            }
        }
        ConfigurationHelper configurationHelper = new ConfigurationHelper(this, configuration);
        this.configure(configurationHelper);
        this.transactionController = new TransactionController(this.getOrCreateTransactionIDFactory(), configuration.getDefaultTransactionTimeoutInSeconds());
        this.status = Status.STATUS_ALIVE;
        this.cacheManagerEventListenerRegistry.init();
        this.addShutdownHookIfRequired();
        this.cacheManagerTimer = new FailSafeTimer(this.getName());
        this.mbeanRegistrationProvider = MBEAN_REGISTRATION_PROVIDER_FACTORY.createMBeanRegistrationProvider(configuration);
        if (configuration.getTerracottaConfiguration() != null && configuration.getTerracottaConfiguration().isWanEnabledTSA()) {
            this.terracottaClient.waitForOrchestrator(this.getName());
        }
        INITIALIZING_CACHE_MANAGERS_MAP.put(this.runtimeCfg.getCacheManagerName(), this);
        try {
            this.addConfiguredCaches(configurationHelper);
        }
        finally {
            INITIALIZING_CACHE_MANAGERS_MAP.remove(this.runtimeCfg.getCacheManagerName());
        }
        try {
            this.mbeanRegistrationProvider.initialize(this, this.terracottaClient.getClusteredInstanceFactory());
        }
        catch (MBeanRegistrationProviderException e) {
            LOG.warn("Failed to initialize the MBeanRegistrationProvider - " + this.mbeanRegistrationProvider.getClass().getName(), (Throwable)e);
        }
        ManagementRESTServiceConfiguration managementRESTService = configuration.getManagementRESTService();
        if (managementRESTService == null && clustered && ManagementServerLoader.isManagementAvailable()) {
            managementRESTService = this.getDefaultClusteredManagementRESTServiceConfiguration(configuration);
        }
        if (managementRESTService != null && managementRESTService.isEnabled()) {
            this.initializeManagementService(managementRESTService);
        }
        if (this.featuresManager != null) {
            this.featuresManager.startup();
        }
        this.transactionManagerLookup.init();
        this.localTransactionsRecoveryThread = new Thread(){

            @Override
            public void run() {
                TransactionController ctrl = CacheManager.this.transactionController;
                if (ctrl != null) {
                    try {
                        ctrl.getRecoveryManager().recover();
                    }
                    catch (Exception e) {
                        LOG.warn("local transactions recovery thread failed", (Throwable)e);
                    }
                }
            }
        };
        this.localTransactionsRecoveryThread.setName("ehcache local transactions recovery");
        this.localTransactionsRecoveryThread.setDaemon(true);
        this.localTransactionsRecoveryThread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initializeManagementService(ManagementRESTServiceConfiguration managementRESTService) {
        Class<CacheManager> clazz = CacheManager.class;
        synchronized (CacheManager.class) {
            ClusteredInstanceFactory clusteredInstanceFactory = this.terracottaClient.getClusteredInstanceFactory();
            String clientUUID = clusteredInstanceFactory == null ? null : clusteredInstanceFactory.getUUID();
            ManagementServerLoader.register(this, clientUUID, managementRESTService);
            this.registeredMgmtSvrBind = managementRESTService.getBind();
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    private ManagementRESTServiceConfiguration getDefaultClusteredManagementRESTServiceConfiguration(Configuration configuration) {
        ManagementRESTServiceConfiguration managementRESTService = new ManagementRESTServiceConfiguration();
        String url = configuration.getTerracottaConfiguration().getUrl();
        if (url != null && url.contains("@")) {
            managementRESTService.setSslEnabled(true);
        }
        managementRESTService.setEnabled(true);
        managementRESTService.setBind("");
        managementRESTService.setSecurityServiceLocation("");
        return managementRESTService;
    }

    private void assertManagementRESTServiceConfigurationIsCorrect(Configuration configuration) {
        boolean connectingToSecureCluster;
        ManagementRESTServiceConfiguration managementRESTService = configuration.getManagementRESTService();
        if (managementRESTService == null || !managementRESTService.isEnabled()) {
            return;
        }
        String url = configuration.getTerracottaConfiguration() != null ? configuration.getTerracottaConfiguration().getUrl() : null;
        boolean bl = connectingToSecureCluster = url != null && url.contains("@");
        if (connectingToSecureCluster && !managementRESTService.isSslEnabled()) {
            throw new InvalidConfigurationException("The REST agent cannot be bound to a port when SSL is disabled and connecting to a secure cluster. Change your configuration to <ManagementRESTServiceConfiguration sslEnabled=\"true\" .../> or remove the ManagementRESTServiceConfiguration element.");
        }
        if (connectingToSecureCluster && managementRESTService.getSecurityServiceLocation() == null) {
            managementRESTService.setSecurityServiceLocation("");
            LOG.warn("The REST agent must have a non-null Security Service Location when SSL is enabled. Using ManagementRESTServiceConfiguration.AUTO_LOCATION as a connection to a secure cluster is configured.");
        }
        if (managementRESTService.isSslEnabled() && managementRESTService.getSecurityServiceLocation() == null) {
            throw new InvalidConfigurationException("The REST agent must have a non-null Security Service Location when SSL is enabled. Change your configuration to <ManagementRESTServiceConfiguration securityServiceLocation=\"...\" .../>.");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void assertNoCacheManagerExistsWithSameName(Configuration configuration) {
        Class<CacheManager> clazz = CacheManager.class;
        synchronized (CacheManager.class) {
            boolean isNamed;
            String name;
            if (configuration.getName() != null) {
                name = configuration.getName();
                isNamed = true;
            } else {
                name = DEFAULT_NAME;
                isNamed = false;
            }
            CacheManager cacheManager = CACHE_MANAGERS_MAP.get(name);
            if (cacheManager != null) {
                ConfigurationSource configurationSource = cacheManager.getConfiguration().getConfigurationSource();
                String msg = "Another " + (String)(isNamed ? "CacheManager with same name '" + name + "'" : "unnamed CacheManager") + " already exists in the same VM. Please provide unique names for each CacheManager in the config or do one of following:\n1. Use one of the CacheManager.create() static factory methods to reuse same CacheManager with same name or create one if necessary\n2. Shutdown the earlier cacheManager before creating new one with same name.\nThe source of the existing CacheManager is: " + (configurationSource == null ? "[Programmatically configured]" : configurationSource);
                throw new CacheException(msg);
            }
            CACHE_MANAGERS_MAP.put(name, this);
            CACHE_MANAGERS_REVERSE_MAP.put(this, name);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    ScheduledExecutorService getStatisticsExecutor() {
        return this.statisticsExecutor;
    }

    public Pool getOnHeapPool() {
        return this.onHeapPool;
    }

    public Pool getOnDiskPool() {
        return this.onDiskPool;
    }

    public String getClusterUUID() {
        if (this.terracottaClient.getClusteredInstanceFactory() != null) {
            return CacheManager.getClientUUID(this.terracottaClient.getClusteredInstanceFactory());
        }
        return "";
    }

    private static String getClientUUID(ClusteredInstanceFactory clusteredInstanceFactory) {
        return clusteredInstanceFactory.getUUID();
    }

    public Store createTerracottaStore(Ehcache cache) {
        return this.getClusteredInstanceFactory().createStore(cache);
    }

    public WriteBehind createTerracottaWriteBehind(Ehcache cache) {
        return this.getClusteredInstanceFactory().createWriteBehind(cache);
    }

    public CacheEventListener createTerracottaEventReplicator(Ehcache cache) {
        return this.getClusteredInstanceFactory().createEventReplicator(cache);
    }

    protected ClusteredInstanceFactory getClusteredInstanceFactory() {
        return this.terracottaClient.getClusteredInstanceFactory();
    }

    private synchronized Configuration parseConfiguration(String configurationFileName, URL configurationURL, InputStream configurationInputStream) throws CacheException {
        Configuration parsedConfig;
        this.reinitialisationCheck();
        if (configurationFileName != null) {
            LOG.debug("Configuring CacheManager from {}", (Object)configurationFileName);
            parsedConfig = ConfigurationFactory.parseConfiguration(new File(configurationFileName));
        } else if (configurationURL != null) {
            parsedConfig = ConfigurationFactory.parseConfiguration(configurationURL);
        } else if (configurationInputStream != null) {
            parsedConfig = ConfigurationFactory.parseConfiguration(configurationInputStream);
        } else {
            LOG.debug("Configuring ehcache from classpath.");
            parsedConfig = ConfigurationFactory.parseConfiguration();
        }
        return parsedConfig;
    }

    private void configure(ConfigurationHelper configurationHelper) {
        String diskStorePath = configurationHelper.getDiskStorePath();
        if (diskStorePath == null) {
            this.diskStorePathManager = new DiskStorePathManager();
            if (configurationHelper.numberOfCachesThatUseDiskStorage() > 0) {
                LOG.warn("One or more caches require a DiskStore but there is no diskStore element configured. Using the default disk store path of " + DiskStoreConfiguration.getDefaultPath() + ". Please explicitly configure the diskStore element in ehcache.xml.");
            }
        } else {
            this.diskStorePathManager = new DiskStorePathManager(diskStorePath);
        }
        this.featuresManager = this.retrieveFeaturesManager();
        this.transactionManagerLookup = this.runtimeCfg.getTransactionManagerLookup();
        this.cacheManagerEventListenerRegistry.registerListener(configurationHelper.createCacheManagerEventListener(this));
        ALL_CACHE_MANAGERS.add(this);
        this.defaultCache = configurationHelper.createDefaultCache();
    }

    private void addConfiguredCaches(ConfigurationHelper configurationHelper) {
        Set unitialisedCaches = configurationHelper.createCaches();
        for (Ehcache unitialisedCache : unitialisedCaches) {
            this.addCacheNoCheck(unitialisedCache, true);
            List<Ehcache> cacheDecorators = configurationHelper.createCacheDecorators(unitialisedCache);
            for (Ehcache decoratedCache : cacheDecorators) {
                this.addOrReplaceDecoratedCache(unitialisedCache, decoratedCache);
            }
        }
    }

    private void addOrReplaceDecoratedCache(Ehcache underlyingCache, Ehcache decoratedCache) {
        if (decoratedCache.getName().equals(underlyingCache.getName())) {
            this.replaceCacheWithDecoratedCache(underlyingCache, decoratedCache);
        } else {
            this.addDecoratedCache(decoratedCache);
        }
    }

    private void reinitialisationCheck() throws IllegalStateException {
        if (this.diskStorePathManager != null || this.ehcaches.size() != 0 || this.status.equals(Status.STATUS_SHUTDOWN)) {
            throw new IllegalStateException("Attempt to reinitialise the CacheManager");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CacheManager create() throws CacheException {
        if (singleton != null) {
            LOG.debug("Attempting to create an existing singleton. Existing singleton returned.");
            return singleton;
        }
        Class<CacheManager> clazz = CacheManager.class;
        synchronized (CacheManager.class) {
            if (singleton == null) {
                singleton = CacheManager.newInstance();
            } else {
                LOG.debug("Attempting to create an existing singleton. Existing singleton returned.");
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return singleton;
        }
    }

    public static CacheManager newInstance() throws CacheException {
        return CacheManager.newInstance(ConfigurationFactory.parseConfiguration(), "Creating new CacheManager with default config");
    }

    public static CacheManager getInstance() throws CacheException {
        return CacheManager.create();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CacheManager create(String configurationFileName) throws CacheException {
        if (singleton != null) {
            LOG.debug("Attempting to create an existing singleton. Existing singleton returned.");
            return singleton;
        }
        Class<CacheManager> clazz = CacheManager.class;
        synchronized (CacheManager.class) {
            if (singleton == null) {
                singleton = CacheManager.newInstance(configurationFileName);
            } else {
                LOG.debug("Attempting to create an existing singleton. Existing singleton returned.");
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return singleton;
        }
    }

    public static CacheManager newInstance(String configurationFileName) throws CacheException {
        return CacheManager.newInstance(ConfigurationFactory.parseConfiguration(new File(configurationFileName)), "Creating new CacheManager with config file: " + configurationFileName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CacheManager create(URL configurationFileURL) throws CacheException {
        if (singleton != null) {
            LOG.debug("Attempting to create an existing singleton. Existing singleton returned.");
            return singleton;
        }
        Class<CacheManager> clazz = CacheManager.class;
        synchronized (CacheManager.class) {
            if (singleton == null) {
                singleton = CacheManager.newInstance(configurationFileURL);
            } else {
                LOG.debug("Attempting to create an existing singleton. Existing singleton returned.");
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return singleton;
        }
    }

    public static CacheManager newInstance(URL configurationFileURL) throws CacheException {
        return CacheManager.newInstance(ConfigurationFactory.parseConfiguration(configurationFileURL), "Creating new CacheManager with config URL: " + configurationFileURL);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CacheManager create(InputStream inputStream) throws CacheException {
        if (singleton != null) {
            LOG.debug("Attempting to create an existing singleton. Existing singleton returned.");
            return singleton;
        }
        Class<CacheManager> clazz = CacheManager.class;
        synchronized (CacheManager.class) {
            if (singleton == null) {
                singleton = CacheManager.newInstance(inputStream);
            } else {
                LOG.debug("Attempting to create an existing singleton. Existing singleton returned.");
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return singleton;
        }
    }

    public static CacheManager newInstance(InputStream inputStream) throws CacheException {
        return CacheManager.newInstance(ConfigurationFactory.parseConfiguration(inputStream), "Creating new CacheManager with InputStream");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CacheManager create(Configuration config) throws CacheException {
        if (singleton != null) {
            LOG.debug("Attempting to create an existing singleton. Existing singleton returned.");
            return singleton;
        }
        Class<CacheManager> clazz = CacheManager.class;
        synchronized (CacheManager.class) {
            if (singleton == null) {
                singleton = CacheManager.newInstance(config);
            } else {
                LOG.debug("Attempting to create an existing singleton. Existing singleton returned.");
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return singleton;
        }
    }

    public static CacheManager newInstance(Configuration config) {
        return CacheManager.newInstance(config, "Creating new CacheManager with Configuration Object");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static CacheManager newInstance(Configuration configuration, String msg) throws CacheException {
        Class<CacheManager> clazz = CacheManager.class;
        synchronized (CacheManager.class) {
            CacheManager cacheManager;
            String name = configuration.getName();
            if (name == null) {
                name = DEFAULT_NAME;
            }
            if ((cacheManager = CACHE_MANAGERS_MAP.get(name)) == null) {
                LOG.debug(msg);
                cacheManager = new CacheManager(configuration);
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return cacheManager;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CacheManager getCacheManager(String name) {
        Class<CacheManager> clazz = CacheManager.class;
        synchronized (CacheManager.class) {
            if (name == null) {
                name = DEFAULT_NAME;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return CACHE_MANAGERS_MAP.get(name);
        }
    }

    public Cache getCache(String name) throws IllegalStateException, ClassCastException {
        this.checkStatus();
        Ehcache ehcache = (Ehcache)this.ehcaches.get(name);
        return ehcache instanceof Cache ? (Cache)ehcache : null;
    }

    public Ehcache getEhcache(String name) throws IllegalStateException {
        this.checkStatus();
        return (Ehcache)this.ehcaches.get(name);
    }

    private void addShutdownHookIfRequired() {
        String shutdownHookProperty = System.getProperty(ENABLE_SHUTDOWN_HOOK_PROPERTY);
        boolean enabled = PropertyUtil.parseBoolean(shutdownHookProperty);
        if (!enabled) {
            return;
        }
        LOG.info("The CacheManager shutdown hook is enabled because {} is set to true.", (Object)ENABLE_SHUTDOWN_HOOK_PROPERTY);
        Thread localShutdownHook = new Thread(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                3 var1_1 = this;
                synchronized (var1_1) {
                    if (CacheManager.this.status.equals(Status.STATUS_ALIVE)) {
                        CacheManager.this.shutdownHook = null;
                        LOG.info("VM shutting down with the CacheManager still active. Calling shutdown.");
                        CacheManager.this.shutdown();
                    }
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(localShutdownHook);
        this.shutdownHook = localShutdownHook;
    }

    private void removeShutdownHook() {
        if (this.shutdownHook != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
            }
            catch (IllegalStateException e) {
                LOG.debug("IllegalStateException due to attempt to remove a shutdownhook while the VM is actually shutting down.", (Throwable)e);
            }
            this.shutdownHook = null;
        }
    }

    public synchronized void addCache(String cacheName) throws IllegalStateException, ObjectExistsException, CacheException {
        this.checkStatus();
        if (cacheName == null || cacheName.length() == 0) {
            return;
        }
        if (this.ehcaches.get(cacheName) != null) {
            throw new ObjectExistsException("Cache " + cacheName + " already exists");
        }
        Ehcache clonedDefaultCache = this.cloneDefaultCache(cacheName);
        if (clonedDefaultCache == null) {
            throw new CacheException(NO_DEFAULT_CACHE_ERROR_MSG);
        }
        this.addCache(clonedDefaultCache);
        for (Ehcache ehcache : this.createDefaultCacheDecorators(clonedDefaultCache)) {
            this.addOrReplaceDecoratedCache(clonedDefaultCache, ehcache);
        }
    }

    public void addCache(Cache cache) throws IllegalStateException, ObjectExistsException, CacheException {
        this.checkStatus();
        if (cache == null) {
            return;
        }
        this.addCache((Ehcache)cache);
    }

    public synchronized void addCache(Ehcache cache) throws IllegalStateException, ObjectExistsException, CacheException {
        boolean verifyOffHeapUsage;
        this.checkStatus();
        if (cache == null) {
            return;
        }
        CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();
        boolean bl = verifyOffHeapUsage = this.runtimeCfg.hasOffHeapPool() && (!cacheConfiguration.isOverflowToDisk() && !cacheConfiguration.isOverflowToOffHeapSet() || cacheConfiguration.isOverflowToOffHeap());
        if (verifyOffHeapUsage && (cacheConfiguration.isMaxBytesLocalOffHeapPercentageSet() || cacheConfiguration.getMaxBytesLocalOffHeap() > 0L)) {
            throw new CacheException("CacheManager uses OffHeap settings, you can't add cache using offHeap dynamically!");
        }
        this.addCacheNoCheck(cache, true);
    }

    public synchronized void addDecoratedCache(Ehcache decoratedCache) throws ObjectExistsException {
        this.internalAddDecoratedCache(decoratedCache, true);
    }

    public synchronized void addDecoratedCacheIfAbsent(Ehcache decoratedCache) throws ObjectExistsException {
        this.internalAddDecoratedCache(decoratedCache, false);
    }

    private void internalAddDecoratedCache(Ehcache decoratedCache, boolean strict) {
        Ehcache old = this.ehcaches.putIfAbsent(decoratedCache.getName(), decoratedCache);
        if (strict && old != null) {
            throw new ObjectExistsException("Cache " + decoratedCache.getName() + " already exists in the CacheManager");
        }
    }

    void initializeEhcache(Ehcache cache, boolean registerCacheConfig) {
        if (!registerCacheConfig) {
            cache.getCacheConfiguration().setupFor(this, registerCacheConfig, this.getParentCacheName(cache));
        } else {
            cache.getCacheConfiguration().setupFor(this);
        }
        cache.setCacheManager(this);
        cache.setTransactionManagerLookup(this.transactionManagerLookup);
        cache.initialise();
        if (!this.runtimeCfg.allowsDynamicCacheConfig()) {
            cache.disableDynamicFeatures();
        }
        if (!registerCacheConfig) {
            this.associateShadowCache(cache);
        }
        try {
            cache.bootstrap();
        }
        catch (CacheException e) {
            LOG.warn("Cache " + cache.getName() + "requested bootstrap but a CacheException occured. " + e.getMessage(), (Throwable)e);
        }
    }

    private void associateShadowCache(Ehcache shadow) {
        String parentCacheName = this.getParentCacheName(shadow);
        if (parentCacheName == null) {
            return;
        }
        Ehcache parent = this.initializingCaches.get(parentCacheName);
        if (parent == null) {
            parent = (Ehcache)this.ehcaches.get(parentCacheName);
        }
        if (parent != null) {
            StatisticsManager.associate(shadow).withParent(parent);
        }
    }

    private String getParentCacheName(Ehcache shadow) {
        String shadowPrefix = LOCAL_CACHE_NAME_PREFIX + this.getName() + "___tc_clustered-ehcache|" + this.getName() + "|";
        if (shadow.getName().startsWith(shadowPrefix)) {
            return shadow.getName().substring(shadowPrefix.length());
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Ehcache addCacheNoCheck(Ehcache cache, boolean strict) throws IllegalStateException, ObjectExistsException, CacheException {
        if (cache.getStatus() != Status.STATUS_UNINITIALISED) {
            throw new CacheException("Trying to add an already initialized cache. If you are adding a decorated cache, use CacheManager.addDecoratedCache(Ehcache decoratedCache) instead.");
        }
        if (cache.getCacheConfiguration().isTerracottaClustered() && this.terracottaClient.getClusteredInstanceFactory() == null) {
            throw new CacheException(String.format("Trying to add terracotta cache %s but no <terracottaConfig> element was used to specify the Terracotta configuration on the CacheManager %s.", cache.getName(), this.getName()));
        }
        Ehcache ehcache = (Ehcache)this.ehcaches.get(cache.getName());
        if (ehcache != null) {
            if (strict) {
                throw new ObjectExistsException("Cache " + cache.getName() + " already exists");
            }
            return ehcache;
        }
        this.initializingCaches.put(cache.getName(), cache);
        try {
            this.initializeEhcache(cache, true);
            ehcache = this.ehcaches.putIfAbsent(cache.getName(), cache);
            if (ehcache != null) {
                throw new AssertionError();
            }
        }
        finally {
            this.initializingCaches.remove(cache.getName());
        }
        if (this.status.equals(Status.STATUS_ALIVE)) {
            this.cacheManagerEventListenerRegistry.notifyCacheAdded(cache.getName());
        }
        return cache;
    }

    public boolean cacheExists(String cacheName) throws IllegalStateException {
        this.checkStatus();
        return this.ehcaches.get(cacheName) != null;
    }

    public void removeAllCaches() {
        String[] cacheNames;
        for (String cacheName : cacheNames = this.getCacheNames()) {
            this.removeCache(cacheName);
        }
    }

    @Deprecated
    public void removalAll() {
        this.removeAllCaches();
    }

    public synchronized void removeCache(String cacheName) throws IllegalStateException {
        this.checkStatus();
        if (cacheName == null || cacheName.length() == 0) {
            return;
        }
        Ehcache cache = (Ehcache)this.ehcaches.remove(cacheName);
        if (cache != null && cache.getStatus().equals(Status.STATUS_ALIVE)) {
            cache.dispose();
            this.runtimeCfg.removeCache(cache.getCacheConfiguration());
            this.cacheManagerEventListenerRegistry.notifyCacheRemoved(cache.getName());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shutdown() {
        CacheManager cacheManager = this;
        synchronized (cacheManager) {
            if (this.localTransactionsRecoveryThread != null && this.localTransactionsRecoveryThread.isAlive()) {
                this.localTransactionsRecoveryThread.interrupt();
                try {
                    this.localTransactionsRecoveryThread.join(1000L);
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            this.localTransactionsRecoveryThread = null;
            if (this.status.equals(Status.STATUS_SHUTDOWN)) {
                LOG.debug("CacheManager already shutdown");
                return;
            }
            if (this.registeredMgmtSvrBind != null) {
                ManagementServerLoader.unregister(this.registeredMgmtSvrBind, this);
                this.registeredMgmtSvrBind = null;
            }
            for (Ehcache cache : this.ehcaches.values()) {
                if (cache == null) continue;
                cache.dispose();
            }
            this.ehcaches.clear();
            this.initializingCaches.clear();
            if (this.defaultCache != null) {
                this.defaultCache.dispose();
            }
            if (this.cacheManagerTimer != null) {
                this.cacheManagerTimer.cancel();
                this.cacheManagerTimer.purge();
            }
            this.cacheManagerEventListenerRegistry.dispose();
            ALL_CACHE_MANAGERS.remove(this);
            this.status = Status.STATUS_SHUTDOWN;
            XARequestProcessor.shutdown();
            if (this == singleton) {
                singleton = null;
            }
            this.terracottaClient.shutdown();
            this.terracottaClient = null;
            this.transactionController = null;
            this.removeShutdownHook();
            if (this.featuresManager != null) {
                this.featuresManager.dispose();
            }
            if (this.diskStorePathManager != null) {
                this.diskStorePathManager.releaseLock();
            }
            try {
                ConcurrencyUtil.shutdownAndWaitForTermination(this.statisticsExecutor, 60);
            }
            catch (TimeoutException e) {
                LOG.warn(e.getMessage(), (Throwable)e);
            }
            this.getConfiguration().cleanup();
            Class<CacheManager> clazz = CacheManager.class;
            synchronized (CacheManager.class) {
                String name = CACHE_MANAGERS_REVERSE_MAP.remove(this);
                CACHE_MANAGERS_MAP.remove(name);
                // ** MonitorExit[var2_3] (shouldn't be in output)
            }
        }
    }

    public String[] getCacheNames() throws IllegalStateException {
        this.checkStatus();
        return this.ehcaches.keySet().toArray(new String[0]);
    }

    protected void checkStatus() {
        if (!this.status.equals(Status.STATUS_ALIVE)) {
            if (this.status.equals(Status.STATUS_UNINITIALISED)) {
                throw new IllegalStateException("The CacheManager has not yet been initialised. It cannot be used yet.");
            }
            if (this.status.equals(Status.STATUS_SHUTDOWN)) {
                throw new IllegalStateException("The CacheManager has been shut down. It can no longer be used.");
            }
        }
    }

    public Status getStatus() {
        return this.status;
    }

    public void clearAll() throws CacheException {
        String[] cacheNames = this.getCacheNames();
        LOG.debug("Clearing all caches");
        for (String cacheName : cacheNames) {
            Ehcache cache = this.getEhcache(cacheName);
            cache.removeAll();
        }
    }

    public void clearAllStartingWith(String prefix) throws CacheException {
        if (prefix == null || prefix.length() == 0) {
            return;
        }
        for (Map.Entry o : this.ehcaches.entrySet()) {
            Map.Entry entry = o;
            String cacheName = (String)entry.getKey();
            if (!cacheName.startsWith(prefix)) continue;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Clearing cache named '" + cacheName + "' (matches '" + prefix + "' prefix");
            }
            ((Ehcache)entry.getValue()).removeAll();
        }
    }

    public CacheManagerEventListener getCacheManagerEventListener() {
        return this.cacheManagerEventListenerRegistry;
    }

    public void setCacheManagerEventListener(CacheManagerEventListener cacheManagerEventListener) {
        this.getCacheManagerEventListenerRegistry().registerListener(cacheManagerEventListener);
    }

    public CacheManagerEventListenerRegistry getCacheManagerEventListenerRegistry() {
        return this.cacheManagerEventListenerRegistry;
    }

    public synchronized void replaceCacheWithDecoratedCache(Ehcache ehcache, Ehcache decoratedCache) throws CacheException {
        if (!ehcache.equals(decoratedCache)) {
            throw new CacheException("Cannot replace " + decoratedCache.getName() + " It does not equal the incumbent cache.");
        }
        String cacheName = ehcache.getName();
        if (!this.ehcaches.replace(cacheName, ehcache, decoratedCache)) {
            if (this.cacheExists(cacheName)) {
                throw new CacheException("Cache '" + ehcache.getName() + "' managed with this CacheManager doesn't match!");
            }
            throw new CacheException("Cache '" + cacheName + "' isn't associated with this manager (anymore?)");
        }
    }

    public String getName() {
        if (this.runtimeCfg.getCacheManagerName() != null) {
            return this.runtimeCfg.getCacheManagerName();
        }
        return super.toString();
    }

    public boolean isNamed() {
        return this.runtimeCfg.isNamed();
    }

    public String toString() {
        return this.getName();
    }

    public DiskStorePathManager getDiskStorePathManager() {
        return this.diskStorePathManager;
    }

    public FailSafeTimer getTimer() {
        return this.cacheManagerTimer;
    }

    public CacheCluster getCluster(ClusterScheme scheme) throws ClusterSchemeNotAvailableException {
        switch (scheme) {
            case TERRACOTTA: {
                if (null == this.terracottaClient.getClusteredInstanceFactory()) {
                    throw new ClusterSchemeNotAvailableException(ClusterScheme.TERRACOTTA, "Terracotta cluster scheme is not available");
                }
                return this.terracottaClient.getCacheCluster();
            }
        }
        return NoopCacheCluster.INSTANCE;
    }

    public String getOriginalConfigurationText() {
        if (this.runtimeCfg.getConfiguration().getConfigurationSource() == null) {
            return "Originally configured programmatically. No original configuration source text.";
        }
        Configuration originalConfiguration = this.runtimeCfg.getConfiguration().getConfigurationSource().createConfiguration();
        return ConfigurationUtil.generateCacheManagerConfigurationText(originalConfiguration);
    }

    public String getActiveConfigurationText() {
        return ConfigurationUtil.generateCacheManagerConfigurationText(this);
    }

    public String getOriginalConfigurationText(String cacheName) throws CacheException {
        if (this.runtimeCfg.getConfiguration().getConfigurationSource() == null) {
            return "Originally configured programmatically. No original configuration source text.";
        }
        Configuration originalConfiguration = this.runtimeCfg.getConfiguration().getConfigurationSource().createConfiguration();
        CacheConfiguration cacheConfiguration = originalConfiguration.getCacheConfigurations().get(cacheName);
        if (cacheConfiguration == null) {
            throw new CacheException("Cache with name '" + cacheName + "' does not exist in the original configuration");
        }
        return ConfigurationUtil.generateCacheConfigurationText(this.runtimeCfg.getConfiguration(), cacheConfiguration);
    }

    public String getActiveConfigurationText(String cacheName) throws CacheException {
        CacheConfiguration actualConfig;
        boolean decoratedCache = false;
        Ehcache cache = this.getCache(cacheName);
        if (cache == null) {
            cache = this.getEhcache(cacheName);
            decoratedCache = true;
        }
        CacheConfiguration cacheConfiguration = actualConfig = cache != null ? cache.getCacheConfiguration() : null;
        if (actualConfig == null) {
            throw new CacheException("Cache with name '" + cacheName + "' does not exist");
        }
        CacheConfiguration config = decoratedCache ? actualConfig.clone().name(cacheName) : actualConfig;
        return ConfigurationUtil.generateCacheConfigurationText(this.runtimeCfg.getConfiguration(), config);
    }

    public Configuration getConfiguration() {
        return this.runtimeCfg.getConfiguration();
    }

    public synchronized Ehcache addCacheIfAbsent(Ehcache cache) {
        this.checkStatus();
        return cache == null ? null : this.addCacheNoCheck(cache, false);
    }

    public synchronized Ehcache addCacheIfAbsent(String cacheName) {
        this.checkStatus();
        if (cacheName == null || cacheName.length() == 0) {
            return null;
        }
        Ehcache ehcache = (Ehcache)this.ehcaches.get(cacheName);
        if (ehcache == null) {
            Ehcache clonedDefaultCache = this.cloneDefaultCache(cacheName);
            if (clonedDefaultCache == null) {
                throw new CacheException(NO_DEFAULT_CACHE_ERROR_MSG);
            }
            this.addCacheIfAbsent(clonedDefaultCache);
            for (Ehcache createdCache : this.createDefaultCacheDecorators(clonedDefaultCache)) {
                this.addOrReplaceDecoratedCache(clonedDefaultCache, createdCache);
            }
        }
        return (Ehcache)this.ehcaches.get(cacheName);
    }

    private Ehcache cloneDefaultCache(String cacheName) {
        Ehcache cache;
        if (this.defaultCache == null) {
            return null;
        }
        try {
            cache = (Ehcache)this.defaultCache.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new CacheException("Failure cloning default cache. Initial cause was " + e.getMessage(), e);
        }
        if (cache != null) {
            cache.setName(cacheName);
        }
        return cache;
    }

    private List<Ehcache> createDefaultCacheDecorators(Ehcache underlyingCache) {
        return ConfigurationHelper.createDefaultCacheDecorators(underlyingCache, this.runtimeCfg.getConfiguration().getDefaultCacheConfiguration(), this.getClassLoader());
    }

    private ClassLoader getClassLoader() {
        return this.runtimeCfg.getConfiguration().getClassLoader();
    }

    public TransactionController getTransactionController() {
        return this.transactionController;
    }

    public TransactionIDFactory getOrCreateTransactionIDFactory() {
        if (this.transactionIDFactory == null) {
            this.transactionIDFactory = new DelegatingTransactionIDFactory(this.featuresManager, this.terracottaClient, this.getName());
        }
        return this.transactionIDFactory;
    }

    SoftLockManager createSoftLockManager(Ehcache cache) {
        SoftLockManager softLockManager;
        if (cache.getCacheConfiguration().isTerracottaClustered()) {
            softLockManager = this.getClusteredInstanceFactory().getOrCreateSoftLockManager(cache);
        } else {
            ReadCommittedSoftLockFactory lockFactory = new ReadCommittedSoftLockFactory();
            softLockManager = (SoftLockManager)this.softLockManagers.get(cache.getName());
            if (softLockManager == null) {
                softLockManager = this.featuresManager == null ? new SoftLockManagerImpl(cache.getName(), lockFactory) : this.featuresManager.createSoftLockManager(cache, lockFactory);
                SoftLockManager old = this.softLockManagers.putIfAbsent(cache.getName(), softLockManager);
                if (old != null) {
                    softLockManager = old;
                }
            }
        }
        return softLockManager;
    }

    SizeOfEngine createSizeOfEngine(Cache cache) {
        String className;
        Object prop = "net.sf.ehcache.sizeofengine";
        prop = this.isNamed() ? (String)prop + "." + this.getName() : (String)prop + ".default";
        if (cache != null) {
            prop = (String)prop + "." + cache.getName();
        }
        if ((className = System.getProperty((String)prop)) != null) {
            try {
                Class<?> aClass = Class.forName(className);
                return (SizeOfEngine)aClass.newInstance();
            }
            catch (Exception exception) {
                throw new RuntimeException("Couldn't load and instantiate custom " + (String)(cache != null ? "SizeOfEngine for cache '" + cache.getName() + "'" : "default SizeOfEngine"), exception);
            }
        }
        SizeOfPolicyConfiguration sizeOfPolicyConfiguration = null;
        if (cache != null) {
            sizeOfPolicyConfiguration = cache.getCacheConfiguration().getSizeOfPolicyConfiguration();
        }
        if (sizeOfPolicyConfiguration == null) {
            sizeOfPolicyConfiguration = this.getConfiguration().getSizeOfPolicyConfiguration();
        }
        return SizeOfEngineLoader.newSizeOfEngine(sizeOfPolicyConfiguration.getMaxDepth(), sizeOfPolicyConfiguration.getMaxDepthExceededBehavior().isAbort(), false);
    }

    public FeaturesManager getFeaturesManager() {
        return this.featuresManager;
    }

    private FeaturesManager retrieveFeaturesManager() {
        try {
            Class<?> featuresManagerClass = Class.forName("net.sf.ehcache.EnterpriseFeaturesManager");
            try {
                return (FeaturesManager)featuresManagerClass.getConstructor(CacheManager.class).newInstance(this);
            }
            catch (NoSuchMethodException e) {
                throw new CacheException("Cannot find Enterprise features manager");
            }
            catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof CacheException) {
                    throw (CacheException)cause;
                }
                throw new CacheException("Cannot instantiate enterprise features manager", cause);
            }
            catch (IllegalAccessException e) {
                throw new CacheException("Cannot instantiate enterprise features manager", e);
            }
            catch (InstantiationException e) {
                throw new CacheException("Cannot instantiate enterprise features manager", e);
            }
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    static CacheManager getInitializingCacheManager(String name) {
        return INITIALIZING_CACHE_MANAGERS_MAP.get(name);
    }

    public void sendManagementEvent(Serializable event, String type) {
        this.getOrCreateEventSink().sendManagementEvent(event, type);
    }

    private ManagementEventSink getOrCreateEventSink() {
        if (this.managementEventSink == null) {
            this.managementEventSink = new DelegatingManagementEventSink(this.terracottaClient);
        }
        return this.managementEventSink;
    }

    static {
        MBEAN_REGISTRATION_PROVIDER_FACTORY = new MBeanRegistrationProviderFactoryImpl();
        CACHE_MANAGERS_MAP = new HashMap<String, CacheManager>();
        CACHE_MANAGERS_REVERSE_MAP = new IdentityHashMap();
        INITIALIZING_CACHE_MANAGERS_MAP = new ConcurrentHashMap<String, CacheManager>();
    }
}

