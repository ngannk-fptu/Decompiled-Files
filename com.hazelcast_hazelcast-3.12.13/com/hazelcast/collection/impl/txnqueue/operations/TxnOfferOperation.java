/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnqueue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.txnqueue.operations.BaseTxnQueueOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnOfferBackupOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class TxnOfferOperation
extends BaseTxnQueueOperation
implements Notifier,
MutatingOperation {
    private Data data;

    public TxnOfferOperation() {
    }

    public TxnOfferOperation(String name, long itemId, Data data) {
        super(name, itemId);
        this.data = data;
    }

    @Override
    public void run() throws Exception {
        QueueContainer createContainer = this.getContainer();
        this.response = createContainer.txnCommitOffer(this.getItemId(), this.data, false);
    }

    @Override
    public void afterRun() throws Exception {
        LocalQueueStatsImpl queueStats = this.getQueueService().getLocalQueueStatsImpl(this.name);
        if (Boolean.TRUE.equals(this.response)) {
            queueStats.incrementOffers();
            this.publishEvent(ItemEventType.ADDED, this.data);
        } else {
            queueStats.incrementRejectedOffers();
        }
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public Operation getBackupOperation() {
        return new TxnOfferBackupOperation(this.name, this.getItemId(), this.data);
    }

    @Override
    public boolean shouldNotify() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return this.getContainer().getPollWaitNotifyKey();
    }

    @Override
    public boolean isRemoveOperation() {
        return false;
    }

    @Override
    public int getId() {
        return 24;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.data);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.data = in.readData();
    }
}

