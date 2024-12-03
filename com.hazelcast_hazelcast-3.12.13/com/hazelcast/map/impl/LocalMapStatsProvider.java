/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.nearcache.MapNearCacheManager;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.monitor.LocalRecordStoreStats;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.monitor.impl.IndexesStats;
import com.hazelcast.monitor.impl.LocalMapStatsImpl;
import com.hazelcast.monitor.impl.OnDemandIndexStats;
import com.hazelcast.monitor.impl.PerIndexStats;
import com.hazelcast.nio.Address;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ProxyService;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ExceptionUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class LocalMapStatsProvider {
    public static final LocalMapStats EMPTY_LOCAL_MAP_STATS = new LocalMapStatsImpl();
    private static final int RETRY_COUNT = 3;
    private static final int WAIT_PARTITION_TABLE_UPDATE_MILLIS = 100;
    private final ILogger logger;
    private final Address localAddress;
    private final NodeEngine nodeEngine;
    private final ClusterService clusterService;
    private final MapServiceContext mapServiceContext;
    private final MapNearCacheManager mapNearCacheManager;
    private final IPartitionService partitionService;
    private final ConcurrentMap<String, LocalMapStatsImpl> statsMap = new ConcurrentHashMap<String, LocalMapStatsImpl>(1000);
    private final ConstructorFunction<String, LocalMapStatsImpl> constructorFunction = new ConstructorFunction<String, LocalMapStatsImpl>(){

        @Override
        public LocalMapStatsImpl createNew(String key) {
            return new LocalMapStatsImpl();
        }
    };

    public LocalMapStatsProvider(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
        this.nodeEngine = mapServiceContext.getNodeEngine();
        this.logger = this.nodeEngine.getLogger(this.getClass());
        this.mapNearCacheManager = mapServiceContext.getMapNearCacheManager();
        this.clusterService = this.nodeEngine.getClusterService();
        this.partitionService = this.nodeEngine.getPartitionService();
        this.localAddress = this.clusterService.getThisAddress();
    }

    protected MapServiceContext getMapServiceContext() {
        return this.mapServiceContext;
    }

    public LocalMapStatsImpl getLocalMapStatsImpl(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.statsMap, name, this.constructorFunction);
    }

    public void destroyLocalMapStatsImpl(String name) {
        this.statsMap.remove(name);
    }

    public LocalMapStatsImpl createLocalMapStats(String mapName) {
        LocalMapStatsImpl stats = this.getLocalMapStatsImpl(mapName);
        LocalMapOnDemandCalculatedStats onDemandStats = new LocalMapOnDemandCalculatedStats();
        this.addNearCacheStats(mapName, stats, onDemandStats);
        this.addIndexStats(mapName, stats);
        this.updateMapOnDemandStats(mapName, onDemandStats);
        return onDemandStats.updateAndGet(stats);
    }

    public Map<String, LocalMapStats> createAllLocalMapStats() {
        HashMap<String, Object> statsPerMap = new HashMap<String, Object>();
        PartitionContainer[] partitionContainers = this.mapServiceContext.getPartitionContainers();
        for (PartitionContainer partitionContainer : partitionContainers) {
            Collection<RecordStore> allRecordStores = partitionContainer.getAllRecordStores();
            for (RecordStore recordStore : allRecordStores) {
                if (!LocalMapStatsProvider.isStatsCalculationEnabledFor(recordStore)) continue;
                IPartition partition = this.partitionService.getPartition(partitionContainer.getPartitionId(), false);
                if (partition.isLocal()) {
                    LocalMapStatsProvider.addPrimaryStatsOf(recordStore, LocalMapStatsProvider.getOrCreateOnDemandStats(statsPerMap, recordStore));
                    continue;
                }
                this.addReplicaStatsOf(recordStore, LocalMapStatsProvider.getOrCreateOnDemandStats(statsPerMap, recordStore));
            }
        }
        for (Object e : statsPerMap.entrySet()) {
            Map.Entry entry = (Map.Entry)e;
            String mapName = (String)entry.getKey();
            LocalMapStatsImpl existingStats = this.getLocalMapStatsImpl(mapName);
            LocalMapOnDemandCalculatedStats onDemand = (LocalMapOnDemandCalculatedStats)entry.getValue();
            this.addNearCacheStats(mapName, existingStats, onDemand);
            this.addIndexStats(mapName, existingStats);
            this.addStructureStats(mapName, onDemand);
            LocalMapStatsImpl updatedStats = onDemand.updateAndGet(existingStats);
            entry.setValue(updatedStats);
        }
        this.addStatsOfNoDataIncludedMaps(statsPerMap);
        return statsPerMap;
    }

    private void addStatsOfNoDataIncludedMaps(Map statsPerMap) {
        ProxyService proxyService = this.nodeEngine.getProxyService();
        Collection<String> mapNames = proxyService.getDistributedObjectNames("hz:impl:mapService");
        for (String mapName : mapNames) {
            if (statsPerMap.containsKey(mapName)) continue;
            statsPerMap.put(mapName, EMPTY_LOCAL_MAP_STATS);
        }
    }

    private static boolean isStatsCalculationEnabledFor(RecordStore recordStore) {
        return recordStore.getMapContainer().getMapConfig().isStatisticsEnabled();
    }

    private static LocalMapOnDemandCalculatedStats getOrCreateOnDemandStats(Map<String, Object> onDemandStats, RecordStore recordStore) {
        String mapName = recordStore.getName();
        Object stats = onDemandStats.get(mapName);
        if (stats == null) {
            stats = new LocalMapOnDemandCalculatedStats();
            onDemandStats.put(mapName, stats);
        }
        return (LocalMapOnDemandCalculatedStats)stats;
    }

    private void updateMapOnDemandStats(String mapName, LocalMapOnDemandCalculatedStats onDemandStats) {
        PartitionContainer[] partitionContainers;
        for (PartitionContainer partitionContainer : partitionContainers = this.mapServiceContext.getPartitionContainers()) {
            IPartition partition = this.partitionService.getPartition(partitionContainer.getPartitionId());
            if (partition.isLocal()) {
                LocalMapStatsProvider.addPrimaryStatsOf(partitionContainer.getExistingRecordStore(mapName), onDemandStats);
                continue;
            }
            this.addReplicaStatsOf(partitionContainer.getExistingRecordStore(mapName), onDemandStats);
        }
        this.addStructureStats(mapName, onDemandStats);
    }

    protected void addStructureStats(String mapName, LocalMapOnDemandCalculatedStats onDemandStats) {
    }

    private static void addPrimaryStatsOf(RecordStore recordStore, LocalMapOnDemandCalculatedStats onDemandStats) {
        if (recordStore != null) {
            onDemandStats.incrementLockedEntryCount(recordStore.getLockedEntryCount());
        }
        if (!LocalMapStatsProvider.hasRecords(recordStore)) {
            return;
        }
        LocalRecordStoreStats stats = recordStore.getLocalRecordStoreStats();
        onDemandStats.incrementHits(stats.getHits());
        onDemandStats.incrementDirtyEntryCount(recordStore.getMapDataStore().notFinishedOperationsCount());
        onDemandStats.incrementOwnedEntryMemoryCost(recordStore.getOwnedEntryCost());
        if (InMemoryFormat.NATIVE != recordStore.getMapContainer().getMapConfig().getInMemoryFormat()) {
            onDemandStats.incrementHeapCost(recordStore.getOwnedEntryCost());
        }
        onDemandStats.incrementOwnedEntryCount(recordStore.size());
        onDemandStats.setLastAccessTime(stats.getLastAccessTime());
        onDemandStats.setLastUpdateTime(stats.getLastUpdateTime());
        onDemandStats.setBackupCount(recordStore.getMapContainer().getMapConfig().getTotalBackupCount());
    }

    private void addReplicaStatsOf(RecordStore recordStore, LocalMapOnDemandCalculatedStats onDemandStats) {
        if (!LocalMapStatsProvider.hasRecords(recordStore)) {
            return;
        }
        long backupEntryCount = 0L;
        long backupEntryMemoryCost = 0L;
        int totalBackupCount = recordStore.getMapContainer().getTotalBackupCount();
        for (int replicaNumber = 1; replicaNumber <= totalBackupCount; ++replicaNumber) {
            int partitionId = recordStore.getPartitionId();
            Address replicaAddress = this.getReplicaAddress(partitionId, replicaNumber, totalBackupCount);
            if (!this.isReplicaAvailable(replicaAddress, totalBackupCount)) {
                this.printWarning(partitionId, replicaNumber);
                continue;
            }
            if (!this.isReplicaOnThisNode(replicaAddress)) continue;
            backupEntryMemoryCost += recordStore.getOwnedEntryCost();
            backupEntryCount += (long)recordStore.size();
        }
        if (InMemoryFormat.NATIVE != recordStore.getMapContainer().getMapConfig().getInMemoryFormat()) {
            onDemandStats.incrementHeapCost(backupEntryMemoryCost);
        }
        onDemandStats.incrementBackupEntryMemoryCost(backupEntryMemoryCost);
        onDemandStats.incrementBackupEntryCount(backupEntryCount);
        onDemandStats.setBackupCount(recordStore.getMapContainer().getMapConfig().getTotalBackupCount());
    }

    private static boolean hasRecords(RecordStore recordStore) {
        return recordStore != null && recordStore.size() > 0;
    }

    private boolean isReplicaAvailable(Address replicaAddress, int backupCount) {
        return replicaAddress != null || this.partitionService.getMaxAllowedBackupCount() < backupCount;
    }

    private boolean isReplicaOnThisNode(Address replicaAddress) {
        return replicaAddress != null && this.localAddress.equals(replicaAddress);
    }

    private void printWarning(int partitionId, int replica) {
        this.logger.warning("partitionId: " + partitionId + ", replica: " + replica + " has no owner!");
    }

    private Address getReplicaAddress(int partitionId, int replicaNumber, int backupCount) {
        IPartition partition = this.partitionService.getPartition(partitionId);
        Address replicaAddress = partition.getReplicaAddress(replicaNumber);
        if (replicaAddress == null) {
            replicaAddress = this.waitForReplicaAddress(replicaNumber, partition, backupCount);
        }
        return replicaAddress;
    }

    private Address waitForReplicaAddress(int replica, IPartition partition, int backupCount) {
        int tryCount = 3;
        Address replicaAddress = null;
        while (replicaAddress == null && this.partitionService.getMaxAllowedBackupCount() >= backupCount && tryCount-- > 0) {
            LocalMapStatsProvider.sleep();
            replicaAddress = partition.getReplicaAddress(replica);
        }
        return replicaAddress;
    }

    private static void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(100L);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw ExceptionUtil.rethrow(e);
        }
    }

    private void addNearCacheStats(String mapName, LocalMapStatsImpl localMapStats, LocalMapOnDemandCalculatedStats onDemandStats) {
        NearCache nearCache = this.mapNearCacheManager.getNearCache(mapName);
        if (nearCache == null) {
            return;
        }
        NearCacheStats nearCacheStats = nearCache.getNearCacheStats();
        localMapStats.setNearCacheStats(nearCacheStats);
        onDemandStats.incrementHeapCost(nearCacheStats.getOwnedEntryMemoryCost());
    }

    private void addIndexStats(String mapName, LocalMapStatsImpl localMapStats) {
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(mapName);
        Indexes globalIndexes = mapContainer.getIndexes();
        Map<String, OnDemandIndexStats> freshStats = null;
        if (globalIndexes != null) {
            assert (globalIndexes.isGlobal());
            localMapStats.setQueryCount(globalIndexes.getIndexesStats().getQueryCount());
            localMapStats.setIndexedQueryCount(globalIndexes.getIndexesStats().getIndexedQueryCount());
            freshStats = LocalMapStatsProvider.aggregateFreshIndexStats(globalIndexes.getIndexes(), null);
            LocalMapStatsProvider.finalizeFreshIndexStats(freshStats);
        } else {
            PartitionContainer[] partitionContainers;
            long queryCount = 0L;
            long indexedQueryCount = 0L;
            for (PartitionContainer partitionContainer : partitionContainers = this.mapServiceContext.getPartitionContainers()) {
                Indexes partitionIndexes;
                IPartition partition = this.partitionService.getPartition(partitionContainer.getPartitionId());
                if (!partition.isLocal() || (partitionIndexes = (Indexes)partitionContainer.getIndexes().get(mapName)) == null) continue;
                assert (!partitionIndexes.isGlobal());
                IndexesStats indexesStats = partitionIndexes.getIndexesStats();
                queryCount = Math.max(queryCount, indexesStats.getQueryCount());
                indexedQueryCount = Math.max(indexedQueryCount, indexesStats.getIndexedQueryCount());
                freshStats = LocalMapStatsProvider.aggregateFreshIndexStats(partitionIndexes.getIndexes(), freshStats);
            }
            localMapStats.setQueryCount(queryCount);
            localMapStats.setIndexedQueryCount(indexedQueryCount);
            LocalMapStatsProvider.finalizeFreshIndexStats(freshStats);
        }
        localMapStats.updateIndexStats(freshStats);
    }

    private static Map<String, OnDemandIndexStats> aggregateFreshIndexStats(InternalIndex[] freshIndexes, Map<String, OnDemandIndexStats> freshStats) {
        if (freshIndexes.length > 0 && freshStats == null) {
            freshStats = new HashMap<String, OnDemandIndexStats>();
        }
        for (InternalIndex index : freshIndexes) {
            String indexName = index.getName();
            OnDemandIndexStats freshIndexStats = freshStats.get(indexName);
            if (freshIndexStats == null) {
                freshIndexStats = new OnDemandIndexStats();
                freshIndexStats.setCreationTime(Long.MAX_VALUE);
                freshStats.put(indexName, freshIndexStats);
            }
            PerIndexStats indexStats = index.getPerIndexStats();
            freshIndexStats.setCreationTime(Math.min(freshIndexStats.getCreationTime(), indexStats.getCreationTime()));
            long hitCount = indexStats.getHitCount();
            freshIndexStats.setHitCount(Math.max(freshIndexStats.getHitCount(), hitCount));
            freshIndexStats.setQueryCount(Math.max(freshIndexStats.getQueryCount(), indexStats.getQueryCount()));
            freshIndexStats.setMemoryCost(freshIndexStats.getMemoryCost() + indexStats.getMemoryCost());
            freshIndexStats.setAverageHitSelectivity(freshIndexStats.getAverageHitSelectivity() + indexStats.getTotalNormalizedHitCardinality());
            freshIndexStats.setAverageHitLatency(freshIndexStats.getAverageHitLatency() + indexStats.getTotalHitLatency());
            freshIndexStats.setTotalHitCount(freshIndexStats.getTotalHitCount() + hitCount);
            freshIndexStats.setInsertCount(freshIndexStats.getInsertCount() + indexStats.getInsertCount());
            freshIndexStats.setTotalInsertLatency(freshIndexStats.getTotalInsertLatency() + indexStats.getTotalInsertLatency());
            freshIndexStats.setUpdateCount(freshIndexStats.getUpdateCount() + indexStats.getUpdateCount());
            freshIndexStats.setTotalUpdateLatency(freshIndexStats.getTotalUpdateLatency() + indexStats.getTotalUpdateLatency());
            freshIndexStats.setRemoveCount(freshIndexStats.getRemoveCount() + indexStats.getRemoveCount());
            freshIndexStats.setTotalRemoveLatency(freshIndexStats.getTotalRemoveLatency() + indexStats.getTotalRemoveLatency());
        }
        return freshStats;
    }

    private static void finalizeFreshIndexStats(Map<String, OnDemandIndexStats> freshStats) {
        if (freshStats == null) {
            return;
        }
        for (OnDemandIndexStats freshIndexStats : freshStats.values()) {
            long totalHitCount = freshIndexStats.getTotalHitCount();
            if (totalHitCount == 0L) continue;
            double averageHitSelectivity = 1.0 - freshIndexStats.getAverageHitSelectivity() / (double)totalHitCount;
            averageHitSelectivity = Math.max(0.0, averageHitSelectivity);
            freshIndexStats.setAverageHitSelectivity(averageHitSelectivity);
            freshIndexStats.setAverageHitLatency(freshIndexStats.getAverageHitLatency() / totalHitCount);
        }
    }

    protected static class LocalMapOnDemandCalculatedStats {
        private int backupCount;
        private long hits;
        private long ownedEntryCount;
        private long backupEntryCount;
        private long ownedEntryMemoryCost;
        private long backupEntryMemoryCost;
        private long heapCost;
        private long merkleTreesCost;
        private long lockedEntryCount;
        private long dirtyEntryCount;
        private long lastAccessTime;
        private long lastUpdateTime;

        protected LocalMapOnDemandCalculatedStats() {
        }

        public void setBackupCount(int backupCount) {
            this.backupCount = backupCount;
        }

        public void incrementHits(long hits) {
            this.hits += hits;
        }

        public void incrementOwnedEntryCount(long ownedEntryCount) {
            this.ownedEntryCount += ownedEntryCount;
        }

        public void incrementBackupEntryCount(long backupEntryCount) {
            this.backupEntryCount += backupEntryCount;
        }

        public void incrementOwnedEntryMemoryCost(long ownedEntryMemoryCost) {
            this.ownedEntryMemoryCost += ownedEntryMemoryCost;
        }

        public void incrementBackupEntryMemoryCost(long backupEntryMemoryCost) {
            this.backupEntryMemoryCost += backupEntryMemoryCost;
        }

        public void incrementLockedEntryCount(long lockedEntryCount) {
            this.lockedEntryCount += lockedEntryCount;
        }

        public void incrementDirtyEntryCount(long dirtyEntryCount) {
            this.dirtyEntryCount += dirtyEntryCount;
        }

        public void incrementHeapCost(long heapCost) {
            this.heapCost += heapCost;
        }

        public void incrementMerkleTreesCost(long merkleTreeCost) {
            this.merkleTreesCost += merkleTreeCost;
        }

        public LocalMapStatsImpl updateAndGet(LocalMapStatsImpl stats) {
            stats.setBackupCount(this.backupCount);
            stats.setHits(this.hits);
            stats.setOwnedEntryCount(this.ownedEntryCount);
            stats.setBackupEntryCount(this.backupEntryCount);
            stats.setOwnedEntryMemoryCost(this.ownedEntryMemoryCost);
            stats.setBackupEntryMemoryCost(this.backupEntryMemoryCost);
            stats.setHeapCost(this.heapCost);
            stats.setMerkleTreesCost(this.merkleTreesCost);
            stats.setLockedEntryCount(this.lockedEntryCount);
            stats.setDirtyEntryCount(this.dirtyEntryCount);
            stats.setLastAccessTime(this.lastAccessTime);
            stats.setLastUpdateTime(this.lastUpdateTime);
            return stats;
        }

        public void setLastAccessTime(long lastAccessTime) {
            if (lastAccessTime > this.lastAccessTime) {
                this.lastAccessTime = lastAccessTime;
            }
        }

        public void setLastUpdateTime(long lastUpdateTime) {
            if (lastUpdateTime > this.lastUpdateTime) {
                this.lastUpdateTime = lastUpdateTime;
            }
        }
    }
}

