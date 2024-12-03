/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.operation.CacheOperation;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.util.MapUtil;
import com.hazelcast.version.Version;
import java.io.IOException;
import java.util.Map;

public class CachePutAllBackupOperation
extends CacheOperation
implements BackupOperation {
    private Map<Data, CacheRecord> cacheRecords;

    public CachePutAllBackupOperation() {
    }

    public CachePutAllBackupOperation(String cacheNameWithPrefix, Map<Data, CacheRecord> cacheRecords) {
        super(cacheNameWithPrefix);
        this.cacheRecords = cacheRecords;
    }

    @Override
    public void run() throws Exception {
        if (this.recordStore == null) {
            return;
        }
        if (this.cacheRecords != null) {
            for (Map.Entry<Data, CacheRecord> entry : this.cacheRecords.entrySet()) {
                CacheRecord record = entry.getValue();
                this.recordStore.putRecord(entry.getKey(), record, true);
                this.publishWanUpdate(entry.getKey(), record);
            }
        }
    }

    @Override
    protected boolean requiresExplicitServiceName() {
        Version clusterVersion = this.getNodeEngine().getClusterService().getClusterVersion();
        return clusterVersion.isUnknownOrLessThan(Versions.V3_11);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.cacheRecords != null);
        if (this.cacheRecords != null) {
            out.writeInt(this.cacheRecords.size());
            for (Map.Entry<Data, CacheRecord> entry : this.cacheRecords.entrySet()) {
                Data key = entry.getKey();
                CacheRecord record = entry.getValue();
                out.writeData(key);
                out.writeObject(record);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        boolean recordNotNull = in.readBoolean();
        if (recordNotNull) {
            int size = in.readInt();
            this.cacheRecords = MapUtil.createHashMap(size);
            for (int i = 0; i < size; ++i) {
                Data key = in.readData();
                CacheRecord record = (CacheRecord)in.readObject();
                this.cacheRecords.put(key, record);
            }
        }
    }

    @Override
    public int getId() {
        return 10;
    }
}

