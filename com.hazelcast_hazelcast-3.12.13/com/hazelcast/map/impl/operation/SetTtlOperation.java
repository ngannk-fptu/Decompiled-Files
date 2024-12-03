/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.LockAwareOperation;
import com.hazelcast.map.impl.operation.SetTtlBackupOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;

public class SetTtlOperation
extends LockAwareOperation
implements BackupAwareOperation,
MutatingOperation {
    private transient boolean response;

    public SetTtlOperation() {
    }

    public SetTtlOperation(String name, Data dataKey, long ttl) {
        super(name, dataKey, ttl, -1L);
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(null);
    }

    @Override
    public void run() throws Exception {
        this.response = this.recordStore.setTtl(this.dataKey, this.ttl);
    }

    @Override
    public void afterRun() throws Exception {
        Object record = this.recordStore.getRecord(this.dataKey);
        if (record != null) {
            this.publishWanUpdate(this.dataKey, record.getValue());
            this.invalidateNearCache(this.dataKey);
        }
    }

    @Override
    public int getId() {
        return 148;
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public boolean shouldBackup() {
        return this.mapContainer.getTotalBackupCount() > 0;
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
    public Operation getBackupOperation() {
        return new SetTtlBackupOperation(this.name, this.dataKey, this.ttl);
    }
}

