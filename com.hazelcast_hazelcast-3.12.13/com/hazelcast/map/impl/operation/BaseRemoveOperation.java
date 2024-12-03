/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.map.impl.operation.LockAwareOperation;
import com.hazelcast.map.impl.operation.RemoveBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public abstract class BaseRemoveOperation
extends LockAwareOperation
implements BackupAwareOperation,
MutatingOperation {
    private static final long BITMASK_TTL_DISABLE_WAN = Long.MIN_VALUE;
    protected transient Data dataOldValue;

    public BaseRemoveOperation(String name, Data dataKey, boolean disableWanReplicationEvent) {
        super(name, dataKey);
        this.disableWanReplicationEvent = disableWanReplicationEvent;
    }

    public BaseRemoveOperation(String name, Data dataKey) {
        this(name, dataKey, false);
    }

    public BaseRemoveOperation() {
    }

    @Override
    public void afterRun() {
        this.mapServiceContext.interceptAfterRemove(this.name, this.dataOldValue);
        this.mapEventPublisher.publishEvent(this.getCallerAddress(), this.name, EntryEventType.REMOVED, this.dataKey, this.dataOldValue, null);
        this.invalidateNearCache(this.dataKey);
        this.publishWanRemove(this.dataKey);
        this.evict(this.dataKey);
    }

    @Override
    public Object getResponse() {
        return this.dataOldValue;
    }

    @Override
    public Operation getBackupOperation() {
        return new RemoveBackupOperation(this.name, this.dataKey, false, this.disableWanReplicationEvent);
    }

    @Override
    public int getAsyncBackupCount() {
        return this.mapContainer.getAsyncBackupCount();
    }

    @Override
    public int getSyncBackupCount() {
        return this.mapContainer.getBackupCount();
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(null);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        if (this.disableWanReplicationEvent && out.getVersion().isEqualTo(Versions.V3_10)) {
            this.ttl ^= Long.MIN_VALUE;
        }
        super.writeInternal(out);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        if (in.getVersion().isEqualTo(Versions.V3_10)) {
            this.disableWanReplicationEvent |= (this.ttl & Long.MIN_VALUE) == 0L;
        }
    }
}

