/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnqueue;

import com.hazelcast.collection.impl.txncollection.CollectionTransactionLogRecord;
import com.hazelcast.collection.impl.txnqueue.operations.TxnCommitOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnPrepareOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnRollbackOperation;
import com.hazelcast.spi.Operation;

public class QueueTransactionLogRecord
extends CollectionTransactionLogRecord {
    public QueueTransactionLogRecord() {
    }

    public QueueTransactionLogRecord(String transactionId, String name, int partitionId) {
        super("hz:impl:queueService", transactionId, name, partitionId);
    }

    @Override
    public Operation newPrepareOperation() {
        long[] itemIds = this.createItemIdArray();
        return new TxnPrepareOperation(this.partitionId, this.name, itemIds, this.transactionId);
    }

    @Override
    public Operation newCommitOperation() {
        return new TxnCommitOperation(this.partitionId, this.name, this.operationList);
    }

    @Override
    public Operation newRollbackOperation() {
        long[] itemIds = this.createItemIdArray();
        return new TxnRollbackOperation(this.partitionId, this.name, itemIds);
    }

    @Override
    public int getId() {
        return 44;
    }
}

