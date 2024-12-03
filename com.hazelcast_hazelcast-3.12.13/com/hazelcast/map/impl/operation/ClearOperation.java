/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.map.impl.operation.ClearBackupOperation;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.MutatingOperation;

public class ClearOperation
extends MapOperation
implements BackupAwareOperation,
PartitionAwareOperation,
MutatingOperation {
    private boolean shouldBackup;
    private int numberOfClearedEntries;

    public ClearOperation() {
        this(null);
    }

    public ClearOperation(String name) {
        super(name);
        this.createRecordStoreOnDemand = false;
    }

    @Override
    public void run() {
        if (this.recordStore == null) {
            return;
        }
        this.numberOfClearedEntries = this.recordStore.clear();
        this.shouldBackup = true;
    }

    @Override
    public void afterRun() throws Exception {
        super.afterRun();
        this.invalidateAllKeysInNearCaches();
        this.hintMapEvent();
    }

    private void hintMapEvent() {
        this.mapEventPublisher.hintMapEvent(this.getCallerAddress(), this.name, EntryEventType.CLEAR_ALL, this.numberOfClearedEntries, this.getPartitionId());
    }

    @Override
    public boolean shouldBackup() {
        return this.shouldBackup;
    }

    @Override
    public int getSyncBackupCount() {
        return this.mapServiceContext.getMapContainer(this.name).getBackupCount();
    }

    @Override
    public int getAsyncBackupCount() {
        return this.mapServiceContext.getMapContainer(this.name).getAsyncBackupCount();
    }

    @Override
    public Object getResponse() {
        return this.numberOfClearedEntries;
    }

    @Override
    public Operation getBackupOperation() {
        ClearBackupOperation clearBackupOperation = new ClearBackupOperation(this.name);
        clearBackupOperation.setServiceName("hz:impl:mapService");
        return clearBackupOperation;
    }

    @Override
    public int getId() {
        return 28;
    }
}

