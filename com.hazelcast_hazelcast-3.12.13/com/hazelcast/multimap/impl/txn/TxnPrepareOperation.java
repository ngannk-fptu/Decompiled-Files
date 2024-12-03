/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.txn;

import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.operations.AbstractBackupAwareMultiMapOperation;
import com.hazelcast.multimap.impl.txn.TxnPrepareBackupOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.Operation;
import com.hazelcast.transaction.TransactionException;
import java.util.concurrent.TimeUnit;

public class TxnPrepareOperation
extends AbstractBackupAwareMultiMapOperation
implements Versioned {
    static final long LOCK_EXTENSION_TIME_IN_MILLIS = TimeUnit.SECONDS.toMillis(10L);

    public TxnPrepareOperation() {
    }

    public TxnPrepareOperation(int partitionId, String name, Data dataKey, long threadId) {
        super(name, dataKey, threadId);
        this.setPartitionId(partitionId);
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainer();
        if (!container.extendLock(this.dataKey, this.getCallerUuid(), this.threadId, LOCK_EXTENSION_TIME_IN_MILLIS)) {
            throw new TransactionException("Lock is not owned by the transaction! -> " + container.getLockOwnerInfo(this.dataKey));
        }
        this.response = true;
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public boolean shouldWait() {
        return false;
    }

    @Override
    public Operation getBackupOperation() {
        return new TxnPrepareBackupOperation(this.name, this.dataKey, this.threadId, this.getCallerUuid());
    }

    @Override
    public int getId() {
        return 32;
    }
}

