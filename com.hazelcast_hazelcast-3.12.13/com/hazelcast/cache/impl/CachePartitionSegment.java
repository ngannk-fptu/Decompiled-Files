/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.AbstractCacheService;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CachePartitionSegment
implements ConstructorFunction<String, ICacheRecordStore> {
    protected final int partitionId;
    protected final Object mutex = new Object();
    protected final AbstractCacheService cacheService;
    protected final ConcurrentMap<String, ICacheRecordStore> recordStores = new ConcurrentHashMap<String, ICacheRecordStore>();
    private boolean runningCleanupOperation;
    private volatile long lastCleanupTime;
    private long lastCleanupTimeCopy;

    public CachePartitionSegment(AbstractCacheService cacheService, int partitionId) {
        this.cacheService = cacheService;
        this.partitionId = partitionId;
    }

    @Override
    public ICacheRecordStore createNew(String cacheNameWithPrefix) {
        return this.cacheService.createNewRecordStore(cacheNameWithPrefix, this.partitionId);
    }

    public Iterator<ICacheRecordStore> recordStoreIterator() {
        return this.recordStores.values().iterator();
    }

    public boolean hasRunningCleanupOperation() {
        return this.runningCleanupOperation;
    }

    public void setRunningCleanupOperation(boolean status) {
        this.runningCleanupOperation = status;
    }

    public long getLastCleanupTime() {
        return this.lastCleanupTime;
    }

    public void setLastCleanupTime(long time) {
        this.lastCleanupTime = time;
    }

    public long getLastCleanupTimeBeforeSorting() {
        return this.lastCleanupTimeCopy;
    }

    public void storeLastCleanupTime() {
        this.lastCleanupTimeCopy = this.getLastCleanupTime();
    }

    public Collection<CacheConfig> getCacheConfigs() {
        return this.cacheService.getCacheConfigs();
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public ICacheRecordStore getOrCreateRecordStore(String cacheNameWithPrefix) {
        return ConcurrencyUtil.getOrPutSynchronized(this.recordStores, cacheNameWithPrefix, this.mutex, this);
    }

    public ICacheRecordStore getRecordStore(String cacheNameWithPrefix) {
        return (ICacheRecordStore)this.recordStores.get(cacheNameWithPrefix);
    }

    public ICacheService getCacheService() {
        return this.cacheService;
    }

    public void deleteRecordStore(String name, boolean destroy) {
        if (destroy) {
            ICacheRecordStore store = (ICacheRecordStore)this.recordStores.remove(name);
            if (store != null) {
                store.destroy();
            }
        } else {
            ICacheRecordStore store = (ICacheRecordStore)this.recordStores.get(name);
            if (store != null) {
                store.close(false);
            }
        }
    }

    public boolean hasAnyRecordStore() {
        return !this.recordStores.isEmpty();
    }

    public boolean hasRecordStore(String name) {
        return this.recordStores.containsKey(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void init() {
        Object object = this.mutex;
        synchronized (object) {
            for (ICacheRecordStore store : this.recordStores.values()) {
                store.init();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset() {
        Object object = this.mutex;
        synchronized (object) {
            for (ICacheRecordStore store : this.recordStores.values()) {
                store.reset();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shutdown() {
        Object object = this.mutex;
        synchronized (object) {
            for (ICacheRecordStore store : this.recordStores.values()) {
                store.close(true);
            }
        }
        this.recordStores.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void clearHavingLesserBackupCountThan(int backupCount) {
        Object object = this.mutex;
        synchronized (object) {
            for (ICacheRecordStore store : this.recordStores.values()) {
                CacheConfig cacheConfig = store.getConfig();
                if (backupCount <= cacheConfig.getTotalBackupCount()) continue;
                store.reset();
            }
        }
    }

    public Collection<ServiceNamespace> getAllNamespaces(int replicaIndex) {
        HashSet<ServiceNamespace> namespaces = new HashSet<ServiceNamespace>();
        for (ICacheRecordStore recordStore : this.recordStores.values()) {
            if (recordStore.getConfig().getTotalBackupCount() < replicaIndex) continue;
            namespaces.add(recordStore.getObjectNamespace());
        }
        return namespaces;
    }
}

