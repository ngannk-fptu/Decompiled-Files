/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.terracotta.toolkit.cluster.ClusterInfo
 */
package org.terracotta.modules.ehcache.store;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import net.sf.ehcache.config.NonstopConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.toolkit.cluster.ClusterInfo;

public class TerracottaStoreInitializationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerracottaStoreInitializationService.class);
    private final ExecutorService threadPool;
    private final ClusterInfo clusterInfo;

    public TerracottaStoreInitializationService(ClusterInfo clusterInfo) {
        this.clusterInfo = clusterInfo;
        this.threadPool = this.getThreadPool();
    }

    public void shutdown() {
        this.threadPool.shutdownNow();
    }

    public void initialize(Runnable runnable, NonstopConfiguration nonStopConfiguration) {
        Future<?> future = this.threadPool.submit(runnable);
        this.waitForInitialization(future, nonStopConfiguration.getTimeoutMillis());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void waitForInitialization(Future<?> future, long nonStopTimeOutInMillis) {
        boolean interrupted = false;
        boolean initializationCompleted = false;
        try {
            do {
                try {
                    future.get(nonStopTimeOutInMillis, TimeUnit.MILLISECONDS);
                    initializationCompleted = true;
                }
                catch (InterruptedException e) {
                    interrupted = true;
                }
                catch (ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                }
                catch (TimeoutException timeoutException) {
                    // empty catch block
                }
            } while (!initializationCompleted && this.areOperationsEnabled());
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        if (!initializationCompleted) {
            LOGGER.debug("Returning without completing TerracottaStore initialization. Operations Enabled = {}", (Object)this.areOperationsEnabled());
        }
    }

    private boolean areOperationsEnabled() {
        return this.clusterInfo.areOperationsEnabled();
    }

    private ExecutorService getThreadPool() {
        ThreadFactory daemonThreadFactory = new ThreadFactory(){
            private final AtomicInteger threadID = new AtomicInteger();

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, "TerracottaStoreInitializationThread_" + this.threadID.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        };
        return Executors.newCachedThreadPool(daemonThreadFactory);
    }
}

