/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.cache.ICache;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.ICacheManager;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.spi.exception.ServiceNotFoundException;
import com.hazelcast.util.Preconditions;

public class HazelcastInstanceCacheManager
implements ICacheManager {
    private final HazelcastInstanceImpl original;

    public HazelcastInstanceCacheManager(HazelcastInstanceImpl original) {
        this.original = original;
    }

    @Override
    public <K, V> ICache<K, V> getCache(String name) {
        Preconditions.checkNotNull(name, "Retrieving a cache instance with a null name is not allowed!");
        return this.getCacheByFullName("/hz/" + name);
    }

    public <K, V> ICache<K, V> getCacheByFullName(String fullName) {
        Preconditions.checkNotNull(fullName, "Retrieving a cache instance with a null name is not allowed!");
        try {
            return (ICache)this.original.getDistributedObject("hz:impl:cacheService", fullName);
        }
        catch (HazelcastException e) {
            if (e.getCause() instanceof ServiceNotFoundException) {
                throw new IllegalStateException("There is no valid JCache API library at classpath. Please be sure that there is a JCache API library in your classpath and it is newer than `0.x` and `1.0.0-PFD` versions!");
            }
            throw e;
        }
    }
}

