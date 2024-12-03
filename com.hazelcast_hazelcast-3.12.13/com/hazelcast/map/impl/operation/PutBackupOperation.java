/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.KeyBasedMapOperation;
import com.hazelcast.map.impl.record.RecordInfo;
import com.hazelcast.map.impl.record.Records;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public final class PutBackupOperation
extends KeyBasedMapOperation
implements BackupOperation {
    private boolean unlockKey;
    private RecordInfo recordInfo;
    private boolean putTransient;

    public PutBackupOperation(String name, Data dataKey, Data dataValue, RecordInfo recordInfo) {
        this(name, dataKey, dataValue, recordInfo, false, false);
    }

    public PutBackupOperation(String name, Data dataKey, Data dataValue, RecordInfo recordInfo, boolean putTransient) {
        this(name, dataKey, dataValue, recordInfo, false, putTransient);
    }

    public PutBackupOperation(String name, Data dataKey, Data dataValue, RecordInfo recordInfo, boolean unlockKey, boolean putTransient) {
        this(name, dataKey, dataValue, recordInfo, unlockKey, putTransient, false);
    }

    public PutBackupOperation(String name, Data dataKey, Data dataValue, RecordInfo recordInfo, boolean unlockKey, boolean putTransient, boolean disableWanReplicationEvent) {
        super(name, dataKey, dataValue);
        this.unlockKey = unlockKey;
        this.recordInfo = recordInfo;
        this.putTransient = putTransient;
        this.disableWanReplicationEvent = disableWanReplicationEvent;
    }

    public PutBackupOperation() {
    }

    @Override
    public void run() {
        this.ttl = this.recordInfo != null ? this.recordInfo.getTtl() : this.ttl;
        this.maxIdle = this.recordInfo != null ? this.recordInfo.getMaxIdle() : this.maxIdle;
        Object record = this.recordStore.putBackup(this.dataKey, this.dataValue, this.ttl, this.maxIdle, this.putTransient, this.getCallerProvenance());
        if (this.recordInfo != null) {
            Records.applyRecordInfo(record, this.recordInfo);
        }
        if (this.unlockKey) {
            this.recordStore.forceUnlock(this.dataKey);
        }
    }

    @Override
    public void afterRun() {
        if (this.recordInfo != null) {
            this.evict(this.dataKey);
        }
        this.publishWanUpdate(this.dataKey, this.dataValue);
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.unlockKey);
        if (this.recordInfo != null) {
            out.writeBoolean(true);
            this.recordInfo.writeData(out);
        } else {
            out.writeBoolean(false);
        }
        out.writeBoolean(this.putTransient);
        out.writeBoolean(this.disableWanReplicationEvent);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.unlockKey = in.readBoolean();
        boolean hasRecordInfo = in.readBoolean();
        if (hasRecordInfo) {
            this.recordInfo = new RecordInfo();
            this.recordInfo.readData(in);
        }
        this.putTransient = in.readBoolean();
        this.disableWanReplicationEvent = in.readBoolean();
    }
}

