/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.EntryView;
import com.hazelcast.map.impl.operation.BasePutOperation;
import com.hazelcast.map.impl.operation.RemoveBackupOperation;
import com.hazelcast.map.merge.MapMergePolicy;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public class LegacyMergeOperation
extends BasePutOperation {
    private MapMergePolicy mergePolicy;
    private EntryView<Data, Data> mergingEntry;
    private transient boolean merged;

    public LegacyMergeOperation() {
    }

    public LegacyMergeOperation(String name, EntryView<Data, Data> mergingEntry, MapMergePolicy policy, boolean disableWanReplicationEvent) {
        super(name, mergingEntry.getKey(), null);
        this.mergingEntry = mergingEntry;
        this.mergePolicy = policy;
        this.disableWanReplicationEvent = disableWanReplicationEvent;
    }

    @Override
    public void run() {
        Object record;
        Object oldRecord = this.recordStore.getRecord(this.dataKey);
        if (oldRecord != null) {
            this.oldValue = this.mapServiceContext.toData(oldRecord.getValue());
        }
        this.merged = this.recordStore.merge(this.dataKey, this.mergingEntry, this.mergePolicy, this.getCallerProvenance());
        if (this.merged && (record = this.recordStore.getRecord(this.dataKey)) != null) {
            this.dataValue = this.mapServiceContext.toData(record.getValue());
            this.dataMergingValue = this.mapServiceContext.toData(this.mergingEntry.getValue());
        }
    }

    @Override
    public Object getResponse() {
        return this.merged;
    }

    @Override
    public boolean shouldBackup() {
        return this.merged;
    }

    @Override
    public void afterRun() {
        if (this.merged) {
            this.eventType = EntryEventType.MERGED;
            super.afterRun();
        }
    }

    @Override
    public Operation getBackupOperation() {
        if (this.dataValue == null) {
            return new RemoveBackupOperation(this.name, this.dataKey, false, this.disableWanReplicationEvent);
        }
        return super.getBackupOperation();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.mergingEntry);
        out.writeObject(this.mergePolicy);
        out.writeBoolean(this.disableWanReplicationEvent);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.mergingEntry = (EntryView)in.readObject();
        this.mergePolicy = (MapMergePolicy)in.readObject();
        this.disableWanReplicationEvent = in.readBoolean();
    }

    @Override
    public int getId() {
        return 35;
    }
}

