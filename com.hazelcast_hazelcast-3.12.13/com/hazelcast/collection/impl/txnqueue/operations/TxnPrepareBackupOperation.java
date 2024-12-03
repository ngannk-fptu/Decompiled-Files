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

public class TxnPrepareBackupOperation
extends QueueOperation
implements BackupOperation {
    private long[] itemIds;
    private String transactionId;

    public TxnPrepareBackupOperation() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public TxnPrepareBackupOperation(String name, long[] itemIds, String transactionId) {
        super(name);
        this.itemIds = itemIds;
        this.transactionId = transactionId;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        for (long itemId : this.itemIds) {
            boolean remove = CollectionTxnUtil.isRemove(itemId);
            queueContainer.txnEnsureBackupReserve(Math.abs(itemId), this.transactionId, remove);
        }
    }

    @Override
    public int getId() {
        return 27;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.transactionId);
        out.writeLongArray(this.itemIds);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.transactionId = in.readUTF();
        this.itemIds = in.readLongArray();
    }
}

