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

public class TxnRollbackBackupOperation
extends KeyBasedMapOperation
implements BackupOperation {
    private String lockOwner;
    private long lockThreadId;

    protected TxnRollbackBackupOperation(String name, Data dataKey, String lockOwner, long lockThreadId) {
        super(name, dataKey);
        this.lockOwner = lockOwner;
        this.lockThreadId = lockThreadId;
    }

    public TxnRollbackBackupOperation() {
    }

    @Override
    public void run() throws Exception {
        if (this.recordStore.isLocked(this.getKey()) && !this.recordStore.unlock(this.getKey(), this.lockOwner, this.lockThreadId, this.getCallId())) {
            throw new TransactionException("Lock is not owned by the transaction! Owner: " + this.recordStore.getLockOwnerInfo(this.getKey()));
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
        return 70;
    }
}

