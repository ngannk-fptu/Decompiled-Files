/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.tx;

import com.hazelcast.map.impl.operation.LockAwareOperation;
import com.hazelcast.map.impl.tx.MapTxnOperation;
import com.hazelcast.map.impl.tx.TxnUnlockBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.transaction.TransactionException;
import java.io.IOException;

public class TxnUnlockOperation
extends LockAwareOperation
implements MapTxnOperation,
BackupAwareOperation,
MutatingOperation {
    private long version;
    private String ownerUuid;

    public TxnUnlockOperation() {
    }

    public TxnUnlockOperation(String name, Data dataKey, long version) {
        super(name, dataKey, -1L, -1L);
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
        this.recordStore.unlock(this.dataKey, this.ownerUuid, this.threadId, this.getCallId());
    }

    @Override
    public boolean shouldWait() {
        return false;
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
        TxnUnlockBackupOperation txnUnlockOperation = new TxnUnlockBackupOperation(this.name, this.dataKey, this.ownerUuid);
        txnUnlockOperation.setThreadId(this.getThreadId());
        return txnUnlockOperation;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
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
        return 72;
    }
}

