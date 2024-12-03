/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.bandana.impl;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaPersister;
import com.atlassian.bandana.impl.PersisterKey;
import com.atlassian.cache.Cache;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CachingBandanaPersister
implements BandanaPersister {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private final BandanaPersister delegatedPersister;
    private final Cache<PersisterKey, Object> cache;

    public CachingBandanaPersister(BandanaPersister delegatedPersister, Cache<PersisterKey, Object> cache) {
        this.delegatedPersister = delegatedPersister;
        this.cache = cache;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object retrieve(BandanaContext context, String key) {
        PersisterKey persisterKey = new PersisterKey(context, key);
        if (this.cache.get((Object)persisterKey) == null) {
            Cache<PersisterKey, Object> cache = this.cache;
            synchronized (cache) {
                Object value = this.delegatedPersister.retrieve(context, key);
                this.cache.put((Object)persisterKey, value);
            }
        }
        return this.cache.get((Object)persisterKey);
    }

    @Override
    public Map<String, Object> retrieve(BandanaContext context) {
        return this.delegatedPersister.retrieve(context);
    }

    @Override
    public Iterable<String> retrieveKeys(BandanaContext context) {
        throw new UnsupportedOperationException();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void store(BandanaContext context, String key, Object value) {
        this.delegatedPersister.store(context, key, value);
        PersisterKey pKey = new PersisterKey(context, key);
        Cache<PersisterKey, Object> cache = this.cache;
        synchronized (cache) {
            this.cache.put((Object)pKey, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void flushCaches() {
        Cache<PersisterKey, Object> cache = this.cache;
        synchronized (cache) {
            this.cache.removeAll();
        }
    }

    @Override
    public void remove(BandanaContext context) {
        this.delegatedPersister.remove(context);
        for (PersisterKey persisterKey : this.cache.getKeys()) {
            if (!persisterKey.getContext().equals(context)) continue;
            this.cache.remove((Object)persisterKey);
        }
    }

    @Override
    public void remove(BandanaContext context, String key) {
        this.cache.remove((Object)new PersisterKey(context, key));
    }
}

