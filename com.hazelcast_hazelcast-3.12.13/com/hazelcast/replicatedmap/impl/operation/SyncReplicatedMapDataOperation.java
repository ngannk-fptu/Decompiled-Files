/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.AbstractSerializableOperation;
import com.hazelcast.replicatedmap.impl.record.AbstractReplicatedRecordStore;
import com.hazelcast.replicatedmap.impl.record.InternalReplicatedMapStorage;
import com.hazelcast.replicatedmap.impl.record.RecordMigrationInfo;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class SyncReplicatedMapDataOperation<K, V>
extends AbstractSerializableOperation {
    private String name;
    private Set<RecordMigrationInfo> recordSet;
    private long version;

    public SyncReplicatedMapDataOperation() {
    }

    public SyncReplicatedMapDataOperation(String name, Set<RecordMigrationInfo> recordSet, long version) {
        this.name = name;
        this.recordSet = recordSet;
        this.version = version;
    }

    @Override
    public void run() throws Exception {
        ILogger logger = this.getLogger();
        if (logger.isFineEnabled()) {
            logger.fine("Syncing " + this.recordSet.size() + " records (version " + this.version + ") for replicated map '" + this.name + "' (partitionId " + this.getPartitionId() + ") from " + this.getCallerAddress() + " to " + this.getNodeEngine().getThisAddress());
        }
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        AbstractReplicatedRecordStore store = (AbstractReplicatedRecordStore)service.getReplicatedRecordStore(this.name, true, this.getPartitionId());
        InternalReplicatedMapStorage<Object, Object> newStorage = new InternalReplicatedMapStorage<Object, Object>();
        for (RecordMigrationInfo record : this.recordSet) {
            Object key = store.marshall(record.getKey());
            Object value = store.marshall(record.getValue());
            ReplicatedRecord<Object, Object> replicatedRecord = this.buildReplicatedRecord(key, value, record.getTtl());
            ReplicatedRecord oldRecord = store.getReplicatedRecord(key);
            if (oldRecord != null) {
                replicatedRecord.setHits(oldRecord.getHits());
            }
            newStorage.put(key, replicatedRecord);
            if (record.getTtl() <= 0L) continue;
            store.scheduleTtlEntry(record.getTtl(), key, value);
        }
        newStorage.syncVersion(this.version);
        AtomicReference storageRef = store.getStorageRef();
        storageRef.set(newStorage);
        store.setLoaded(true);
    }

    private ReplicatedRecord<K, V> buildReplicatedRecord(K key, V value, long ttlMillis) {
        return new ReplicatedRecord<K, V>(key, value, ttlMillis);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeLong(this.version);
        out.writeInt(this.recordSet.size());
        for (RecordMigrationInfo record : this.recordSet) {
            record.writeData(out);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.version = in.readLong();
        int size = in.readInt();
        this.recordSet = SetUtil.createHashSet(size);
        for (int j = 0; j < size; ++j) {
            RecordMigrationInfo record = new RecordMigrationInfo();
            record.readData(in);
            this.recordSet.add(record);
        }
    }

    @Override
    public int getId() {
        return 21;
    }
}

