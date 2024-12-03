/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnqueue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;

public class TxnPeekOperation
extends QueueOperation
implements BlockingOperation,
ReadonlyOperation {
    private long itemId;
    private String transactionId;

    public TxnPeekOperation() {
    }

    public TxnPeekOperation(String name, long timeoutMillis, long itemId, String transactionId) {
        super(name, timeoutMillis);
        this.itemId = itemId;
        this.transactionId = transactionId;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        this.response = queueContainer.txnPeek(this.itemId, this.transactionId);
    }

    @Override
    public void afterRun() throws Exception {
        if (this.response != null) {
            LocalQueueStatsImpl localQueueStatsImpl = this.getQueueService().getLocalQueueStatsImpl(this.name);
            localQueueStatsImpl.incrementOtherOperations();
        }
    }

    @Override
    public int getId() {
        return 39;
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

    @Override
    public WaitNotifyKey getWaitKey() {
        QueueContainer queueContainer = this.getContainer();
        return queueContainer.getPollWaitNotifyKey();
    }

    @Override
    public boolean shouldWait() {
        QueueContainer queueContainer = this.getContainer();
        return this.getWaitTimeout() != 0L && this.itemId == -1L && queueContainer.size() == 0;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(null);
    }
}

