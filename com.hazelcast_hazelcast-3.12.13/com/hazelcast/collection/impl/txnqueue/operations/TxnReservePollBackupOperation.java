/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnqueue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class TxnReservePollBackupOperation
extends QueueOperation
implements BackupOperation {
    private long itemId;
    private String transactionId;

    public TxnReservePollBackupOperation() {
    }

    public TxnReservePollBackupOperation(String name, long itemId, String transactionId) {
        super(name);
        this.itemId = itemId;
        this.transactionId = transactionId;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        queueContainer.txnPollBackupReserve(this.itemId, this.transactionId);
    }

    @Override
    public int getId() {
        return 32;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.itemId);
        out.writeUTF(this.transactionId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.itemId = in.readLong();
        this.transactionId = in.readUTF();
    }
}

