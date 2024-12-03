/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.map.impl.operation.EvictAllBackupOperation;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class EvictAllOperation
extends MapOperation
implements BackupAwareOperation,
MutatingOperation,
PartitionAwareOperation {
    private boolean shouldRunOnBackup;
    private int numberOfEvictedEntries;

    public EvictAllOperation() {
        this(null);
    }

    public EvictAllOperation(String name) {
        super(name);
        this.createRecordStoreOnDemand = false;
    }

    @Override
    public void run() throws Exception {
        if (this.recordStore == null) {
            return;
        }
        this.numberOfEvictedEntries = this.recordStore.evictAll(false);
        this.shouldRunOnBackup = true;
    }

    @Override
    public void afterRun() throws Exception {
        super.afterRun();
        this.hintMapEvent();
        this.invalidateAllKeysInNearCaches();
    }

    private void hintMapEvent() {
        this.mapEventPublisher.hintMapEvent(this.getCallerAddress(), this.name, EntryEventType.EVICT_ALL, this.numberOfEvictedEntries, this.getPartitionId());
    }

    @Override
    public boolean shouldBackup() {
        return this.shouldRunOnBackup;
    }

    @Override
    public Object getResponse() {
        return this.numberOfEvictedEntries;
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
    public Operation getBackupOperation() {
        return new EvictAllBackupOperation(this.name);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.numberOfEvictedEntries);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.numberOfEvictedEntries = in.readInt();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", shouldRunOnBackup=").append(this.shouldRunOnBackup);
        sb.append(", numberOfEvictedEntries=").append(this.numberOfEvictedEntries);
    }

    @Override
    public int getId() {
        return 31;
    }
}

