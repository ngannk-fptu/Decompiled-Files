/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.terracotta;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.config.TerracottaClientConfiguration;
import net.sf.ehcache.terracotta.ClusteredInstanceFactory;
import net.sf.ehcache.terracotta.ClusteredInstanceFactoryWrapper;
import net.sf.ehcache.terracotta.TerracottaCacheCluster;
import net.sf.ehcache.terracotta.TerracottaClusteredInstanceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerracottaClient {
    public static final String CUSTOM_SECRET_PROVIDER_SYSTEM_PROPERTY = "com.terracotta.express.SecretProvider";
    private static final Logger LOGGER = LoggerFactory.getLogger(TerracottaClient.class);
    private static final int REJOIN_SLEEP_MILLIS_ON_EXCEPTION = Integer.getInteger("net.sf.ehcache.rejoin.sleepMillisOnException", 5000);
    private static final String CUSTOM_SECRET_PROVIDER_WRAPPER_CLASSNAME = "net.sf.ehcache.terracotta.security.SingletonSecretProviderWrapper";
    private final TerracottaClientConfiguration terracottaClientConfiguration;
    private volatile ClusteredInstanceFactoryWrapper clusteredInstanceFactory;
    private final TerracottaCacheCluster cacheCluster = new TerracottaCacheCluster();
    private final CacheManager cacheManager;
    private ExecutorService l1TerminatorThreadPool;

    public TerracottaClient(CacheManager cacheManager, TerracottaClientConfiguration terracottaClientConfiguration) {
        this.cacheManager = cacheManager;
        this.terracottaClientConfiguration = terracottaClientConfiguration;
        if (terracottaClientConfiguration != null) {
            terracottaClientConfiguration.freezeConfig();
            String secretProviderClassname = System.getProperty(CUSTOM_SECRET_PROVIDER_SYSTEM_PROPERTY);
            String tcUrl = terracottaClientConfiguration.getUrl();
            if (tcUrl != null && tcUrl.contains("@") && secretProviderClassname != null) {
                try {
                    System.setProperty(CUSTOM_SECRET_PROVIDER_SYSTEM_PROPERTY, CUSTOM_SECRET_PROVIDER_WRAPPER_CLASSNAME);
                    Class<?> secretProviderWrapperClass = Class.forName(CUSTOM_SECRET_PROVIDER_WRAPPER_CLASSNAME);
                    secretProviderWrapperClass.getMethod("useAsDelegate", String.class).invoke(secretProviderWrapperClass, secretProviderClassname);
                }
                catch (Exception e) {
                    throw new CacheException("Unable to initialize net.sf.ehcache.terracotta.security.SingletonSecretProviderWrapper", e);
                }
            }
        }
    }

    private static void setTestMode(TerracottaClusteredInstanceHelper testHelper) {
        try {
            Method method = TerracottaClusteredInstanceHelper.class.getDeclaredMethod("setTestMode", TerracottaClusteredInstanceHelper.class);
            method.setAccessible(true);
            method.invoke(null, testHelper);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClusteredInstanceFactory getClusteredInstanceFactory() {
        return this.clusteredInstanceFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean createClusteredInstanceFactory() {
        boolean created;
        if (this.terracottaClientConfiguration == null) {
            return false;
        }
        if (this.clusteredInstanceFactory != null) {
            return false;
        }
        TerracottaClient terracottaClient = this;
        synchronized (terracottaClient) {
            if (this.clusteredInstanceFactory == null) {
                this.clusteredInstanceFactory = this.createNewClusteredInstanceFactory();
                created = true;
            } else {
                created = false;
            }
        }
        return created;
    }

    public TerracottaCacheCluster getCacheCluster() {
        if (this.clusteredInstanceFactory == null) {
            throw new CacheException("Cannot get CacheCluster as ClusteredInstanceFactory has not been initialized yet.");
        }
        return this.cacheCluster;
    }

    public synchronized void shutdown() {
        if (this.clusteredInstanceFactory != null) {
            this.shutdownClusteredInstanceFactoryWrapper(this.clusteredInstanceFactory);
        }
    }

    public void waitForOrchestrator(String cacheManagerName) {
        this.clusteredInstanceFactory.waitForOrchestrator(cacheManagerName);
    }

    private void shutdownClusteredInstanceFactoryWrapper(ClusteredInstanceFactoryWrapper clusteredInstanceFactory) {
        clusteredInstanceFactory.getActualFactory().getTopology().removeAllListeners();
        clusteredInstanceFactory.shutdown();
    }

    private synchronized ClusteredInstanceFactoryWrapper createNewClusteredInstanceFactory() {
        if (this.clusteredInstanceFactory != null) {
            this.info("Shutting down old ClusteredInstanceFactory...");
            this.shutdownClusteredInstanceFactoryWrapper(this.clusteredInstanceFactory);
        }
        this.info("Creating new ClusteredInstanceFactory");
        ClusteredInstanceFactory factory = TerracottaClusteredInstanceHelper.getInstance().newClusteredInstanceFactory(this.terracottaClientConfiguration, this.cacheManager.getName(), this.cacheManager.getConfiguration().getClassLoader());
        CacheCluster underlyingCacheCluster = factory.getTopology();
        this.cacheCluster.setUnderlyingCacheCluster(underlyingCacheCluster);
        return new ClusteredInstanceFactoryWrapper(this, factory);
    }

    private synchronized ExecutorService getL1TerminatorThreadPool() {
        if (this.l1TerminatorThreadPool == null) {
            this.l1TerminatorThreadPool = Executors.newCachedThreadPool(new ThreadFactory(){
                private final ThreadGroup threadGroup = new ThreadGroup("Rejoin Terminator Thread Group");

                @Override
                public Thread newThread(Runnable runnable) {
                    Thread t = new Thread(this.threadGroup, runnable, "L1 Terminator");
                    t.setDaemon(true);
                    return t;
                }
            });
        }
        return this.l1TerminatorThreadPool;
    }

    private void info(String msg) {
        this.info(msg, null);
    }

    private void info(String msg, Throwable t) {
        if (t == null) {
            LOGGER.info(this.getLogPrefix() + msg);
        } else {
            LOGGER.info(this.getLogPrefix() + msg, t);
        }
    }

    private String getLogPrefix() {
        return "Thread [" + Thread.currentThread().getName() + "] [cacheManager: " + this.getCacheManagerName() + "]: ";
    }

    private void debug(String msg) {
        LOGGER.debug(this.getLogPrefix() + msg);
    }

    private void warn(String msg) {
        LOGGER.warn(this.getLogPrefix() + msg);
    }

    private String getCacheManagerName() {
        if (this.cacheManager.isNamed()) {
            return "'" + this.cacheManager.getName() + "'";
        }
        return "no name";
    }
}

