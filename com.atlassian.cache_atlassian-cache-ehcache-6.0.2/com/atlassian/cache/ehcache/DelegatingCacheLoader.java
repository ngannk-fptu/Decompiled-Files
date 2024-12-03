/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheLoader
 *  net.sf.ehcache.CacheException
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Status
 *  net.sf.ehcache.loader.CacheLoader
 */
package com.atlassian.cache.ehcache;

import com.atlassian.cache.CacheLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;

class DelegatingCacheLoader<K, V>
implements net.sf.ehcache.loader.CacheLoader,
Cloneable {
    private CacheLoader<K, V> loader;

    public DelegatingCacheLoader(CacheLoader<K, V> loader) {
        this.loader = loader;
    }

    public Object load(Object key) throws CacheException {
        return this.loader.load(key);
    }

    public Map loadAll(Collection keys) {
        HashMap map = new HashMap();
        for (Object key : keys) {
            Object value = this.loader.load(key);
            if (value == null) continue;
            map.put(key, value);
        }
        return map;
    }

    public Object load(Object key, Object argument) {
        return this.load(key);
    }

    public Map loadAll(Collection keys, Object argument) {
        return this.loadAll(keys);
    }

    public String getName() {
        return this.loader.getClass().getName();
    }

    public net.sf.ehcache.loader.CacheLoader clone(Ehcache cache) throws CloneNotSupportedException {
        return (net.sf.ehcache.loader.CacheLoader)super.clone();
    }

    public void init() {
    }

    public void dispose() throws CacheException {
        this.loader = null;
    }

    public Status getStatus() {
        return this.loader == null ? Status.STATUS_ALIVE : Status.STATUS_UNINITIALISED;
    }
}

