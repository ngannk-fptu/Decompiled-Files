/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.terracotta.toolkit.Toolkit
 *  org.terracotta.toolkit.ToolkitFeatureTypeInternal
 *  org.terracotta.toolkit.internal.ToolkitInternal
 *  org.terracotta.toolkit.internal.feature.ManagementInternalFeature
 *  org.terracotta.toolkit.internal.feature.NonStopInternalFeature
 */
package org.terracotta.modules.ehcache.store;

import com.terracotta.entity.ehcache.EhcacheEntitiesNaming;
import java.util.concurrent.Callable;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.config.CacheWriterConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.TerracottaClientConfiguration;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.management.event.ManagementEventSink;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.TerracottaStore;
import net.sf.ehcache.terracotta.ClusteredInstanceFactory;
import net.sf.ehcache.transaction.SoftLockManager;
import net.sf.ehcache.transaction.TransactionIDFactory;
import net.sf.ehcache.util.ProductInfo;
import net.sf.ehcache.writer.writebehind.WriteBehind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.ToolkitInstanceFactoryImpl;
import org.terracotta.modules.ehcache.async.AsyncCoordinator;
import org.terracotta.modules.ehcache.async.AsyncCoordinatorFactory;
import org.terracotta.modules.ehcache.async.AsyncCoordinatorFactoryImpl;
import org.terracotta.modules.ehcache.event.ClusteredEventReplicatorFactory;
import org.terracotta.modules.ehcache.event.FireRejoinOperatorEventClusterListener;
import org.terracotta.modules.ehcache.event.TerracottaTopologyImpl;
import org.terracotta.modules.ehcache.management.ClusteredManagementEventSink;
import org.terracotta.modules.ehcache.store.ClusteredSafeStore;
import org.terracotta.modules.ehcache.store.ClusteredStore;
import org.terracotta.modules.ehcache.store.TerracottaStoreInitializationService;
import org.terracotta.modules.ehcache.store.nonstop.NonStopStoreWrapper;
import org.terracotta.modules.ehcache.transaction.ClusteredTransactionIDFactory;
import org.terracotta.modules.ehcache.transaction.SoftLockManagerProvider;
import org.terracotta.modules.ehcache.writebehind.AsyncWriteBehind;
import org.terracotta.modules.ehcache.writebehind.WriteBehindAsyncConfig;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.ToolkitFeatureTypeInternal;
import org.terracotta.toolkit.internal.ToolkitInternal;
import org.terracotta.toolkit.internal.feature.ManagementInternalFeature;
import org.terracotta.toolkit.internal.feature.NonStopInternalFeature;

public class TerracottaClusteredInstanceFactory
implements ClusteredInstanceFactory {
    public static final Logger LOGGER = LoggerFactory.getLogger(TerracottaClusteredInstanceFactory.class);
    public static final String DEFAULT_CACHE_MANAGER_NAME = "__DEFAULT__";
    protected final ToolkitInstanceFactory toolkitInstanceFactory;
    protected final CacheCluster topology;
    private final ClusteredEventReplicatorFactory clusteredEventReplicatorFactory;
    private final SoftLockManagerProvider softLockManagerProvider;
    private final AsyncCoordinatorFactory asyncCoordinatorFactory;
    private final TerracottaStoreInitializationService initializationService;

    public TerracottaClusteredInstanceFactory(TerracottaClientConfiguration terracottaClientConfiguration, String cacheManagerName, ClassLoader loader) {
        this.toolkitInstanceFactory = this.createToolkitInstanceFactory(terracottaClientConfiguration, cacheManagerName, loader);
        this.initializationService = new TerracottaStoreInitializationService(this.toolkitInstanceFactory.getToolkit().getClusterInfo());
        this.topology = TerracottaClusteredInstanceFactory.createTopology(this.toolkitInstanceFactory);
        this.clusteredEventReplicatorFactory = new ClusteredEventReplicatorFactory(this.toolkitInstanceFactory);
        this.softLockManagerProvider = new SoftLockManagerProvider(this.toolkitInstanceFactory);
        this.asyncCoordinatorFactory = this.createAsyncCoordinatorFactory();
        this.logEhcacheBuildInfo();
    }

    private static CacheCluster createTopology(ToolkitInstanceFactory factory) {
        TerracottaTopologyImpl cacheCluster = new TerracottaTopologyImpl(factory.getToolkit().getClusterInfo());
        try {
            cacheCluster.addTopologyListener(new FireRejoinOperatorEventClusterListener(factory));
        }
        catch (Exception e) {
            LOGGER.warn("Unable to register: " + FireRejoinOperatorEventClusterListener.class.getName(), (Throwable)e);
        }
        return cacheCluster;
    }

    private void logEhcacheBuildInfo() {
        ProductInfo ehcacheCoreProductInfo = new ProductInfo();
        LOGGER.info(ehcacheCoreProductInfo.toString());
    }

    protected ToolkitInstanceFactory createToolkitInstanceFactory(TerracottaClientConfiguration terracottaClientConfiguration, String cacheManagerName, ClassLoader loader) {
        return new ToolkitInstanceFactoryImpl(terracottaClientConfiguration, cacheManagerName, loader);
    }

    protected AsyncCoordinatorFactory createAsyncCoordinatorFactory() {
        return new AsyncCoordinatorFactoryImpl(this.toolkitInstanceFactory);
    }

    @Override
    public final Store createStore(Ehcache cache) {
        return new ClusteredSafeStore(this.newStore(cache));
    }

    protected ClusteredStore newStore(Ehcache cache) {
        return new ClusteredStore(this.toolkitInstanceFactory, cache, this.topology);
    }

    @Override
    public final TerracottaStore createNonStopStore(Callable<TerracottaStore> store, Ehcache cache) {
        return new NonStopStoreWrapper(store, this.toolkitInstanceFactory, cache, this.initializationService);
    }

    @Override
    public CacheCluster getTopology() {
        return this.topology;
    }

    @Override
    public WriteBehind createWriteBehind(Ehcache cache) {
        CacheWriterConfiguration config = cache.getCacheConfiguration().getCacheWriterConfiguration();
        WriteBehindAsyncConfig asyncConfig = new WriteBehindAsyncConfig(config.getMinWriteDelay() * 1000, config.getMaxWriteDelay() * 1000, config.getWriteBatching(), config.getWriteBatchSize(), cache.getCacheConfiguration().getTerracottaConfiguration().isSynchronousWrites(), config.getRetryAttempts(), config.getRetryAttemptDelaySeconds() * 1000, config.getRateLimitPerSecond(), config.getWriteBehindMaxQueueSize());
        AsyncCoordinator asyncCoordinator = this.asyncCoordinatorFactory.getOrCreateAsyncCoordinator(cache, asyncConfig);
        return new AsyncWriteBehind(asyncCoordinator, config.getWriteBehindConcurrency());
    }

    @Override
    public synchronized CacheEventListener createEventReplicator(Ehcache cache) {
        return this.clusteredEventReplicatorFactory.getOrCreateClusteredEventReplicator(cache);
    }

    @Override
    public String getUUID() {
        return ((ToolkitInternal)this.toolkitInstanceFactory.getToolkit()).getClientUUID();
    }

    @Override
    public void enableNonStopForCurrentThread(boolean enable) {
        NonStopInternalFeature nonStopInternalFeature = (NonStopInternalFeature)((ToolkitInternal)this.toolkitInstanceFactory.getToolkit()).getFeature(ToolkitFeatureTypeInternal.NONSTOP);
        if (nonStopInternalFeature != null) {
            nonStopInternalFeature.enableForCurrentThread(enable);
        }
    }

    @Override
    public void shutdown() {
        this.toolkitInstanceFactory.shutdown();
        this.initializationService.shutdown();
    }

    @Override
    public TransactionIDFactory createTransactionIDFactory(String uuid, String cacheManagerName) {
        return new ClusteredTransactionIDFactory(uuid, cacheManagerName, this.toolkitInstanceFactory, this.topology);
    }

    @Override
    public SoftLockManager getOrCreateSoftLockManager(Ehcache cache) {
        return this.softLockManagerProvider.getOrCreateClusteredSoftLockFactory(cache);
    }

    @Override
    public boolean destroyCache(String cacheManagerName, String cacheName) {
        boolean destroyed = this.toolkitInstanceFactory.destroy(cacheManagerName, cacheName);
        return destroyed |= this.asyncCoordinatorFactory.destroy(cacheManagerName, cacheName);
    }

    @Override
    public void linkClusteredCacheManager(String cacheManagerName, Configuration configuration) {
        this.toolkitInstanceFactory.linkClusteredCacheManager(cacheManagerName, configuration);
    }

    @Override
    public void unlinkCache(String cacheName) {
        try {
            this.toolkitInstanceFactory.unlinkCache(cacheName);
        }
        catch (RuntimeException e) {
            if (e.getClass().getSimpleName().equals("TCNotRunningException")) {
                LOGGER.info("Terracotta client already shutdown", (Throwable)e);
            }
            throw e;
        }
    }

    @Override
    public ManagementEventSink createEventSink() {
        Toolkit toolkit = this.toolkitInstanceFactory.getToolkit();
        ToolkitInternal toolkitInternal = (ToolkitInternal)toolkit;
        ManagementInternalFeature feature = (ManagementInternalFeature)toolkitInternal.getFeature(ToolkitFeatureTypeInternal.MANAGEMENT);
        return new ClusteredManagementEventSink(feature);
    }

    public static String getToolkitMapNameForCache(String cacheManagerName, String cacheName) {
        return EhcacheEntitiesNaming.getToolkitCacheNameFor(cacheManagerName, cacheName);
    }

    @Override
    public void waitForOrchestrator(String cacheManagerName) {
        this.toolkitInstanceFactory.waitForOrchestrator(cacheManagerName);
    }
}

