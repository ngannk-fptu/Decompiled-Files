/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock
 */
package com.terracotta.entity.ehcache;

import com.terracotta.entity.RootEntity;
import com.terracotta.entity.ehcache.ClusteredCache;
import com.terracotta.entity.ehcache.ClusteredCacheManagerConfiguration;
import java.util.Map;
import org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock;

public interface ClusteredCacheManager
extends RootEntity<ClusteredCacheManagerConfiguration> {
    public Map<String, ClusteredCache> getCaches();

    public ClusteredCache getCache(String var1);

    public ClusteredCache addCacheIfAbsent(String var1, ClusteredCache var2);

    public boolean destroyCache(ClusteredCache var1);

    public ToolkitReadWriteLock getCacheLock(String var1);

    public void markInUse();

    public void releaseUse();

    public boolean isUsed();

    public void markCacheInUse(ClusteredCache var1);

    public void releaseCacheUse(ClusteredCache var1);

    public boolean isCacheUsed(ClusteredCache var1);
}

