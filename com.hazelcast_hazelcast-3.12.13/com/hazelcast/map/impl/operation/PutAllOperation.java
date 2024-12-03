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
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PutAllOperation
extends MapOperation
implements PartitionAwareOperation,
BackupAwareOperation,
MutatingOperation {
    private MapEntries mapEntries;
    private boolean hasMapListener;
    private boolean hasWanReplication;
    private boolean hasBackups;
    private boolean hasInvalidation;
    private List<RecordInfo> backupRecordInfos;
    private List<Data> invalidationKeys;

    public PutAllOperation() {
    }

    public PutAllOperation(String name, MapEntries mapEntries) {
        super(name);
        this.mapEntries = mapEntries;
    }

    @Override
    public void run() {
        this.hasMapListener = this.mapEventPublisher.hasEventListener(this.name);
        this.hasWanReplication = this.mapContainer.isWanReplicationEnabled();
        this.hasBackups = this.hasBackups();
        this.hasInvalidation = this.mapContainer.hasInvalidationListener();
        if (this.hasBackups) {
            this.backupRecordInfos = new ArrayList<RecordInfo>(this.mapEntries.size());
        }
        if (this.hasInvalidation) {
            this.invalidationKeys = new ArrayList<Data>(this.mapEntries.size());
        }
        for (int i = 0; i < this.mapEntries.size(); ++i) {
            this.put(this.mapEntries.getKey(i), this.mapEntries.getValue(i));
        }
    }

    private boolean hasBackups() {
        return this.mapContainer.getTotalBackupCount() > 0;
    }

    private void put(Data dataKey, Data dataValue) {
        Object oldValue = this.putToRecordStore(dataKey, dataValue);
        dataValue = this.getValueOrPostProcessedValue(dataKey, dataValue);
        this.mapServiceContext.interceptAfterPut(this.name, dataValue);
        if (this.hasMapListener) {
            EntryEventType eventType = oldValue == null ? EntryEventType.ADDED : EntryEventType.UPDATED;
            this.mapEventPublisher.publishEvent(this.getCallerAddress(), this.name, eventType, dataKey, oldValue, dataValue);
        }
        if (this.hasWanReplication) {
            this.publishWanUpdate(dataKey, dataValue);
        }
        if (this.hasBackups) {
            Object record = this.recordStore.getRecord(dataKey);
            RecordInfo replicationInfo = Records.buildRecordInfo(record);
            this.backupRecordInfos.add(replicationInfo);
        }
        this.evict(dataKey);
        if (this.hasInvalidation) {
            this.invalidationKeys.add(dataKey);
        }
    }

    private Object putToRecordStore(Data dataKey, Data dataValue) {
        if (this.hasMapListener) {
            return this.recordStore.put(dataKey, dataValue, -1L, -1L);
        }
        this.recordStore.set(dataKey, dataValue, -1L, -1L);
        return null;
    }

    @Override
    public void afterRun() throws Exception {
        this.invalidateNearCache(this.invalidationKeys);
        super.afterRun();
    }

    private Data getValueOrPostProcessedValue(Data dataKey, Data dataValue) {
        if (!this.isPostProcessing(this.recordStore)) {
            return dataValue;
        }
        Object record = this.recordStore.getRecord(dataKey);
        return this.mapServiceContext.toData(record.getValue());
    }

    @Override
    public Object getResponse() {
        return true;
    }

    @Override
    public boolean shouldBackup() {
        return this.hasBackups && !this.mapEntries.isEmpty();
    }

    @Override
    public final int getAsyncBackupCount() {
        return this.mapContainer.getAsyncBackupCount();
    }

    @Override
    public final int getSyncBackupCount() {
        return this.mapContainer.getBackupCount();
    }

    @Override
    public Operation getBackupOperation() {
        return new PutAllBackupOperation(this.name, this.mapEntries, this.backupRecordInfos, false);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        this.mapEntries.writeData(out);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.mapEntries = new MapEntries();
        this.mapEntries.readData(in);
    }

    @Override
    public int getId() {
        return 21;
    }
}

