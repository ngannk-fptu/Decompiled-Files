/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.PutAllBackupOperation;
import com.hazelcast.map.impl.record.RecordInfo;
import com.hazelcast.map.impl.record.Records;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MergeOperation
extends MapOperation
implements PartitionAwareOperation,
BackupAwareOperation {
    private List<SplitBrainMergeTypes.MapMergeTypes> mergingEntries;
    private SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> mergePolicy;
    private transient boolean hasMapListener;
    private transient boolean hasWanReplication;
    private transient boolean hasBackups;
    private transient boolean hasInvalidation;
    private transient MapEntries mapEntries;
    private transient List<RecordInfo> backupRecordInfos;
    private transient List<Data> invalidationKeys;
    private transient boolean hasMergedValues;

    public MergeOperation() {
    }

    MergeOperation(String name, List<SplitBrainMergeTypes.MapMergeTypes> mergingEntries, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> mergePolicy, boolean disableWanReplicationEvent) {
        super(name);
        this.mergingEntries = mergingEntries;
        this.mergePolicy = mergePolicy;
        this.disableWanReplicationEvent = disableWanReplicationEvent;
    }

    @Override
    public void run() {
        this.hasMapListener = this.mapEventPublisher.hasEventListener(this.name);
        this.hasWanReplication = this.mapContainer.isWanReplicationEnabled() && !this.disableWanReplicationEvent;
        this.hasBackups = this.mapContainer.getTotalBackupCount() > 0;
        this.hasInvalidation = this.mapContainer.hasInvalidationListener();
        if (this.hasBackups) {
            this.mapEntries = new MapEntries(this.mergingEntries.size());
            this.backupRecordInfos = new ArrayList<RecordInfo>(this.mergingEntries.size());
        }
        if (this.hasInvalidation) {
            this.invalidationKeys = new ArrayList<Data>(this.mergingEntries.size());
        }
        for (SplitBrainMergeTypes.MapMergeTypes mergingEntry : this.mergingEntries) {
            this.merge(mergingEntry);
        }
    }

    private void merge(SplitBrainMergeTypes.MapMergeTypes mergingEntry) {
        Data oldValue;
        Data dataKey = (Data)mergingEntry.getKey();
        Data data = oldValue = this.hasMapListener ? this.getValue(dataKey) : null;
        if (this.recordStore.merge(mergingEntry, this.mergePolicy, this.getCallerProvenance())) {
            this.hasMergedValues = true;
            Data dataValue = this.getValueOrPostProcessedValue(dataKey, this.getValue(dataKey));
            this.mapServiceContext.interceptAfterPut(this.name, dataValue);
            if (this.hasMapListener) {
                this.mapEventPublisher.publishEvent(this.getCallerAddress(), this.name, EntryEventType.MERGED, dataKey, oldValue, dataValue);
            }
            if (this.hasWanReplication) {
                this.publishWanUpdate(dataKey, dataValue);
            }
            if (this.hasBackups) {
                this.mapEntries.add(dataKey, dataValue);
                this.backupRecordInfos.add(Records.buildRecordInfo(this.recordStore.getRecord(dataKey)));
            }
            this.evict(dataKey);
            if (this.hasInvalidation) {
                this.invalidationKeys.add(dataKey);
            }
        }
    }

    private Data getValueOrPostProcessedValue(Data dataKey, Data dataValue) {
        if (!this.isPostProcessing(this.recordStore)) {
            return dataValue;
        }
        Object record = this.recordStore.getRecord(dataKey);
        return this.mapServiceContext.toData(record.getValue());
    }

    private Data getValue(Data dataKey) {
        Object record = this.recordStore.getRecord(dataKey);
        if (record != null) {
            return this.mapServiceContext.toData(record.getValue());
        }
        return null;
    }

    @Override
    public Object getResponse() {
        return this.hasMergedValues;
    }

    @Override
    public boolean shouldBackup() {
        return this.hasBackups && !this.backupRecordInfos.isEmpty();
    }

    @Override
    public int getSyncBackupCount() {
        return this.mapContainer.getBackupCount();
    }

    @Override
    public int getAsyncBackupCount() {
        return this.mapContainer.getAsyncBackupCount();
    }

    @Override
    public void afterRun() throws Exception {
        this.invalidateNearCache(this.invalidationKeys);
        super.afterRun();
    }

    @Override
    public Operation getBackupOperation() {
        return new PutAllBackupOperation(this.name, this.mapEntries, this.backupRecordInfos, this.disableWanReplicationEvent);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.mergingEntries.size());
        for (SplitBrainMergeTypes.MapMergeTypes mergingEntry : this.mergingEntries) {
            out.writeObject(mergingEntry);
        }
        out.writeObject(this.mergePolicy);
        out.writeBoolean(this.disableWanReplicationEvent);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.mergingEntries = new ArrayList<SplitBrainMergeTypes.MapMergeTypes>(size);
        for (int i = 0; i < size; ++i) {
            SplitBrainMergeTypes.MapMergeTypes mergingEntry = (SplitBrainMergeTypes.MapMergeTypes)in.readObject();
            this.mergingEntries.add(mergingEntry);
        }
        this.mergePolicy = (SplitBrainMergePolicy)in.readObject();
        this.disableWanReplicationEvent = in.readBoolean();
    }

    @Override
    public int getId() {
        return 147;
    }
}

