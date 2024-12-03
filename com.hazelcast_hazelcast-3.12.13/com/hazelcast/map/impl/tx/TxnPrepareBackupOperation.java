/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.tx;

import com.hazelcast.map.impl.operation.KeyBasedMapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.transaction.TransactionException;
import java.io.IOException;

public class TxnPrepareBackupOperation
extends KeyBasedMapOperation
implements BackupOperation {
    private static final long LOCK_TTL_MILLIS = 10000L;
    private String lockOwner;
    private long lockThreadId;

    protected TxnPrepareBackupOperation(String name, Data dataKey, String lockOwner, long lockThreadId) {
        super(name, dataKey);
        this.lockOwner = lockOwner;
        this.lockThreadId = lockThreadId;
    }

    public TxnPrepareBackupOperation() {
    }

    @Override
    public void run() throws Exception {
        if (!this.recordStore.txnLock(this.getKey(), this.lockOwner, this.lockThreadId, this.getCallId(), 10000L, true)) {
            throw new TransactionException("Lock is not owned by the transaction! Caller: " + this.lockOwner + ", Owner: " + this.recordStore.getLockOwnerInfo(this.getKey()));
        }
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.lockOwner);
        out.writeLong(this.lockThreadId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.lockOwner = in.readUTF();
        this.lockThreadId = in.readLong();
    }

    @Override
    public int getId() {
        return 68;
    }
}

