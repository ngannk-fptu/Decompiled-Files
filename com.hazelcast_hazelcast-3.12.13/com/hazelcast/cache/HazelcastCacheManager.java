/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheManager
 */
package com.hazelcast.cache;

import com.hazelcast.core.HazelcastInstance;
import javax.cache.CacheManager;

public interface HazelcastCacheManager
extends CacheManager {
    public static final String CACHE_MANAGER_PREFIX = "/hz/";

    public String getCacheNameWithPrefix(String var1);

    public HazelcastInstance getHazelcastInstance();

    public void removeCache(String var1, boolean var2);

    public void destroy();
}

