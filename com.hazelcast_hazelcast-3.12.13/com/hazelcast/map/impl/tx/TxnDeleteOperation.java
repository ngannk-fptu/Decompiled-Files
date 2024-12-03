/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.tx;

import com.hazelcast.map.impl.operation.BaseRemoveOperation;
import com.hazelcast.map.impl.operation.RemoveBackupOperation;
import com.hazelcast.map.impl.tx.MapTxnOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.transaction.TransactionException;
import java.io.IOException;

public class TxnDeleteOperation
extends BaseRemoveOperation
implements MapTxnOperation {
    private long version;
    private boolean successful;
    private String ownerUuid;

    public TxnDeleteOperation() {
    }

    public TxnDeleteOperation(String name, Data dataKey, long version) {
        super(name, dataKey);
        this.version = version;
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
        this.recordStore.unlock(this.dataKey, this.ownerUuid, this.getThreadId(), this.getCallId());
        Object record = this.recordStore.getRecord(this.dataKey);
        if (record == null || this.version == record.getVersion()) {
            this.dataOldValue = this.getNodeEngine().toData(this.recordStore.remove(this.dataKey, this.getCallerProvenance()));
            this.successful = this.dataOldValue != null;
        }
    }

    @Override
    public boolean shouldWait() {
        return false;
    }

    @Override
    public void afterRun() {
        if (this.successful) {
            super.afterRun();
        }
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
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
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    public boolean shouldNotify() {
        return true;
    }

    @Override
    public Operation getBackupOperation() {
        return new RemoveBackupOperation(this.name, this.dataKey, true);
    }

    @Override
    public void setOwnerUuid(String ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    @Override
    public boolean shouldBackup() {
        return true;
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
        return 66;
    }
}

