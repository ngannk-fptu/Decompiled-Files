/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.map.impl.operation.LockAwareOperation;
import com.hazelcast.map.impl.operation.PutBackupOperation;
import com.hazelcast.map.impl.record.RecordInfo;
import com.hazelcast.map.impl.record.Records;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;

public abstract class BasePutOperation
extends LockAwareOperation
implements BackupAwareOperation {
    protected transient Object oldValue;
    protected transient Data dataMergingValue;
    protected transient EntryEventType eventType;
    protected transient boolean putTransient;

    public BasePutOperation(String name, Data dataKey, Data value) {
        super(name, dataKey, value, -1L, -1L);
    }

    public BasePutOperation(String name, Data dataKey, Data value, long ttl, long maxIdle) {
        super(name, dataKey, value, ttl, maxIdle);
    }

    public BasePutOperation() {
    }

    @Override
    public void afterRun() {
        this.mapServiceContext.interceptAfterPut(this.name, this.dataValue);
        Object record = this.recordStore.getRecord(this.dataKey);
        Object value = this.isPostProcessing(this.recordStore) ? record.getValue() : this.dataValue;
        this.mapEventPublisher.publishEvent(this.getCallerAddress(), this.name, this.getEventType(), this.dataKey, this.oldValue, value, this.dataMergingValue);
        this.invalidateNearCache(this.dataKey);
        this.publishWanUpdate(this.dataKey, value);
        this.evict(this.dataKey);
    }

    private EntryEventType getEventType() {
        if (this.eventType == null) {
            this.eventType = this.oldValue == null ? EntryEventType.ADDED : EntryEventType.UPDATED;
        }
        return this.eventType;
    }

    @Override
    public boolean shouldBackup() {
        Object record = this.recordStore.getRecord(this.dataKey);
        return record != null;
    }

    @Override
    public Operation getBackupOperation() {
        Object record = this.recordStore.getRecord(this.dataKey);
        RecordInfo replicationInfo = Records.buildRecordInfo(record);
        if (this.isPostProcessing(this.recordStore)) {
            this.dataValue = this.mapServiceContext.toData(record.getValue());
        }
        return new PutBackupOperation(this.name, this.dataKey, this.dataValue, replicationInfo, this.shouldUnlockKeyOnBackup(), this.putTransient, !this.canThisOpGenerateWANEvent());
    }

    protected boolean shouldUnlockKeyOnBackup() {
        return false;
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
    public void onWaitExpire() {
        this.sendResponse(null);
    }
}

