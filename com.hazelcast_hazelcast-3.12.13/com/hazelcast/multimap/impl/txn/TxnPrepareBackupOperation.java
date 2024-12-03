/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.txn;

import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.operations.AbstractKeyBasedMultiMapOperation;
import com.hazelcast.multimap.impl.txn.TxnPrepareOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.transaction.TransactionException;
import java.io.IOException;

public class TxnPrepareBackupOperation
extends AbstractKeyBasedMultiMapOperation
implements BackupOperation,
Versioned {
    private String caller;

    public TxnPrepareBackupOperation() {
    }

    public TxnPrepareBackupOperation(String name, Data dataKey, long threadId, String caller) {
        super(name, dataKey, threadId);
        this.caller = caller;
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainerWithoutAccess();
        if (!container.txnLock(this.dataKey, this.caller, this.threadId, this.getCallId(), TxnPrepareOperation.LOCK_EXTENSION_TIME_IN_MILLIS, true)) {
            throw new TransactionException("Lock is not owned by the transaction! -> " + container.getLockOwnerInfo(this.dataKey));
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.caller);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.caller = in.readUTF();
    }

    @Override
    public int getId() {
        return 31;
    }
}

