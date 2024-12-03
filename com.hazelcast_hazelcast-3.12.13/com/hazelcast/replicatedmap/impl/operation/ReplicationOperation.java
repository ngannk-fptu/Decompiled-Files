/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.PartitionContainer;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.AbstractSerializableOperation;
import com.hazelcast.replicatedmap.impl.record.AbstractReplicatedRecordStore;
import com.hazelcast.replicatedmap.impl.record.RecordMigrationInfo;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ReplicationOperation
extends AbstractSerializableOperation {
    private SerializationService serializationService;
    private Map<String, Set<RecordMigrationInfo>> data;
    private Map<String, Long> versions;

    public ReplicationOperation() {
    }

    public ReplicationOperation(SerializationService serializationService, PartitionContainer container, int partitionId) {
        this.serializationService = serializationService;
        this.setPartitionId(partitionId);
        this.fetchReplicatedMapRecords(container);
    }

    @Override
    public void run() throws Exception {
        ILogger logger = this.getLogger();
        if (logger.isFineEnabled()) {
            logger.fine("Moving replicated map (partitionId " + this.getPartitionId() + ") from " + this.getCallerAddress() + " to the new owner " + this.getNodeEngine().getThisAddress());
        }
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        if (this.data == null) {
            return;
        }
        this.fillRecordStoreWithRecords(service);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    private void fetchReplicatedMapRecords(PartitionContainer container) {
        int storeCount = container.getStores().size();
        this.data = MapUtil.createHashMap(storeCount);
        this.versions = MapUtil.createHashMap(storeCount);
        for (Map.Entry entry : container.getStores().entrySet()) {
            String name = (String)entry.getKey();
            ReplicatedRecordStore store = (ReplicatedRecordStore)entry.getValue();
            Set<RecordMigrationInfo> recordSet = SetUtil.createHashSet(store.size());
            Iterator<ReplicatedRecord> iterator = store.recordIterator();
            while (iterator.hasNext()) {
                ReplicatedRecord record = iterator.next();
                Object dataKey = this.serializationService.toData(record.getKeyInternal());
                Object dataValue = this.serializationService.toData(record.getValueInternal());
                RecordMigrationInfo migrationInfo = new RecordMigrationInfo();
                migrationInfo.setKey((Data)dataKey);
                migrationInfo.setValue((Data)dataValue);
                migrationInfo.setTtl(record.getTtlMillis());
                migrationInfo.setHits(record.getHits());
                migrationInfo.setCreationTime(record.getCreationTime());
                migrationInfo.setLastAccessTime(record.getLastAccessTime());
                migrationInfo.setLastUpdateTime(record.getUpdateTime());
                recordSet.add(migrationInfo);
            }
            this.data.put(name, recordSet);
            this.versions.put(name, store.getVersion());
        }
    }

    private void fillRecordStoreWithRecords(ReplicatedMapService service) {
        for (Map.Entry<String, Set<RecordMigrationInfo>> dataEntry : this.data.entrySet()) {
            Set<RecordMigrationInfo> recordSet = dataEntry.getValue();
            String name = dataEntry.getKey();
            AbstractReplicatedRecordStore store = (AbstractReplicatedRecordStore)service.getReplicatedRecordStore(name, true, this.getPartitionId());
            long version = this.versions.get(name);
            store.putRecords(recordSet, version);
            store.setLoaded(true);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.data.size());
        for (Map.Entry<String, Set<RecordMigrationInfo>> entry : this.data.entrySet()) {
            out.writeUTF(entry.getKey());
            Set<RecordMigrationInfo> recordSet = entry.getValue();
            out.writeInt(recordSet.size());
            for (RecordMigrationInfo record : recordSet) {
                record.writeData(out);
            }
        }
        out.writeInt(this.versions.size());
        for (Map.Entry<String, Object> entry : this.versions.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeLong((Long)entry.getValue());
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.data = MapUtil.createHashMap(size);
        for (int i = 0; i < size; ++i) {
            String name = in.readUTF();
            int mapSize = in.readInt();
            Set<RecordMigrationInfo> recordSet = SetUtil.createHashSet(mapSize);
            for (int j = 0; j < mapSize; ++j) {
                RecordMigrationInfo record = new RecordMigrationInfo();
                record.readData(in);
                recordSet.add(record);
            }
            this.data.put(name, recordSet);
        }
        int versionsSize = in.readInt();
        this.versions = MapUtil.createHashMap(versionsSize);
        for (int i = 0; i < versionsSize; ++i) {
            String name = in.readUTF();
            long version = in.readLong();
            this.versions.put(name, version);
        }
    }

    public boolean isEmpty() {
        return this.data == null || this.data.isEmpty();
    }

    @Override
    public int getId() {
        return 19;
    }
}

