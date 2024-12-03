/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writethrough;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.CacheWriterManager;
import net.sf.ehcache.writer.CacheWriterManagerException;

public class WriteThroughManager
implements CacheWriterManager {
    private volatile Cache cache;

    @Override
    public void init(Cache cache) throws CacheException {
        this.cache = cache;
    }

    @Override
    public void put(Element element) throws CacheException {
        try {
            CacheWriter writer = this.cache.getRegisteredCacheWriter();
            if (writer != null) {
                writer.write(element);
            }
        }
        catch (RuntimeException e) {
            throw new CacheWriterManagerException(e);
        }
    }

    @Override
    public void remove(CacheEntry entry) throws CacheException {
        try {
            CacheWriter writer = this.cache.getRegisteredCacheWriter();
            if (writer != null) {
                writer.delete(entry);
            }
        }
        catch (RuntimeException e) {
            throw new CacheWriterManagerException(e);
        }
    }

    @Override
    public void dispose() {
    }
}

