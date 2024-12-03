/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.eviction;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.nearcache.MapNearCacheManager;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.memory.MemoryUnit;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.MemoryInfoAccessor;
import com.hazelcast.util.Preconditions;
import java.util.concurrent.atomic.AtomicBoolean;

public class EvictionChecker {
    protected static final double ONE_HUNDRED = 100.0;
    private static final int MIN_TRANSLATED_PARTITION_SIZE = 1;
    private final int partitionCount;
    private final ILogger logger;
    private final ClusterService clusterService;
    private final PartitionContainer[] containers;
    private final MemoryInfoAccessor memoryInfoAccessor;
    private final MapNearCacheManager mapNearCacheManager;
    private final AtomicBoolean misconfiguredPerNodeMaxSizeWarningLogged;

    public EvictionChecker(MemoryInfoAccessor givenMemoryInfoAccessor, MapServiceContext mapServiceContext) {
        Preconditions.checkNotNull(givenMemoryInfoAccessor, "givenMemoryInfoAccessor cannot be null");
        Preconditions.checkNotNull(mapServiceContext, "mapServiceContext cannot be null");
        NodeEngine nodeEngine = mapServiceContext.getNodeEngine();
        this.logger = nodeEngine.getLogger(this.getClass());
        this.containers = mapServiceContext.getPartitionContainers();
        this.clusterService = nodeEngine.getClusterService();
        this.partitionCount = nodeEngine.getPartitionService().getPartitionCount();
        this.mapNearCacheManager = mapServiceContext.getMapNearCacheManager();
        this.memoryInfoAccessor = givenMemoryInfoAccessor;
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Used memoryInfoAccessor=" + this.memoryInfoAccessor.getClass().getCanonicalName());
        }
        this.misconfiguredPerNodeMaxSizeWarningLogged = new AtomicBoolean();
    }

    public boolean checkEvictable(RecordStore recordStore) {
        if (recordStore.size() == 0) {
            return false;
        }
        String mapName = recordStore.getName();
        MapContainer mapContainer = recordStore.getMapContainer();
        MaxSizeConfig maxSizeConfig = mapContainer.getMapConfig().getMaxSizeConfig();
        MaxSizeConfig.MaxSizePolicy maxSizePolicy = maxSizeConfig.getMaxSizePolicy();
        int maxConfiguredSize = maxSizeConfig.getSize();
        switch (maxSizePolicy) {
            case PER_NODE: {
                return (double)recordStore.size() > this.toPerPartitionMaxSize(maxConfiguredSize, mapName);
            }
            case PER_PARTITION: {
                return recordStore.size() > maxConfiguredSize;
            }
            case USED_HEAP_SIZE: {
                return this.usedHeapInBytes(mapName) > MemoryUnit.MEGABYTES.toBytes(maxConfiguredSize);
            }
            case FREE_HEAP_SIZE: {
                return this.availableMemoryInBytes() < MemoryUnit.MEGABYTES.toBytes(maxConfiguredSize);
            }
            case USED_HEAP_PERCENTAGE: {
                return (double)this.usedHeapInBytes(mapName) * 100.0 / (double)Math.max(this.maxMemoryInBytes(), 1L) > (double)maxConfiguredSize;
            }
            case FREE_HEAP_PERCENTAGE: {
                return (double)this.availableMemoryInBytes() * 100.0 / (double)Math.max(this.maxMemoryInBytes(), 1L) < (double)maxConfiguredSize;
            }
        }
        throw new IllegalArgumentException("Not an appropriate max size policy [" + (Object)((Object)maxSizePolicy) + ']');
    }

    private double toPerPartitionMaxSize(int maxConfiguredSize, String mapName) {
        int memberCount = this.clusterService.getSize(MemberSelectors.DATA_MEMBER_SELECTOR);
        double translatedPartitionSize = 1.0 * (double)maxConfiguredSize * (double)memberCount / (double)this.partitionCount;
        if (translatedPartitionSize < 1.0) {
            translatedPartitionSize = 1.0;
            this.logMisconfiguredPerNodeMaxSize(mapName, memberCount);
        }
        return translatedPartitionSize;
    }

    private void logMisconfiguredPerNodeMaxSize(String mapName, int memberCount) {
        if (this.misconfiguredPerNodeMaxSizeWarningLogged.get()) {
            return;
        }
        if (this.misconfiguredPerNodeMaxSizeWarningLogged.compareAndSet(false, true)) {
            int minMaxSize = (int)Math.ceil(1.0 * (double)this.partitionCount / (double)memberCount);
            int newSize = 1 * this.partitionCount / memberCount;
            this.logger.warning(String.format("The max size configuration for map \"%s\" does not allow any data in the map. Given the current cluster size of %d members with %d partitions, max size should be at least %d. Map size is forced set to %d for backward compatibility", mapName, memberCount, this.partitionCount, minMaxSize, newSize));
        }
    }

    private long usedHeapInBytes(String mapName) {
        long usedHeapInBytes = 0L;
        for (int partitionId = 0; partitionId < this.partitionCount; ++partitionId) {
            usedHeapInBytes += this.getRecordStoreHeapCost(mapName, this.containers[partitionId]);
        }
        NearCache nearCache = this.mapNearCacheManager.getNearCache(mapName);
        if (nearCache != null) {
            NearCacheStats nearCacheStats = nearCache.getNearCacheStats();
            usedHeapInBytes += nearCacheStats.getOwnedEntryMemoryCost();
        }
        return usedHeapInBytes;
    }

    private long getRecordStoreHeapCost(String mapName, PartitionContainer container) {
        RecordStore existingRecordStore = container.getExistingRecordStore(mapName);
        if (existingRecordStore == null) {
            return 0L;
        }
        return existingRecordStore.getOwnedEntryCost();
    }

    private long totalMemoryInBytes() {
        return this.memoryInfoAccessor.getTotalMemory();
    }

    private long freeMemoryInBytes() {
        return this.memoryInfoAccessor.getFreeMemory();
    }

    private long maxMemoryInBytes() {
        return this.memoryInfoAccessor.getMaxMemory();
    }

    private long availableMemoryInBytes() {
        return this.freeMemoryInBytes() + this.maxMemoryInBytes() - this.totalMemoryInBytes();
    }
}

