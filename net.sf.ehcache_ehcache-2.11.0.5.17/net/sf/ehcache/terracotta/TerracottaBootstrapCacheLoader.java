/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.terracotta;

import java.io.IOException;
import java.util.Set;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.DiskStorePathManager;
import net.sf.ehcache.Disposable;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.distribution.RemoteCacheException;
import net.sf.ehcache.store.MemoryLimitedCacheLoader;
import net.sf.ehcache.terracotta.KeySnapshotter;
import net.sf.ehcache.terracotta.RotatingSnapshotFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerracottaBootstrapCacheLoader
extends MemoryLimitedCacheLoader
implements Disposable {
    public static final long DEFAULT_INTERVAL = 600L;
    public static final boolean DEFAULT_DEDICATED_THREAD = false;
    private static final Logger LOG = LoggerFactory.getLogger(TerracottaBootstrapCacheLoader.class);
    private final boolean aSynchronous;
    private final boolean doKeySnapshot;
    private final boolean doKeySnapshotOnDedicatedThread;
    private final long interval;
    private final DiskStorePathManager diskStorePathManager;
    private volatile KeySnapshotter keySnapshotter;
    private volatile boolean immediateShutdown;
    private volatile boolean doKeySnapshotOnDispose;

    private TerracottaBootstrapCacheLoader(boolean doKeySnapshot, boolean aSynchronous, String directory, long interval, boolean doKeySnapshotOnDedicatedThread) {
        this.aSynchronous = aSynchronous;
        this.doKeySnapshot = doKeySnapshot;
        this.doKeySnapshotOnDedicatedThread = doKeySnapshotOnDedicatedThread;
        this.interval = interval;
        this.diskStorePathManager = directory != null ? new DiskStorePathManager(directory) : null;
    }

    public TerracottaBootstrapCacheLoader(boolean asynchronous, String directory, boolean doKeySnapshots) {
        this(doKeySnapshots, asynchronous, directory, 600L, false);
    }

    public TerracottaBootstrapCacheLoader(boolean asynchronous, String directory, long interval) {
        this(asynchronous, directory, interval, false);
    }

    public TerracottaBootstrapCacheLoader(boolean asynchronous, String directory, long interval, boolean onDedicatedThread) {
        this(true, asynchronous, directory, interval, onDedicatedThread);
    }

    public boolean isImmediateShutdown() {
        return this.immediateShutdown;
    }

    public void setImmediateShutdown(boolean immediateShutdown) {
        this.immediateShutdown = immediateShutdown;
    }

    @Override
    public void load(Ehcache cache) throws CacheException {
        if (!cache.getCacheConfiguration().isTerracottaClustered()) {
            LOG.error("You're trying to bootstrap a non Terracotta clustered cache with a TerracottaBootstrapCacheLoader! Cache '{}' will not be bootstrapped and no keySet snapshot will be recorded...", (Object)cache.getName());
            return;
        }
        if (cache.getStatus() != Status.STATUS_ALIVE) {
            throw new CacheException("Cache '" + cache.getName() + "' isn't alive yet: " + cache.getStatus());
        }
        if (this.isAsynchronous()) {
            BootstrapThread thread = new BootstrapThread(cache);
            thread.start();
        } else {
            this.doLoad(cache);
        }
    }

    private void doLoad(Ehcache cache) {
        CacheManager manager = cache.getCacheManager();
        if (manager == null) {
            throw new CacheException("Cache must belong to a cache manager to bootstrap");
        }
        DiskStorePathManager pathManager = this.diskStorePathManager != null ? this.diskStorePathManager : cache.getCacheManager().getDiskStorePathManager();
        RotatingSnapshotFile snapshotFile = new RotatingSnapshotFile(pathManager, cache.getName(), manager.getConfiguration().getClassLoader());
        try {
            Set keys = snapshotFile.readAll();
            int loaded = 0;
            for (Object key : keys) {
                if (this.isInMemoryLimitReached(cache, loaded)) break;
                cache.get(key);
                ++loaded;
            }
            LOG.info("Finished loading {} keys (of {} on disk) from previous snapshot for Cache '{}'", new Object[]{loaded, keys.size(), cache.getName()});
        }
        catch (IOException e) {
            LOG.error("Couldn't load keySet for Cache '{}'", (Object)cache.getName(), (Object)e);
        }
        if (this.doKeySnapshot) {
            this.keySnapshotter = new KeySnapshotter(cache, this.interval, this.doKeySnapshotOnDedicatedThread, snapshotFile);
        }
    }

    @Override
    public boolean isAsynchronous() {
        return this.aSynchronous;
    }

    @Override
    public void dispose() {
        if (this.keySnapshotter != null) {
            if (this.doKeySnapshotOnDispose) {
                try {
                    this.keySnapshotter.doSnapshot();
                }
                catch (IOException e) {
                    LOG.error("Error writing local key set for Cache '{}'", (Object)this.keySnapshotter.getCacheName(), (Object)e);
                }
            } else {
                this.keySnapshotter.dispose(this.immediateShutdown);
            }
        }
        if (this.diskStorePathManager != null) {
            this.diskStorePathManager.releaseLock();
        }
    }

    public void doLocalKeySnapshot() throws IOException {
        this.keySnapshotter.doSnapshot();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    KeySnapshotter getKeySnapshotter() {
        return this.keySnapshotter;
    }

    public void setSnapshotOnDispose(boolean doKeySnapshotOnDispose) {
        this.doKeySnapshotOnDispose = doKeySnapshotOnDispose;
    }

    private final class BootstrapThread
    extends Thread {
        private Ehcache cache;

        public BootstrapThread(Ehcache cache) {
            super("Bootstrap Thread for cache " + cache.getName());
            this.cache = cache;
            this.setDaemon(true);
            this.setPriority(5);
        }

        @Override
        public final void run() {
            try {
                TerracottaBootstrapCacheLoader.this.doLoad(this.cache);
            }
            catch (RemoteCacheException e) {
                LOG.warn("Error asynchronously performing bootstrap. The cause was: " + e.getMessage(), (Throwable)e);
            }
            this.cache = null;
        }
    }
}

