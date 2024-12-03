/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.constructs.blocking;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.concurrent.LockType;
import net.sf.ehcache.concurrent.Sync;
import net.sf.ehcache.constructs.blocking.LockTimeoutException;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdatingSelfPopulatingCache
extends SelfPopulatingCache {
    private static final Logger LOG = LoggerFactory.getLogger((String)UpdatingSelfPopulatingCache.class.getName());

    public UpdatingSelfPopulatingCache(Ehcache cache, UpdatingCacheEntryFactory factory) throws CacheException {
        super(cache, factory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element get(Object key) throws LockTimeoutException {
        try {
            Ehcache backingCache = this.getCache();
            Element element = backingCache.get(key);
            if (element == null) {
                element = super.get(key);
            } else {
                Sync lock = this.getLockForKey(key);
                try {
                    lock.lock(LockType.WRITE);
                    this.update(key);
                }
                finally {
                    lock.unlock(LockType.WRITE);
                }
            }
            return element;
        }
        catch (Throwable throwable) {
            this.put(new Element(key, null));
            throw new LockTimeoutException("Could not update object for cache entry with key \"" + key + "\".", throwable);
        }
    }

    protected void update(Object key) {
        try {
            Ehcache backingCache = this.getCache();
            Element element = backingCache.getQuiet(key);
            if (element == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(this.getName() + ": entry with key " + key + " has been removed - skipping it");
                }
                return;
            }
            this.refreshElement(element, backingCache);
        }
        catch (Exception e) {
            LOG.warn(this.getName() + "Could not refresh element " + key, (Throwable)e);
        }
    }

    @Override
    public void refresh() throws CacheException {
        throw new CacheException("UpdatingSelfPopulatingCache objects should not be refreshed.");
    }
}

