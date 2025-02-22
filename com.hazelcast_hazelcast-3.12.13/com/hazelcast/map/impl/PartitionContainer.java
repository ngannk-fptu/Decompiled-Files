/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.hazelcast.map.impl;

import com.hazelcast.concurrent.lock.LockService;
import com.hazelcast.config.MapConfig;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapKeyLoader;
import com.hazelcast.map.impl.MapKeyLoaderUtil;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nullable;

public class PartitionContainer {
    private final int partitionId;
    private final MapService mapService;
    private final ContextMutexFactory contextMutexFactory = new ContextMutexFactory();
    private final ConcurrentMap<String, RecordStore> maps = new ConcurrentHashMap<String, RecordStore>(1000);
    private final ConcurrentMap<String, Indexes> indexes = new ConcurrentHashMap<String, Indexes>(10);
    private final ConstructorFunction<String, RecordStore> recordStoreConstructor = new ConstructorFunction<String, RecordStore>(){

        @Override
        public RecordStore createNew(String name) {
            RecordStore recordStore = PartitionContainer.this.createRecordStore(name);
            recordStore.startLoading();
            return recordStore;
        }
    };
    private final ConstructorFunction<String, RecordStore> recordStoreConstructorSkipLoading = new ConstructorFunction<String, RecordStore>(){

        @Override
        public RecordStore createNew(String name) {
            return PartitionContainer.this.createRecordStore(name);
        }
    };
    private final ConstructorFunction<String, RecordStore> recordStoreConstructorForHotRestart = new ConstructorFunction<String, RecordStore>(){

        @Override
        public RecordStore createNew(String name) {
            return PartitionContainer.this.createRecordStore(name);
        }
    };
    private volatile boolean hasRunningCleanup;
    private volatile long lastCleanupTime;
    private long lastCleanupTimeCopy;

    public PartitionContainer(MapService mapService, int partitionId) {
        this.mapService = mapService;
        this.partitionId = partitionId;
    }

    private RecordStore createRecordStore(String name) {
        MapServiceContext serviceContext = this.mapService.getMapServiceContext();
        MapContainer mapContainer = serviceContext.getMapContainer(name);
        MapConfig mapConfig = mapContainer.getMapConfig();
        NodeEngine nodeEngine = serviceContext.getNodeEngine();
        IPartitionService ps = nodeEngine.getPartitionService();
        OperationService opService = nodeEngine.getOperationService();
        ExecutionService execService = nodeEngine.getExecutionService();
        HazelcastProperties hazelcastProperties = nodeEngine.getProperties();
        MapKeyLoader keyLoader = new MapKeyLoader(name, opService, ps, nodeEngine.getClusterService(), execService, mapContainer.toData());
        keyLoader.setMaxBatch(hazelcastProperties.getInteger(GroupProperty.MAP_LOAD_CHUNK_SIZE));
        keyLoader.setMaxSize(MapKeyLoaderUtil.getMaxSizePerNode(mapConfig.getMaxSizeConfig()));
        keyLoader.setHasBackup(mapConfig.getTotalBackupCount() > 0);
        keyLoader.setMapOperationProvider(serviceContext.getMapOperationProvider(name));
        if (!mapContainer.isGlobalIndexEnabled()) {
            Indexes indexesForMap = mapContainer.createIndexes(false);
            this.indexes.putIfAbsent(name, indexesForMap);
        }
        RecordStore recordStore = serviceContext.createRecordStore(mapContainer, this.partitionId, keyLoader);
        recordStore.init();
        return recordStore;
    }

    public ConcurrentMap<String, RecordStore> getMaps() {
        return this.maps;
    }

    public ConcurrentMap<String, Indexes> getIndexes() {
        return this.indexes;
    }

    public Collection<RecordStore> getAllRecordStores() {
        return this.maps.values();
    }

    public Collection<ServiceNamespace> getAllNamespaces(int replicaIndex) {
        HashSet<ServiceNamespace> namespaces = new HashSet<ServiceNamespace>();
        for (RecordStore recordStore : this.maps.values()) {
            MapContainer mapContainer = recordStore.getMapContainer();
            MapConfig mapConfig = mapContainer.getMapConfig();
            if (mapConfig.getTotalBackupCount() < replicaIndex) continue;
            namespaces.add(mapContainer.getObjectNamespace());
        }
        return namespaces;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public MapService getMapService() {
        return this.mapService;
    }

    public RecordStore getRecordStore(String name) {
        return ConcurrencyUtil.getOrPutSynchronized(this.maps, name, this.contextMutexFactory, this.recordStoreConstructor);
    }

    public RecordStore getRecordStore(String name, boolean skipLoadingOnCreate) {
        return ConcurrencyUtil.getOrPutSynchronized(this.maps, name, this, skipLoadingOnCreate ? this.recordStoreConstructorSkipLoading : this.recordStoreConstructor);
    }

    public RecordStore getRecordStoreForHotRestart(String name) {
        return ConcurrencyUtil.getOrPutSynchronized(this.maps, name, this.contextMutexFactory, this.recordStoreConstructorForHotRestart);
    }

    @Nullable
    public RecordStore getExistingRecordStore(String mapName) {
        return (RecordStore)this.maps.get(mapName);
    }

    public void destroyMap(MapContainer mapContainer) {
        String name = mapContainer.getName();
        RecordStore recordStore = (RecordStore)this.maps.remove(name);
        if (recordStore != null) {
            recordStore.destroy();
        } else {
            this.clearLockStore(name);
        }
        this.indexes.remove(name);
        MapServiceContext mapServiceContext = this.mapService.getMapServiceContext();
        if (mapServiceContext.removeMapContainer(mapContainer)) {
            mapContainer.onDestroy();
        }
        mapServiceContext.removePartitioningStrategyFromCache(mapContainer.getName());
    }

    private void clearLockStore(String name) {
        NodeEngine nodeEngine = this.mapService.getMapServiceContext().getNodeEngine();
        LockService lockService = (LockService)nodeEngine.getSharedService("hz:impl:lockService");
        if (lockService != null) {
            ObjectNamespace namespace = MapService.getObjectNamespace(name);
            lockService.clearLockStore(this.partitionId, namespace);
        }
    }

    public boolean hasRunningCleanup() {
        return this.hasRunningCleanup;
    }

    public void setHasRunningCleanup(boolean hasRunningCleanup) {
        this.hasRunningCleanup = hasRunningCleanup;
    }

    public long getLastCleanupTime() {
        return this.lastCleanupTime;
    }

    public void setLastCleanupTime(long lastCleanupTime) {
        this.lastCleanupTime = lastCleanupTime;
    }

    public long getLastCleanupTimeCopy() {
        return this.lastCleanupTimeCopy;
    }

    public void setLastCleanupTimeCopy(long lastCleanupTimeCopy) {
        this.lastCleanupTimeCopy = lastCleanupTimeCopy;
    }

    Indexes getIndexes(String name) {
        Indexes ixs = (Indexes)this.indexes.get(name);
        if (ixs == null) {
            MapServiceContext mapServiceContext = this.mapService.getMapServiceContext();
            MapContainer mapContainer = mapServiceContext.getMapContainer(name);
            if (mapContainer.isGlobalIndexEnabled()) {
                throw new IllegalStateException("Can't use a partitioned-index in the context of a global-index.");
            }
            Indexes indexesForMap = mapContainer.createIndexes(false);
            ixs = this.indexes.putIfAbsent(name, indexesForMap);
            if (ixs == null) {
                ixs = indexesForMap;
            }
        }
        return ixs;
    }
}

