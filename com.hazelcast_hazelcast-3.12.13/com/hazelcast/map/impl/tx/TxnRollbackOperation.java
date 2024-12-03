/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.tx;

import com.hazelcast.concurrent.lock.LockWaitNotifyKey;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.operation.KeyBasedMapOperation;
import com.hazelcast.map.impl.tx.TxnRollbackBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.transaction.TransactionException;
import java.io.IOException;

public class TxnRollbackOperation
extends KeyBasedMapOperation
implements BackupAwareOperation,
Notifier {
    private String ownerUuid;

    protected TxnRollbackOperation(int partitionId, String name, Data dataKey, String ownerUuid) {
        super(name, dataKey);
        this.setPartitionId(partitionId);
        this.ownerUuid = ownerUuid;
    }

    public TxnRollbackOperation() {
    }

    @Override
    public void run() throws Exception {
        if (this.recordStore.isLocked(this.getKey()) && !this.recordStore.unlock(this.getKey(), this.ownerUuid, this.getThreadId(), this.getCallId())) {
            throw new TransactionException("Lock is not owned by the transaction! Owner: " + this.recordStore.getLockOwnerInfo(this.getKey()));
        }
    }

    @Override
    public void logError(Throwable e) {
        if (e instanceof TransactionException) {
            ILogger logger = this.getLogger();
            if (logger.isFinestEnabled()) {
                logger.finest("failed to execute:" + this, e);
            }
            return;
        }
        super.logError(e);
    }

    @Override
    public Object getResponse() {
        return true;
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public final Operation getBackupOperation() {
        return new TxnRollbackBackupOperation(this.name, this.dataKey, this.ownerUuid, this.getThreadId());
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
    public boolean shouldNotify() {
        return true;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return new LockWaitNotifyKey(this.getServiceNamespace(), this.dataKey);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.ownerUuid);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.ownerUuid = in.readUTF();
    }

    @Override
    public int getId() {
        return 69;
    }
}

