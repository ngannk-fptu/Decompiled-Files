/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.terracotta;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.CacheStoreHelper;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.TerracottaStore;
import net.sf.ehcache.terracotta.RotatingSnapshotFile;
import net.sf.ehcache.util.WeakIdentityConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class KeySnapshotter
implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger((String)KeySnapshotter.class.getName());
    private static final int POOL_SIZE = Integer.getInteger("net.sf.ehcache.terracotta.KeySnapshotter.threadPoolSize", 10);
    private static final WeakIdentityConcurrentMap<CacheManager, ScheduledExecutorService> INSTANCES = new WeakIdentityConcurrentMap(new WeakIdentityConcurrentMap.CleanUpTask<ScheduledExecutorService>(){

        @Override
        public void cleanUp(ScheduledExecutorService executor) {
            executor.shutdownNow();
        }
    });
    private final String cacheName;
    private volatile TerracottaStore tcStore;
    private final RotatingSnapshotFile rotatingWriter;
    private final Thread thread;
    private volatile Runnable onSnapshot;
    private final ScheduledFuture<?> scheduledFuture;

    KeySnapshotter(Ehcache cache, long interval, boolean doKeySnapshotOnDedicatedThread, RotatingSnapshotFile rotatingWriter) throws IllegalArgumentException {
        Store store = new CacheStoreHelper((Cache)cache).getStore();
        if (!(store instanceof TerracottaStore)) {
            throw new IllegalArgumentException("Cache '" + cache.getName() + "' isn't backed by a " + TerracottaStore.class.getSimpleName() + " but uses a " + store.getClass().getName() + " instead");
        }
        if (interval <= 0L) {
            throw new IllegalArgumentException("Interval needs to be a positive & non-zero value");
        }
        if (rotatingWriter == null) {
            throw new NullPointerException();
        }
        this.cacheName = cache.getName();
        this.rotatingWriter = rotatingWriter;
        this.tcStore = (TerracottaStore)store;
        if (doKeySnapshotOnDedicatedThread) {
            this.scheduledFuture = null;
            this.thread = new SnapShottingThread(this, interval, "KeySnapshotter for cache " + this.cacheName);
            this.thread.start();
        } else {
            this.scheduledFuture = this.getScheduledExecutorService(cache.getCacheManager()).scheduleWithFixedDelay(this, interval, interval, TimeUnit.SECONDS);
            this.thread = null;
        }
    }

    private ScheduledExecutorService getScheduledExecutorService(CacheManager cacheManager) {
        ScheduledExecutorService previous;
        ScheduledExecutorService scheduledExecutorService = INSTANCES.get(cacheManager);
        if (scheduledExecutorService == null && (previous = INSTANCES.putIfAbsent(cacheManager, scheduledExecutorService = new ScheduledThreadPoolExecutor(POOL_SIZE))) != null) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = previous;
        }
        return scheduledExecutorService;
    }

    void dispose(boolean immediately) {
        if (this.thread != null) {
            this.rotatingWriter.setShutdownOnThreadInterrupted(immediately);
            this.thread.interrupt();
            try {
                this.thread.join();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            this.scheduledFuture.cancel(immediately);
        }
        this.tcStore = null;
    }

    @Override
    public void run() {
        try {
            INSTANCES.cleanUp();
            this.rotatingWriter.writeAll(this.tcStore.getLocalKeys());
            this.onSnapshot();
        }
        catch (Throwable e) {
            LOG.error("Couldn't snapshot local keySet for Cache {}", (Object)this.cacheName, (Object)e);
        }
    }

    private void onSnapshot() {
        if (this.onSnapshot != null) {
            try {
                this.onSnapshot.run();
            }
            catch (Exception e) {
                LOG.warn("Error occurred in onSnapshot callback", (Throwable)e);
            }
        }
    }

    static Collection<CacheManager> getKnownCacheManagers() {
        return INSTANCES.keySet();
    }

    void doSnapshot() throws IOException {
        this.rotatingWriter.snapshotNowOrWaitForCurrentToFinish(this.tcStore.getLocalKeys());
        this.onSnapshot();
    }

    void setOnSnapshot(Runnable onSnapshot) {
        this.onSnapshot = onSnapshot;
    }

    public String getCacheName() {
        return this.cacheName;
    }

    private static class SnapShottingThread
    extends Thread {
        private long lastRun;
        private final long interval;

        public SnapShottingThread(Runnable runnable, long interval, String threadName) {
            super(runnable, threadName);
            this.interval = interval;
            this.lastRun = System.currentTimeMillis();
            this.setDaemon(true);
        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                long now;
                long nextTime = this.lastRun + TimeUnit.SECONDS.toMillis(this.interval);
                if (nextTime <= (now = System.currentTimeMillis())) {
                    super.run();
                    this.lastRun = System.currentTimeMillis();
                    continue;
                }
                try {
                    SnapShottingThread.sleep(nextTime - now);
                }
                catch (InterruptedException e) {
                    this.interrupt();
                }
            }
        }
    }
}

