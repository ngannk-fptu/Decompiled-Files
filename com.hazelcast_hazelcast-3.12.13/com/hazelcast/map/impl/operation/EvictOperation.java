/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.operation.EvictBackupOperation;
import com.hazelcast.map.impl.operation.LockAwareOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class EvictOperation
extends LockAwareOperation
implements MutatingOperation,
BackupAwareOperation {
    private boolean evicted;
    private boolean asyncBackup;

    public EvictOperation(String name, Data dataKey, boolean asyncBackup) {
        super(name, dataKey);
        this.asyncBackup = asyncBackup;
    }

    public EvictOperation() {
    }

    @Override
    public void run() {
        this.dataValue = this.mapServiceContext.toData(this.recordStore.evict(this.dataKey, false));
        this.evicted = this.dataValue != null;
    }

    @Override
    public void afterRun() {
        if (!this.evicted) {
            return;
        }
        this.mapServiceContext.interceptAfterRemove(this.name, this.dataValue);
        this.mapEventPublisher.publishEvent(this.getCallerAddress(), this.name, EntryEventType.EVICTED, this.dataKey, this.dataValue, null);
        this.invalidateNearCache(this.dataKey);
    }

    @Override
    public Object getResponse() {
        return this.evicted;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
    }

    @Override
    public Operation getBackupOperation() {
        return new EvictBackupOperation(this.name, this.dataKey);
    }

    @Override
    public int getAsyncBackupCount() {
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(this.name);
        if (this.asyncBackup) {
            return mapContainer.getTotalBackupCount();
        }
        return mapContainer.getAsyncBackupCount();
    }

    @Override
    public int getSyncBackupCount() {
        if (this.asyncBackup) {
            return 0;
        }
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(this.name);
        return mapContainer.getBackupCount();
    }

    @Override
    public boolean shouldBackup() {
        return this.evicted;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.asyncBackup);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.asyncBackup = in.readBoolean();
    }

    @Override
    public int getId() {
        return 30;
    }
}

