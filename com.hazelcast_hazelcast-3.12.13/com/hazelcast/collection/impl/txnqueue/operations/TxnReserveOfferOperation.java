/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnqueue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueBackupAwareOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnReserveOfferBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class TxnReserveOfferOperation
extends QueueBackupAwareOperation
implements BlockingOperation,
MutatingOperation {
    private int txSize;
    private String transactionId;

    public TxnReserveOfferOperation() {
    }

    public TxnReserveOfferOperation(String name, long timeoutMillis, int txSize, String transactionId) {
        super(name, timeoutMillis);
        this.txSize = txSize;
        this.transactionId = transactionId;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        if (queueContainer.hasEnoughCapacity(this.txSize + 1)) {
            this.response = queueContainer.txnOfferReserve(this.transactionId);
        }
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        QueueContainer queueContainer = this.getContainer();
        return queueContainer.getOfferWaitNotifyKey();
    }

    @Override
    public boolean shouldWait() {
        QueueContainer queueContainer = this.getContainer();
        return this.getWaitTimeout() != 0L && !queueContainer.hasEnoughCapacity(this.txSize + 1);
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(null);
    }

    @Override
    public boolean shouldBackup() {
        return this.response != null;
    }

    @Override
    public Operation getBackupOperation() {
        return new TxnReserveOfferBackupOperation(this.name, (Long)this.response, this.transactionId);
    }

    @Override
    public int getId() {
        return 29;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.txSize);
        out.writeUTF(this.transactionId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.txSize = in.readInt();
        this.transactionId = in.readUTF();
    }
}

