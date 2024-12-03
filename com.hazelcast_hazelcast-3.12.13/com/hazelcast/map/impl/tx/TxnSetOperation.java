/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.tx;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.map.impl.operation.BasePutOperation;
import com.hazelcast.map.impl.tx.MapTxnOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.transaction.TransactionException;
import java.io.IOException;

public class TxnSetOperation
extends BasePutOperation
implements MapTxnOperation,
MutatingOperation {
    private long version;
    private String ownerUuid;
    private transient boolean shouldBackup;

    public TxnSetOperation() {
    }

    public TxnSetOperation(String name, Data dataKey, Data value, long version, long ttl) {
        super(name, dataKey, value);
        this.version = version;
        this.ttl = ttl;
    }

    @Override
    public boolean shouldWait() {
        return false;
    }

    @Override
    public void innerBeforeRun() throws Exception {
        super.innerBeforeRun();
        if (!this.recordStore.canAcquireLock(this.dataKey, this.ownerUuid, this.threadId)) {
            throw new TransactionException("Cannot acquire lock UUID: " + this.ownerUuid + ", threadId: " + this.threadId);
        }
    }

    @Override
    public void run() {
        this.recordStore.unlock(this.dataKey, this.ownerUuid, this.threadId, this.getCallId());
        Object record = this.recordStore.getRecordOrNull(this.dataKey);
        if (record == null || this.version == record.getVersion()) {
            EventService eventService = this.getNodeEngine().getEventService();
            if (eventService.hasEventRegistration("hz:impl:mapService", this.getName())) {
                this.oldValue = record == null ? null : this.mapServiceContext.toData(record.getValue());
            }
            this.eventType = record == null ? EntryEventType.ADDED : EntryEventType.UPDATED;
            this.recordStore.set(this.dataKey, this.dataValue, this.ttl, this.maxIdle);
            this.shouldBackup = true;
        }
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public void setOwnerUuid(String ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    public boolean shouldNotify() {
        return true;
    }

    @Override
    protected boolean shouldUnlockKeyOnBackup() {
        return true;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
    }

    @Override
    public boolean shouldBackup() {
        return this.shouldBackup && this.recordStore.getRecord(this.dataKey) != null;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return this.getWaitKey();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.version);
        out.writeUTF(this.ownerUuid);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.version = in.readLong();
        this.ownerUuid = in.readUTF();
    }

    @Override
    public int getId() {
        return 71;
    }
}

