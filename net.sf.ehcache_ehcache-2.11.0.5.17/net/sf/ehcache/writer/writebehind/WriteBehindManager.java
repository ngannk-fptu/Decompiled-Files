/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.TerracottaStore;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.CacheWriterManager;
import net.sf.ehcache.writer.writebehind.CoalesceKeysFilter;
import net.sf.ehcache.writer.writebehind.WriteBehind;
import net.sf.ehcache.writer.writebehind.WriteBehindQueueManager;
import org.terracotta.statistics.Statistic;

public class WriteBehindManager
implements CacheWriterManager {
    private final WriteBehind writeBehind;

    public WriteBehindManager(Cache cache, Store store) {
        this.writeBehind = cache.isTerracottaClustered() ? ((TerracottaStore)store).createWriteBehind() : (cache.getCacheConfiguration().getPersistenceConfiguration() != null && cache.getCacheConfiguration().getPersistenceConfiguration().getStrategy() == PersistenceConfiguration.Strategy.LOCALRESTARTABLE ? cache.getCacheManager().getFeaturesManager().createWriteBehind(cache) : new WriteBehindQueueManager(cache.getCacheConfiguration()));
    }

    @Override
    public void init(Cache cache) throws CacheException {
        CacheWriter cacheWriter = cache.getRegisteredCacheWriter();
        if (null == cacheWriter) {
            throw new CacheException("No cache writer was registered for cache " + cache.getName());
        }
        if (cache.getCacheConfiguration().getCacheWriterConfiguration().getWriteCoalescing()) {
            this.writeBehind.setOperationsFilter(new CoalesceKeysFilter());
        }
        this.writeBehind.start(cacheWriter);
    }

    @Override
    public void put(Element element) throws CacheException {
        this.writeBehind.write(element);
    }

    @Override
    public void remove(CacheEntry entry) throws CacheException {
        this.writeBehind.delete(entry);
    }

    @Override
    public void dispose() {
        if (this.writeBehind != null) {
            this.writeBehind.stop();
        }
    }

    @Statistic(name="queue-length", tags={"write-behind"})
    public long getQueueSize() {
        return this.writeBehind.getQueueSize();
    }
}

