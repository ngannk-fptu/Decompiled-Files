/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.base.Preconditions
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 */
package com.atlassian.confluence.cache.hazelcast;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.cache.hazelcast.HazelcastHelper;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.base.Preconditions;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Internal
public class DefaultHazelcastHelper
implements HazelcastHelper {
    protected static final String PREFIX = "atlassian-cache.";
    protected static final String PREFIX_CACHE = "atlassian-cache.Cache.";
    protected static final String PREFIX_CACHE_REFERENCE = "atlassian-cache.CacheReference.";
    private final Supplier<HazelcastInstance> instanceSupplier;

    public DefaultHazelcastHelper(Supplier<HazelcastInstance> instanceSupplier) {
        this.instanceSupplier = (Supplier)Preconditions.checkNotNull(instanceSupplier);
    }

    public DefaultHazelcastHelper(HazelcastInstance hazelcastInstance) {
        this.instanceSupplier = () -> (HazelcastInstance)Preconditions.checkNotNull((Object)hazelcastInstance);
    }

    @Override
    public IMap getHazelcastMapForCache(String cacheName) {
        return ((HazelcastInstance)this.instanceSupplier.get()).getMap(this.getHazelcastMapNameForCache(cacheName));
    }

    @Override
    public IMap getHazelcastMapForCachedReference(String cacheName) {
        return ((HazelcastInstance)this.instanceSupplier.get()).getMap(this.getHazelcastMapNameForCachedReference(cacheName));
    }

    @Override
    public String getHazelcastMapNameForCache(String cacheName) {
        return PREFIX_CACHE + cacheName;
    }

    @Override
    public String getHazelcastMapNameForCachedReference(String cacheName) {
        return PREFIX_CACHE_REFERENCE + cacheName;
    }

    @Override
    public String getBaseSharedDataName() {
        return "confluenceHazelcastSharedData.*";
    }
}

