/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.event;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

public class TerracottaCacheEventReplication
implements CacheEventListener {
    private final ConcurrentMap<Ehcache, CacheEventListener> replicators = new ConcurrentHashMap<Ehcache, CacheEventListener>();

    @Override
    public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
        if (cache.getCacheConfiguration().isTerracottaClustered()) {
            this.createCacheEventReplicator(cache).notifyElementRemoved(cache, element);
        }
    }

    @Override
    public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
        if (cache.getCacheConfiguration().isTerracottaClustered()) {
            this.createCacheEventReplicator(cache).notifyElementPut(cache, element);
        }
    }

    @Override
    public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
        if (cache.getCacheConfiguration().isTerracottaClustered()) {
            this.createCacheEventReplicator(cache).notifyElementUpdated(cache, element);
        }
    }

    @Override
    public void notifyElementExpired(Ehcache cache, Element element) {
        if (cache.getCacheConfiguration().isTerracottaClustered()) {
            this.createCacheEventReplicator(cache).notifyElementExpired(cache, element);
        }
    }

    @Override
    public void notifyElementEvicted(Ehcache cache, Element element) {
        if (cache.getCacheConfiguration().isTerracottaClustered()) {
            this.createCacheEventReplicator(cache).notifyElementEvicted(cache, element);
        }
    }

    @Override
    public void notifyRemoveAll(Ehcache cache) {
        if (cache.getCacheConfiguration().isTerracottaClustered()) {
            this.createCacheEventReplicator(cache).notifyRemoveAll(cache);
        }
    }

    private CacheEventListener createCacheEventReplicator(Ehcache cache) {
        CacheEventListener replicator = (CacheEventListener)this.replicators.get(cache);
        if (null == replicator) {
            replicator = cache.getCacheManager().createTerracottaEventReplicator(cache);
            this.replicators.put(cache, replicator);
        }
        return replicator;
    }

    @Override
    public TerracottaCacheEventReplication clone() throws CloneNotSupportedException {
        return (TerracottaCacheEventReplication)super.clone();
    }

    @Override
    public void dispose() {
    }
}

