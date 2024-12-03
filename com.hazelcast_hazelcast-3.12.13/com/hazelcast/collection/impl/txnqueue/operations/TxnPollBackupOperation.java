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

public class TxnPollBackupOperation
extends QueueOperation
implements BackupOperation {
    private long itemId;

    public TxnPollBackupOperation() {
    }

    public TxnPollBackupOperation(String name, long itemId) {
        super(name);
        this.itemId = itemId;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        queueContainer.txnCommitPollBackup(this.itemId);
        this.response = true;
    }

    @Override
    public int getId() {
        return 25;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.itemId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.itemId = in.readLong();
    }
}

