/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.txn;

import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.operations.AbstractBackupAwareMultiMapOperation;
import com.hazelcast.multimap.impl.txn.TxnRollbackBackupOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.transaction.TransactionException;

public class TxnRollbackOperation
extends AbstractBackupAwareMultiMapOperation
implements Notifier {
    public TxnRollbackOperation() {
    }

    public TxnRollbackOperation(int partitionId, String name, Data dataKey, long threadId) {
        super(name, dataKey, threadId);
        this.setPartitionId(partitionId);
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainer();
        if (container.isLocked(this.dataKey) && !container.unlock(this.dataKey, this.getCallerUuid(), this.threadId, this.getCallId())) {
            throw new TransactionException("Lock is not owned by the transaction! Owner: " + container.getLockOwnerInfo(this.dataKey));
        }
    }

    @Override
    public Operation getBackupOperation() {
        return new TxnRollbackBackupOperation(this.name, this.dataKey, this.getCallerUuid(), this.threadId);
    }

    @Override
    public boolean shouldNotify() {
        return true;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return this.getWaitKey();
    }

    @Override
    public int getId() {
        return 39;
    }
}

