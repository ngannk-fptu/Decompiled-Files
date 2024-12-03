/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.collection.impl.txnqueue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueBackupAwareOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnPrepareBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

public class TxnPrepareOperation
extends QueueBackupAwareOperation {
    private long[] itemIds;
    private String transactionId;

    public TxnPrepareOperation() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public TxnPrepareOperation(int partitionId, String name, long[] itemIds, String transactionId) {
        super(name);
        this.setPartitionId(partitionId);
        this.itemIds = itemIds;
        this.transactionId = transactionId;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        for (long itemId : this.itemIds) {
            queueContainer.txnCheckReserve(Math.abs(itemId));
        }
    }

    @Override
    public boolean shouldBackup() {
        return true;
    }

    @Override
    public Operation getBackupOperation() {
        return new TxnPrepareBackupOperation(this.name, this.itemIds, this.transactionId);
    }

    @Override
    public int getId() {
        return 28;
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

