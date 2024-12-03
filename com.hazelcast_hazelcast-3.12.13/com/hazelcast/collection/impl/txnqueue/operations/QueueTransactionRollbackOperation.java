/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnqueue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class QueueTransactionRollbackOperation
extends QueueOperation {
    private String transactionId;

    public QueueTransactionRollbackOperation() {
    }

    public QueueTransactionRollbackOperation(String name, String transactionId) {
        super(name);
        this.transactionId = transactionId;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        queueContainer.rollbackTransaction(this.transactionId);
    }

    @Override
    public int getId() {
        return 36;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.transactionId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.transactionId = in.readUTF();
    }
}

