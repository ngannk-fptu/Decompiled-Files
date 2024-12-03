/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.record.RecordInfo;
import com.hazelcast.map.impl.record.Records;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.PartitionAwareOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PutAllBackupOperation
extends MapOperation
implements PartitionAwareOperation,
BackupOperation,
Versioned {
    private MapEntries entries;
    private List<RecordInfo> recordInfos;

    public PutAllBackupOperation(String name, MapEntries entries, List<RecordInfo> recordInfos, boolean disableWanReplicationEvent) {
        super(name);
        this.entries = entries;
        this.recordInfos = recordInfos;
        this.disableWanReplicationEvent = disableWanReplicationEvent;
    }

    public PutAllBackupOperation() {
    }

    @Override
    public void run() {
        for (int i = 0; i < this.entries.size(); ++i) {
            Data dataKey = this.entries.getKey(i);
            Data dataValue = this.entries.getValue(i);
            Object record = this.recordStore.putBackup(dataKey, dataValue, this.getCallerProvenance());
            Records.applyRecordInfo(record, this.recordInfos.get(i));
            this.publishWanUpdate(dataKey, dataValue);
            this.evict(dataKey);
        }
    }

    @Override
    public Object getResponse() {
        return this.entries;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        this.entries.writeData(out);
        for (RecordInfo recordInfo : this.recordInfos) {
            recordInfo.writeData(out);
        }
        if (out.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            out.writeBoolean(this.disableWanReplicationEvent);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.entries = new MapEntries();
        this.entries.readData(in);
        int size = this.entries.size();
        this.recordInfos = new ArrayList<RecordInfo>(size);
        for (int i = 0; i < size; ++i) {
            RecordInfo recordInfo = new RecordInfo();
            recordInfo.readData(in);
            this.recordInfos.add(recordInfo);
        }
        if (in.getVersion().isGreaterOrEqual(Versions.V3_11)) {
            this.disableWanReplicationEvent = in.readBoolean();
        }
    }

    @Override
    public int getId() {
        return 22;
    }
}

