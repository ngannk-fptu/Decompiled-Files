/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.config.MapConfig;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.operation.MapReplicationOperation;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.record.RecordReplicationInfo;
import com.hazelcast.map.impl.record.Records;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.IndexInfo;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.MapIndexInfo;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.ThreadUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapReplicationStateHolder
implements IdentifiedDataSerializable,
Versioned {
    protected transient Map<String, RecordStore<Record>> storesByMapName;
    protected transient Map<String, Collection<RecordReplicationInfo>> data;
    protected transient Map<String, Boolean> loaded;
    protected transient List<MapIndexInfo> mapIndexInfos;
    private MapReplicationOperation operation;

    public MapReplicationStateHolder() {
    }

    public MapReplicationStateHolder(MapReplicationOperation operation) {
        this.operation = operation;
    }

    void prepare(PartitionContainer container, Collection<ServiceNamespace> namespaces, int replicaIndex) {
        this.storesByMapName = MapUtil.createHashMap(namespaces.size());
        this.data = MapUtil.createHashMap(namespaces.size());
        this.loaded = MapUtil.createHashMap(namespaces.size());
        this.mapIndexInfos = new ArrayList<MapIndexInfo>(namespaces.size());
        for (ServiceNamespace namespace : namespaces) {
            Indexes indexes;
            MapContainer mapContainer;
            MapConfig mapConfig;
            ObjectNamespace mapNamespace = (ObjectNamespace)namespace;
            String mapName = mapNamespace.getObjectName();
            RecordStore recordStore = container.getExistingRecordStore(mapName);
            if (recordStore == null || (mapConfig = (mapContainer = recordStore.getMapContainer()).getMapConfig()).getTotalBackupCount() < replicaIndex) continue;
            this.loaded.put(mapName, recordStore.isLoaded());
            this.storesByMapName.put(mapName, recordStore);
            HashSet<IndexInfo> indexInfos = new HashSet<IndexInfo>();
            if (mapContainer.isGlobalIndexEnabled()) {
                indexes = mapContainer.getIndexes();
                for (InternalIndex index : indexes.getIndexes()) {
                    indexInfos.add(new IndexInfo(index.getName(), index.isOrdered()));
                }
                indexInfos.addAll(indexes.getIndexDefinitions());
            } else {
                indexes = mapContainer.getIndexes(container.getPartitionId());
                if (indexes != null && indexes.haveAtLeastOneIndexOrDefinition()) {
                    for (InternalIndex index : indexes.getIndexes()) {
                        indexInfos.add(new IndexInfo(index.getName(), index.isOrdered()));
                    }
                    indexInfos.addAll(indexes.getIndexDefinitions());
                }
            }
            MapIndexInfo mapIndexInfo = new MapIndexInfo(mapName);
            mapIndexInfo.addIndexInfos(indexInfos);
            this.mapIndexInfos.add(mapIndexInfo);
        }
    }

    void applyState() {
        ThreadUtil.assertRunningOnPartitionThread();
        this.applyIndexesState();
        if (this.data != null) {
            for (Map.Entry<String, Collection<RecordReplicationInfo>> dataEntry : this.data.entrySet()) {
                Collection<RecordReplicationInfo> recordReplicationInfos = dataEntry.getValue();
                String mapName = dataEntry.getKey();
                RecordStore recordStore = this.operation.getRecordStore(mapName);
                recordStore.reset();
                recordStore.setPreMigrationLoadedStatus(this.loaded.get(mapName));
                MapContainer mapContainer = recordStore.getMapContainer();
                PartitionContainer partitionContainer = recordStore.getMapContainer().getMapServiceContext().getPartitionContainer(this.operation.getPartitionId());
                for (Map.Entry<String, Boolean> indexDefinition : mapContainer.getIndexDefinitions().entrySet()) {
                    Indexes indexes = mapContainer.getIndexes(partitionContainer.getPartitionId());
                    indexes.addOrGetIndex(indexDefinition.getKey(), indexDefinition.getValue());
                }
                Indexes indexes = mapContainer.getIndexes(partitionContainer.getPartitionId());
                SerializationService serializationService = mapContainer.getMapServiceContext().getNodeEngine().getSerializationService();
                boolean indexesMustBePopulated = MapReplicationStateHolder.indexesMustBePopulated(indexes, this.operation);
                if (indexesMustBePopulated) {
                    indexes.clearAll();
                }
                InternalIndex[] indexesSnapshot = indexes.getIndexes();
                for (RecordReplicationInfo recordReplicationInfo : recordReplicationInfos) {
                    Object valueToIndex;
                    Data key = recordReplicationInfo.getKey();
                    Data value = recordReplicationInfo.getValue();
                    Record newRecord = recordStore.createRecord(key, value, -1L, -1L, Clock.currentTimeMillis());
                    Records.applyRecordInfo(newRecord, recordReplicationInfo);
                    recordStore.putRecord(key, newRecord);
                    if (indexesMustBePopulated && (valueToIndex = Records.getValueOrCachedValue(newRecord, serializationService)) != null) {
                        QueryableEntry queryableEntry = mapContainer.newQueryEntry(newRecord.getKey(), valueToIndex);
                        indexes.putEntry(queryableEntry, null, Index.OperationSource.SYSTEM);
                    }
                    if (recordStore.shouldEvict()) {
                        recordStore.evictEntries(key);
                        break;
                    }
                    recordStore.disposeDeferredBlocks();
                }
                if (!indexesMustBePopulated) continue;
                Indexes.markPartitionAsIndexed(partitionContainer.getPartitionId(), indexesSnapshot);
            }
        }
    }

    private void applyIndexesState() {
        if (this.mapIndexInfos != null) {
            for (MapIndexInfo mapIndexInfo : this.mapIndexInfos) {
                this.addIndexes(mapIndexInfo.getMapName(), mapIndexInfo.getIndexInfos());
            }
        }
    }

    private void addIndexes(String mapName, Collection<IndexInfo> indexInfos) {
        if (indexInfos == null) {
            return;
        }
        RecordStore recordStore = this.operation.getRecordStore(mapName);
        MapContainer mapContainer = recordStore.getMapContainer();
        if (mapContainer.isGlobalIndexEnabled()) {
            for (IndexInfo indexInfo : indexInfos) {
                Indexes indexes = mapContainer.getIndexes();
                if (indexes.getIndex(indexInfo.getName()) != null) continue;
                indexes.addOrGetIndex(indexInfo.getName(), indexInfo.isOrdered());
            }
        } else {
            Indexes indexes = mapContainer.getIndexes(this.operation.getPartitionId());
            indexes.createIndexesFromRecordedDefinitions();
            for (IndexInfo indexInfo : indexInfos) {
                indexes.addOrGetIndex(indexInfo.getName(), indexInfo.isOrdered());
            }
        }
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.storesByMapName.size());
        for (Map.Entry<String, RecordStore<Record>> entry : this.storesByMapName.entrySet()) {
            String mapName = entry.getKey();
            RecordStore<Record> recordStore = entry.getValue();
            SerializationService ss = MapReplicationStateHolder.getSerializationService(recordStore);
            out.writeUTF(mapName);
            out.writeInt(recordStore.size());
            Iterator<Record> iterator = recordStore.iterator();
            while (iterator.hasNext()) {
                Record record = iterator.next();
                RecordReplicationInfo replicationInfo = this.operation.toReplicationInfo(record, ss);
                out.writeObject(replicationInfo);
            }
        }
        out.writeInt(this.loaded.size());
        for (Map.Entry<String, Object> entry : this.loaded.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeBoolean((Boolean)entry.getValue());
        }
        out.writeInt(this.mapIndexInfos.size());
        for (MapIndexInfo mapIndexInfo : this.mapIndexInfos) {
            out.writeObject(mapIndexInfo);
        }
    }

    private static SerializationService getSerializationService(RecordStore recordStore) {
        return recordStore.getMapContainer().getMapServiceContext().getNodeEngine().getSerializationService();
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.data = MapUtil.createHashMap(size);
        for (int i = 0; i < size; ++i) {
            String name = in.readUTF();
            int recordStoreSize = in.readInt();
            ArrayList<RecordReplicationInfo> recordReplicationInfos = new ArrayList<RecordReplicationInfo>(recordStoreSize);
            for (int j = 0; j < recordStoreSize; ++j) {
                RecordReplicationInfo recordReplicationInfo = (RecordReplicationInfo)in.readObject();
                recordReplicationInfos.add(recordReplicationInfo);
            }
            this.data.put(name, recordReplicationInfos);
        }
        int loadedSize = in.readInt();
        this.loaded = MapUtil.createHashMap(loadedSize);
        for (int i = 0; i < loadedSize; ++i) {
            this.loaded.put(in.readUTF(), in.readBoolean());
        }
        int mapIndexInfosSize = in.readInt();
        this.mapIndexInfos = new ArrayList<MapIndexInfo>(mapIndexInfosSize);
        for (int i = 0; i < mapIndexInfosSize; ++i) {
            MapIndexInfo mapIndexInfo = (MapIndexInfo)in.readObject();
            this.mapIndexInfos.add(mapIndexInfo);
        }
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 113;
    }

    private static boolean indexesMustBePopulated(Indexes indexes, MapReplicationOperation operation) {
        if (!indexes.haveAtLeastOneIndex()) {
            return false;
        }
        if (indexes.isGlobal()) {
            return false;
        }
        return operation.getReplicaIndex() == 0;
    }
}

