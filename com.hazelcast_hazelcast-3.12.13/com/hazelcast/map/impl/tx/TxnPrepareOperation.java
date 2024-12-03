/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.tx;

import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.operation.KeyBasedMapOperation;
import com.hazelcast.map.impl.tx.TxnPrepareBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.transaction.TransactionException;
import java.io.IOException;

public class TxnPrepareOperation
extends KeyBasedMapOperation
implements BackupAwareOperation,
MutatingOperation {
    private static final long LOCK_TTL_MILLIS = 10000L;
    private String ownerUuid;

    protected TxnPrepareOperation(int partitionId, String name, Data dataKey, String ownerUuid) {
        super(name, dataKey);
        this.setPartitionId(partitionId);
        this.ownerUuid = ownerUuid;
    }

    public TxnPrepareOperation() {
    }

    @Override
    public void run() throws Exception {
        if (!this.recordStore.extendLock(this.getKey(), this.ownerUuid, this.getThreadId(), 10000L)) {
            ILogger logger = this.getLogger();
            if (logger.isFinestEnabled()) {
                logger.finest("Locked: [" + this.recordStore.isLocked(this.getKey()) + "], key: [" + this.getKey() + ']');
            }
            throw new TransactionException("Lock is not owned by the transaction! [" + this.recordStore.getLockOwnerInfo(this.getKey()) + ']');
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
        return new TxnPrepareBackupOperation(this.name, this.dataKey, this.ownerUuid, this.getThreadId());
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
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", ownerUuid=").append(this.ownerUuid);
    }

    @Override
    public int getId() {
        return 67;
    }
}

