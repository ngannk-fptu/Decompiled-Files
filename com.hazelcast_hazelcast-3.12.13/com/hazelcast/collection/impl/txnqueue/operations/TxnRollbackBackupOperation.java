/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.collection.impl.txnqueue.operations;

import com.hazelcast.collection.impl.CollectionTxnUtil;
import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

public class TxnRollbackBackupOperation
extends QueueOperation
implements BackupOperation {
    private long[] itemIds;

    public TxnRollbackBackupOperation() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public TxnRollbackBackupOperation(String name, long[] itemIds) {
        super(name);
        this.itemIds = itemIds;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        for (long itemId : this.itemIds) {
            this.response = CollectionTxnUtil.isRemove(itemId) ? Boolean.valueOf(queueContainer.txnRollbackPoll(itemId, true)) : Boolean.valueOf(queueContainer.txnRollbackOfferBackup(-itemId));
        }
    }

    @Override
    public int getId() {
        return 33;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLongArray(this.itemIds);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.itemIds = in.readLongArray();
    }
}

