/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.Toolkit
 *  org.terracotta.toolkit.ToolkitObjectType
 *  org.terracotta.toolkit.collections.ToolkitMap
 *  org.terracotta.toolkit.concurrent.locks.ToolkitLock
 *  org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock
 */
package com.terracotta.entity.ehcache;

import com.terracotta.entity.ClusteredEntityState;
import com.terracotta.entity.EntityLockHandler;
import com.terracotta.entity.ehcache.ClusteredCache;
import com.terracotta.entity.ehcache.ClusteredCacheManager;
import com.terracotta.entity.ehcache.ClusteredCacheManagerConfiguration;
import com.terracotta.entity.ehcache.EhcacheEntitiesNaming;
import com.terracotta.entity.ehcache.ToolkitBackedClusteredCache;
import com.terracotta.entity.internal.InternalRootEntity;
import com.terracotta.entity.internal.LockingEntity;
import com.terracotta.entity.internal.ToolkitAwareEntity;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.ToolkitObjectType;
import org.terracotta.toolkit.collections.ToolkitMap;
import org.terracotta.toolkit.concurrent.locks.ToolkitLock;
import org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock;

public class ToolkitBackedClusteredCacheManager
implements ClusteredCacheManager,
ToolkitAwareEntity,
LockingEntity,
InternalRootEntity {
    private static final long serialVersionUID = 1L;
    private static final String CACHE_ENTITY_MAP_PREFIX = "__entity_cache_root@";
    private static final String CACHE_ENTITY_LOCK_PREFIX = "__entity_cache_lock@";
    private static final long TRY_LOCK_TIMEOUT_SECONDS = 2L;
    private final ClusteredCacheManagerConfiguration configuration;
    private final String cacheManagerName;
    private final ConcurrentMap<ToolkitObjectType, Set<String>> toolkitDSInfo;
    private volatile ClusteredEntityState state;
    private volatile transient Toolkit toolkit;
    private volatile transient EntityLockHandler entityLockHandler;
    private volatile transient ToolkitMap<String, ToolkitBackedClusteredCache> localCachesMap;

    public ToolkitBackedClusteredCacheManager(String cacheManagerName, ClusteredCacheManagerConfiguration configuration) {
        this.cacheManagerName = cacheManagerName;
        this.configuration = configuration;
        this.state = ClusteredEntityState.LIVE;
        this.toolkitDSInfo = new ConcurrentHashMap<ToolkitObjectType, Set<String>>();
        this.addCacheManagerMetaInfo(ToolkitObjectType.MAP, EhcacheEntitiesNaming.getCacheManagerConfigMapName(cacheManagerName));
    }

    @Override
    public ClusteredCacheManagerConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public ClusteredEntityState getState() {
        return this.state;
    }

    @Override
    public void setToolkit(Toolkit toolkit) {
        this.toolkit = toolkit;
    }

    @Override
    public void setEntityLockHandler(EntityLockHandler entityLockHandler) {
        this.entityLockHandler = entityLockHandler;
    }

    @Override
    public Map<String, ClusteredCache> getCaches() {
        HashMap resultMap = new HashMap();
        for (Map.Entry cacheEntry : this.getCachesMap().entrySet()) {
            ToolkitBackedClusteredCache clusteredCache = (ToolkitBackedClusteredCache)cacheEntry.getValue();
            if (ClusteredEntityState.DESTROY_IN_PROGRESS.equals((Object)clusteredCache.getState())) {
                this.destroyCacheSilently(clusteredCache);
                continue;
            }
            resultMap.put(cacheEntry.getKey(), this.processEntry(clusteredCache));
        }
        return Collections.unmodifiableMap(resultMap);
    }

    @Override
    public ClusteredCache getCache(String cacheName) {
        ToolkitBackedClusteredCache cache = this.getCacheInternal(cacheName);
        if (cache != null && ClusteredEntityState.DESTROY_IN_PROGRESS.equals((Object)cache.getState())) {
            this.destroyCacheSilently(cache);
            return null;
        }
        return cache;
    }

    @Override
    public ClusteredCache addCacheIfAbsent(String cacheName, ClusteredCache clusteredCache) {
        ToolkitBackedClusteredCache tkClusteredCache = this.asToolkitClusteredCache(clusteredCache);
        return (ClusteredCache)this.getCachesMap().putIfAbsent((Object)cacheName, (Object)tkClusteredCache);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean destroyCache(ClusteredCache clusteredCache) {
        ToolkitLock entityReadLock = this.getEntityLock().readLock();
        try {
            if (!entityReadLock.tryLock(2L, TimeUnit.SECONDS)) {
                throw new IllegalStateException(String.format("Clustered cache manager %s is not allowing shared access", this.cacheManagerName));
            }
            try {
                ToolkitBackedClusteredCache tkClusteredCache = this.asToolkitClusteredCache(clusteredCache);
                if (!this.getCachesMap().containsKey((Object)tkClusteredCache.getName())) {
                    boolean bl = false;
                    return bl;
                }
                ToolkitLock writeLock = this.getCacheLock(tkClusteredCache.getName()).writeLock();
                if (writeLock.tryLock(2L, TimeUnit.SECONDS)) {
                    try {
                        ToolkitBackedClusteredCache currentClusteredCache = this.getCacheInternal(tkClusteredCache.getName());
                        if (!currentClusteredCache.equals(tkClusteredCache)) {
                            throw new IllegalArgumentException(String.format("The specified clustered cache named %s does not match the mapping known to clustered cache manager named %s", tkClusteredCache.getName(), this.cacheManagerName));
                        }
                        tkClusteredCache.markDestroyInProgress();
                        try {
                            this.getCachesMap().put((Object)tkClusteredCache.getName(), (Object)tkClusteredCache);
                        }
                        catch (Exception e) {
                            tkClusteredCache.alive();
                            throw new UnsupportedOperationException(String.format("Unable to mark cache %s with destroy in progress", tkClusteredCache.getName()), e);
                        }
                        this.processEntry(tkClusteredCache).destroy();
                        this.getCachesMap().remove((Object)clusteredCache.getName());
                        boolean bl = true;
                        return bl;
                    }
                    finally {
                        writeLock.unlock();
                    }
                }
                throw new IllegalStateException(String.format("Unable to lock cache %s for destruction", clusteredCache.getName()));
            }
            finally {
                entityReadLock.unlock();
            }
        }
        catch (InterruptedException e) {
            throw new IllegalStateException(String.format("Clustered cache manager %s is not allowing shared access", this.cacheManagerName), e);
        }
    }

    public void addCacheMetaInfo(String cacheName, ToolkitObjectType type, String dsName) {
        this.assertCacheExist(cacheName);
        ToolkitBackedClusteredCache tkClusteredCache = this.asToolkitClusteredCache(this.getCache(cacheName));
        if (tkClusteredCache.addToolkitDSMetaInfo(type, dsName)) {
            this.getCachesMap().put((Object)tkClusteredCache.getName(), (Object)tkClusteredCache);
        }
    }

    public void addKeyRemoveInfo(String cacheName, String toolkitMapName, String keytoBeRemoved) {
        this.assertCacheExist(cacheName);
        ToolkitBackedClusteredCache tkClusteredCache = this.asToolkitClusteredCache(this.getCache(cacheName));
        if (tkClusteredCache.addKeyRemoveInfo(toolkitMapName, keytoBeRemoved)) {
            this.getCachesMap().put((Object)tkClusteredCache.getName(), (Object)tkClusteredCache);
        }
    }

    @Override
    public ToolkitReadWriteLock getCacheLock(String cacheName) {
        return this.toolkit.getReadWriteLock(this.getCacheLockName(cacheName));
    }

    @Override
    public ToolkitReadWriteLock getEntityLock() {
        return this.toolkit.getReadWriteLock(EhcacheEntitiesNaming.getCacheManagerLockNameFor(this.cacheManagerName));
    }

    @Override
    public void markInUse() {
        this.entityLockHandler.readLock(EhcacheEntitiesNaming.getCacheManagerLockNameFor(this.cacheManagerName));
    }

    @Override
    public void releaseUse() {
        this.entityLockHandler.readUnlock(EhcacheEntitiesNaming.getCacheManagerLockNameFor(this.cacheManagerName));
    }

    @Override
    public boolean isUsed() {
        ToolkitLock entityWriteLock = this.getEntityLock().writeLock();
        if (entityWriteLock.tryLock()) {
            try {
                boolean bl = false;
                return bl;
            }
            finally {
                entityWriteLock.unlock();
            }
        }
        return true;
    }

    @Override
    public void markCacheInUse(ClusteredCache clusteredCache) {
        this.entityLockHandler.readLock(this.getCacheLockName(clusteredCache.getName()));
    }

    @Override
    public void releaseCacheUse(ClusteredCache clusteredCache) {
        this.entityLockHandler.readUnlock(this.getCacheLockName(clusteredCache.getName()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isCacheUsed(ClusteredCache clusteredCache) {
        ClusteredCache currentClusteredCache = this.getCache(clusteredCache.getName());
        if (currentClusteredCache == null || !currentClusteredCache.equals(clusteredCache)) {
            throw new IllegalArgumentException(String.format("The specified clustered cache %s is not know to this clustered cache manager %s", clusteredCache.getName(), this.cacheManagerName));
        }
        ToolkitLock cacheWriteLock = this.getCacheLock(clusteredCache.getName()).writeLock();
        if (cacheWriteLock.tryLock()) {
            try {
                boolean bl = false;
                return bl;
            }
            finally {
                cacheWriteLock.unlock();
            }
        }
        return true;
    }

    @Override
    public void destroy() {
        for (ToolkitBackedClusteredCache toolkitBackedClusteredCache : this.getCachesMap().values()) {
            this.processEntry(toolkitBackedClusteredCache).destroy();
        }
        this.getCachesMap().destroy();
        block4: for (Map.Entry entry : this.toolkitDSInfo.entrySet()) {
            ToolkitObjectType type = (ToolkitObjectType)entry.getKey();
            Set values = (Set)entry.getValue();
            switch (type) {
                case MAP: {
                    for (String name : values) {
                        this.toolkit.getMap(name, String.class, Serializable.class).destroy();
                    }
                    continue block4;
                }
                default: {
                    throw new IllegalStateException("got wrong ToolkitObjectType " + type);
                }
            }
        }
    }

    @Override
    public void markDestroying() {
        this.state = ClusteredEntityState.DESTROY_IN_PROGRESS;
    }

    @Override
    public void alive() {
        this.state = ClusteredEntityState.LIVE;
    }

    String getCachesMapName() {
        return CACHE_ENTITY_MAP_PREFIX + this.cacheManagerName;
    }

    String getCacheLockName(String cacheName) {
        return CACHE_ENTITY_LOCK_PREFIX + this.cacheManagerName + "@" + cacheName;
    }

    private void destroyCacheSilently(ToolkitBackedClusteredCache clusteredCache) {
        try {
            this.destroyCache(clusteredCache);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void addCacheManagerMetaInfo(ToolkitObjectType type, String dsName) {
        HashSet<String> tmpValues = new HashSet<String>();
        tmpValues.add(dsName);
        Set oldValues = this.toolkitDSInfo.putIfAbsent(type, tmpValues);
        if (oldValues != null) {
            oldValues.add(dsName);
        }
    }

    private ToolkitBackedClusteredCache getCacheInternal(String cacheName) {
        return this.processEntry((ToolkitBackedClusteredCache)this.getCachesMap().get((Object)cacheName));
    }

    private void assertCacheExist(String cacheName) {
        if (!this.getCachesMap().containsKey((Object)cacheName)) {
            throw new IllegalArgumentException(String.format("The specified clustered cache named %s does not match the mapping known to clustered cache manager named %s", cacheName, this.cacheManagerName));
        }
    }

    private ToolkitBackedClusteredCache asToolkitClusteredCache(ClusteredCache clusteredCache) {
        if (!(clusteredCache instanceof ToolkitBackedClusteredCache)) {
            throw new IllegalArgumentException("Unexpected implementation of ClusteredCache: " + clusteredCache.getClass());
        }
        return (ToolkitBackedClusteredCache)clusteredCache;
    }

    private ToolkitBackedClusteredCache processEntry(ToolkitBackedClusteredCache clusteredCache) {
        if (clusteredCache != null) {
            clusteredCache.setToolkit(this.toolkit);
        }
        return clusteredCache;
    }

    private ToolkitMap<String, ToolkitBackedClusteredCache> getCachesMap() {
        if (this.localCachesMap == null) {
            this.localCachesMap = this.toolkit.getMap(this.getCachesMapName(), String.class, ToolkitBackedClusteredCache.class);
        }
        return this.localCachesMap;
    }
}

