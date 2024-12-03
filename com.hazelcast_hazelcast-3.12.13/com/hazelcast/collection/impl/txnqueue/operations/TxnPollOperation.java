/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnqueue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.txnqueue.operations.BaseTxnQueueOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnPollBackupOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;

public class TxnPollOperation
extends BaseTxnQueueOperation
implements Notifier,
MutatingOperation {
    private Data data;

    public TxnPollOperation() {
    }

    public TxnPollOperation(String name, long itemId) {
        super(name, itemId);
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        this.data = queueContainer.txnCommitPoll(this.getItemId());
        this.response = this.data != null;
    }

    @Override
    public void afterRun() throws Exception {
        LocalQueueStatsImpl queueStats = this.getQueueService().getLocalQueueStatsImpl(this.name);
        if (this.data == null) {
            queueStats.incrementEmptyPolls();
        } else {
            queueStats.incrementPolls();
            this.publishEvent(ItemEventType.REMOVED, this.data);
        }
    }

    @Override
    public boolean shouldNotify() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        QueueContainer container = this.getContainer();
        return container.getOfferWaitNotifyKey();
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public Operation getBackupOperation() {
        return new TxnPollBackupOperation(this.name, this.getItemId());
    }

    @Override
    public boolean isRemoveOperation() {
        return true;
    }

    @Override
    public int getId() {
        return 26;
    }
}

