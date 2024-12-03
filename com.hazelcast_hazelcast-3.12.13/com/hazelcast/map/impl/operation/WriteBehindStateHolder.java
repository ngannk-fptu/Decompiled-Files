/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.config.MapConfig;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindQueue;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindStore;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntries;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;
import com.hazelcast.map.impl.operation.MapReplicationOperation;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class WriteBehindStateHolder
implements IdentifiedDataSerializable {
    private MapReplicationOperation mapReplicationOperation;
    private Map<String, List<DelayedEntry>> delayedEntries;
    private Map<String, Queue<WriteBehindStore.Sequence>> flushSequences;

    public WriteBehindStateHolder() {
    }

    public WriteBehindStateHolder(MapReplicationOperation mapReplicationOperation) {
        this.mapReplicationOperation = mapReplicationOperation;
    }

    void prepare(PartitionContainer container, Collection<ServiceNamespace> namespaces, int replicaIndex) {
        int size = namespaces.size();
        this.flushSequences = MapUtil.createHashMap(size);
        this.delayedEntries = MapUtil.createHashMap(size);
        for (ServiceNamespace namespace : namespaces) {
            WriteBehindStore mapDataStore;
            WriteBehindQueue<DelayedEntry> writeBehindQueue;
            List<DelayedEntry> entries;
            MapContainer mapContainer;
            MapConfig mapConfig;
            ObjectNamespace mapNamespace = (ObjectNamespace)namespace;
            String mapName = mapNamespace.getObjectName();
            RecordStore recordStore = container.getRecordStore(mapName);
            if (recordStore == null || (mapConfig = (mapContainer = recordStore.getMapContainer()).getMapConfig()).getTotalBackupCount() < replicaIndex || !mapContainer.getMapStoreContext().isWriteBehindMapStoreEnabled() || (entries = (writeBehindQueue = (mapDataStore = (WriteBehindStore)recordStore.getMapDataStore()).getWriteBehindQueue()).asList()) == null || entries.isEmpty()) continue;
            this.delayedEntries.put(mapName, entries);
            this.flushSequences.put(mapName, new ArrayDeque<WriteBehindStore.Sequence>(mapDataStore.getFlushSequences()));
        }
    }

    void applyState() {
        for (Map.Entry<String, List<DelayedEntry>> entry : this.delayedEntries.entrySet()) {
            String mapName = entry.getKey();
            RecordStore recordStore = this.mapReplicationOperation.getRecordStore(mapName);
            WriteBehindStore mapDataStore = (WriteBehindStore)recordStore.getMapDataStore();
            mapDataStore.reset();
            mapDataStore.setFlushSequences(this.flushSequences.get(mapName));
            Collection replicatedEntries = entry.getValue();
            for (DelayedEntry delayedEntry : replicatedEntries) {
                mapDataStore.add(delayedEntry);
                mapDataStore.setSequence(delayedEntry.getSequence());
            }
        }
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        MapService mapService = (MapService)this.mapReplicationOperation.getService();
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        out.writeInt(this.delayedEntries.size());
        for (Map.Entry<String, List<DelayedEntry>> entry : this.delayedEntries.entrySet()) {
            out.writeUTF(entry.getKey());
            List<DelayedEntry> delayedEntryList = entry.getValue();
            out.writeInt(delayedEntryList.size());
            for (DelayedEntry e : delayedEntryList) {
                Data key = mapServiceContext.toData(e.getKey());
                Data value = mapServiceContext.toData(e.getValue());
                out.writeData(key);
                out.writeData(value);
                out.writeLong(e.getStoreTime());
                out.writeInt(e.getPartitionId());
                out.writeLong(e.getSequence());
            }
        }
        out.writeInt(this.flushSequences.size());
        for (Map.Entry<String, Collection<Object>> entry : this.flushSequences.entrySet()) {
            out.writeUTF(entry.getKey());
            Queue queue = (Queue)entry.getValue();
            out.writeInt(queue.size());
            for (WriteBehindStore.Sequence sequence : queue) {
                out.writeLong(sequence.getSequence());
                out.writeBoolean(sequence.isFullFlush());
            }
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.delayedEntries = MapUtil.createHashMap(size);
        for (int i = 0; i < size; ++i) {
            String mapName = in.readUTF();
            int listSize = in.readInt();
            ArrayList<DelayedEntry<Data, Data>> delayedEntriesList = new ArrayList<DelayedEntry<Data, Data>>(listSize);
            for (int j = 0; j < listSize; ++j) {
                Data key = in.readData();
                Data value = in.readData();
                long storeTime = in.readLong();
                int partitionId = in.readInt();
                long sequence = in.readLong();
                DelayedEntry<Data, Data> entry = DelayedEntries.createDefault(key, value, storeTime, partitionId);
                entry.setSequence(sequence);
                delayedEntriesList.add(entry);
            }
            this.delayedEntries.put(mapName, delayedEntriesList);
        }
        int expectedSize = in.readInt();
        this.flushSequences = MapUtil.createHashMap(expectedSize);
        for (int i = 0; i < expectedSize; ++i) {
            String mapName = in.readUTF();
            int setSize = in.readInt();
            ArrayDeque<WriteBehindStore.Sequence> queue = new ArrayDeque<WriteBehindStore.Sequence>(setSize);
            for (int j = 0; j < setSize; ++j) {
                queue.add(new WriteBehindStore.Sequence(in.readLong(), in.readBoolean()));
            }
            this.flushSequences.put(mapName, queue);
        }
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 114;
    }
}

