/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.store;

import java.util.Iterator;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.distribution.RemoteCacheException;
import net.sf.ehcache.store.MemoryLimitedCacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiskStoreBootstrapCacheLoader
extends MemoryLimitedCacheLoader {
    private static final Logger LOG = LoggerFactory.getLogger(DiskStoreBootstrapCacheLoader.class);
    private final boolean asynchronous;

    public DiskStoreBootstrapCacheLoader(boolean asynchronous) {
        this.asynchronous = asynchronous;
    }

    @Override
    public void load(Ehcache cache) throws CacheException {
        if (cache.getCacheConfiguration().isDiskPersistent()) {
            if (this.asynchronous) {
                BootstrapThread thread = new BootstrapThread(cache);
                thread.start();
            } else {
                this.doLoad(cache);
            }
        } else {
            LOG.warn("Cache '" + cache.getName() + "' isn't disk persistent, nothing to laod from!");
        }
    }

    protected void doLoad(Ehcache cache) {
        int loadedElements = 0;
        Iterator iterator = cache.getKeys().iterator();
        while (iterator.hasNext() && !this.isInMemoryLimitReached(cache, loadedElements)) {
            if (cache.get(iterator.next()) == null) continue;
            ++loadedElements;
        }
        LOG.debug("Loaded {} elements from disk into heap for cache {}", (Object)loadedElements, (Object)cache.getName());
    }

    @Override
    public boolean isAsynchronous() {
        return this.asynchronous;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private final class BootstrapThread
    extends Thread {
        private final Ehcache cache;

        public BootstrapThread(Ehcache cache) {
            super("Bootstrap Thread for cache " + cache.getName());
            this.cache = cache;
            this.setDaemon(true);
            this.setPriority(5);
        }

        @Override
        public final void run() {
            try {
                DiskStoreBootstrapCacheLoader.this.doLoad(this.cache);
            }
            catch (RemoteCacheException e) {
                LOG.warn("Error asynchronously performing bootstrap. The cause was: " + e.getMessage(), (Throwable)e);
            }
        }
    }
}

