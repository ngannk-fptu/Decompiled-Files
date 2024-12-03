/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.map.LRUMap
 */
package org.apache.velocity.runtime.resource;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections.map.LRUMap;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.ResourceCache;
import org.apache.velocity.util.MapFactory;

public class ResourceCacheImpl
implements ResourceCache {
    protected Map cache = MapFactory.create(512, 0.5f, 30, false);
    protected RuntimeServices rsvc = null;

    public void initialize(RuntimeServices rs) {
        this.rsvc = rs;
        int maxSize = this.rsvc.getInt("resource.manager.defaultcache.size", 89);
        if (maxSize > 0) {
            Map lruCache = Collections.synchronizedMap(new LRUMap(maxSize));
            lruCache.putAll(this.cache);
            this.cache = lruCache;
        }
        this.rsvc.getLog().debug("ResourceCache: initialized (" + this.getClass() + ") with " + this.cache.getClass() + " cache map.");
    }

    public Resource get(Object key) {
        return (Resource)this.cache.get(key);
    }

    public Resource put(Object key, Resource value) {
        return this.cache.put(key, value);
    }

    public Resource remove(Object key) {
        return (Resource)this.cache.remove(key);
    }

    public Iterator enumerateKeys() {
        return this.cache.keySet().iterator();
    }
}

