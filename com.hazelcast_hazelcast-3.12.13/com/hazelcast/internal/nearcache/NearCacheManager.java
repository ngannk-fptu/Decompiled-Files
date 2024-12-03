/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache;

import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.internal.adapter.DataStructureAdapter;
import com.hazelcast.internal.nearcache.NearCache;
import java.util.Collection;

public interface NearCacheManager {
    public <K, V> NearCache<K, V> getNearCache(String var1);

    public <K, V> NearCache<K, V> getOrCreateNearCache(String var1, NearCacheConfig var2);

    public <K, V> NearCache<K, V> getOrCreateNearCache(String var1, NearCacheConfig var2, DataStructureAdapter var3);

    public Collection<NearCache> listAllNearCaches();

    public boolean clearNearCache(String var1);

    public void clearAllNearCaches();

    public boolean destroyNearCache(String var1);

    public void destroyAllNearCaches();
}

