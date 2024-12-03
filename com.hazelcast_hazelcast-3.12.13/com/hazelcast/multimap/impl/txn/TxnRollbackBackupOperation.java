/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.txn;

import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.operations.AbstractKeyBasedMultiMapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.transaction.TransactionException;
import java.io.IOException;

public class TxnRollbackBackupOperation
extends AbstractKeyBasedMultiMapOperation
implements BackupOperation {
    private String caller;

    public TxnRollbackBackupOperation() {
    }

    public TxnRollbackBackupOperation(String name, Data dataKey, String caller, long threadId) {
        super(name, dataKey);
        this.caller = caller;
        this.threadId = threadId;
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainerWithoutAccess();
        if (container.isLocked(this.dataKey) && !container.unlock(this.dataKey, this.caller, this.threadId, this.getCallId())) {
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
        return 40;
    }
}

