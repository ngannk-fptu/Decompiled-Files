/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.store.Store;

public class CacheStoreHelper {
    private final Cache cache;

    public CacheStoreHelper(Cache cache) {
        this.cache = cache;
    }

    public Store getStore() {
        return this.cache.getStore();
    }
}

