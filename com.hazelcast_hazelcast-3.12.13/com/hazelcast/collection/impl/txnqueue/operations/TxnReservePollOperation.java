/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnqueue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.QueueItem;
import com.hazelcast.collection.impl.queue.operations.QueueBackupAwareOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnReservePollBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class TxnReservePollOperation
extends QueueBackupAwareOperation
implements BlockingOperation,
MutatingOperation {
    private long reservedOfferId;
    private String transactionId;

    public TxnReservePollOperation() {
    }

    public TxnReservePollOperation(String name, long timeoutMillis, long reservedOfferId, String transactionId) {
        super(name, timeoutMillis);
        this.reservedOfferId = reservedOfferId;
        this.transactionId = transactionId;
    }

    @Override
    public void run() throws Exception {
        QueueContainer createContainer = this.getContainer();
        this.response = createContainer.txnPollReserve(this.reservedOfferId, this.transactionId);
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        QueueContainer queueContainer = this.getContainer();
        return queueContainer.getPollWaitNotifyKey();
    }

    @Override
    public boolean shouldWait() {
        QueueContainer queueContainer = this.getContainer();
        return this.getWaitTimeout() != 0L && this.reservedOfferId == -1L && queueContainer.size() == 0;
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
        QueueItem item = (QueueItem)this.response;
        long itemId = item.getItemId();
        return new TxnReservePollBackupOperation(this.name, itemId, this.transactionId);
    }

    @Override
    public int getId() {
        return 31;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.reservedOfferId);
        out.writeUTF(this.transactionId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.reservedOfferId = in.readLong();
        this.transactionId = in.readUTF();
    }
}

