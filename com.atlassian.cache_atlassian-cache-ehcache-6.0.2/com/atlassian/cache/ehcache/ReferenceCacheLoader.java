/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Supplier
 *  com.atlassian.cache.impl.ReferenceKey
 *  net.sf.ehcache.CacheException
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Status
 *  net.sf.ehcache.loader.CacheLoader
 */
package com.atlassian.cache.ehcache;

import com.atlassian.cache.Supplier;
import com.atlassian.cache.impl.ReferenceKey;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.loader.CacheLoader;

class ReferenceCacheLoader<V>
implements CacheLoader,
Cloneable {
    private Supplier<V> supplier;

    public ReferenceCacheLoader(Supplier<V> supplier) {
        this.supplier = supplier;
    }

    public Object load(Object key) {
        return this.supplier.get();
    }

    public Map loadAll(Collection keys) {
        HashMap<ReferenceKey, Object> map = new HashMap<ReferenceKey, Object>();
        for (Object key : keys) {
            Object value = this.supplier.get();
            if (value == null) continue;
            map.put(ReferenceKey.KEY, value);
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
        return this.supplier.getClass().getName();
    }

    public CacheLoader clone(Ehcache cache) throws CloneNotSupportedException {
        return (CacheLoader)super.clone();
    }

    public void init() {
    }

    public void dispose() throws CacheException {
        this.supplier = null;
    }

    public Status getStatus() {
        return this.supplier == null ? Status.STATUS_ALIVE : Status.STATUS_UNINITIALISED;
    }
}

