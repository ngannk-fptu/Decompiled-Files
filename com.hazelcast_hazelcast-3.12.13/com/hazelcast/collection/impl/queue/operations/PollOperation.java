/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.QueueDataSerializerHook;
import com.hazelcast.collection.impl.queue.QueueItem;
import com.hazelcast.collection.impl.queue.operations.PollBackupOperation;
import com.hazelcast.collection.impl.queue.operations.QueueBackupAwareOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;

public final class PollOperation
extends QueueBackupAwareOperation
implements BlockingOperation,
Notifier,
IdentifiedDataSerializable,
MutatingOperation {
    private QueueItem item;

    public PollOperation() {
    }

    public PollOperation(String name, long timeoutMillis) {
        super(name, timeoutMillis);
    }

    @Override
    public void run() {
        QueueContainer queueContainer = this.getContainer();
        this.item = queueContainer.poll();
        if (this.item != null) {
            this.response = this.item.getData();
        }
    }

    @Override
    public void afterRun() throws Exception {
        LocalQueueStatsImpl stats = this.getQueueService().getLocalQueueStatsImpl(this.name);
        if (this.response != null) {
            stats.incrementPolls();
            this.publishEvent(ItemEventType.REMOVED, this.item.getData());
        } else {
            stats.incrementEmptyPolls();
        }
    }

    @Override
    public boolean shouldBackup() {
        return this.response != null;
    }

    @Override
    public Operation getBackupOperation() {
        return new PollBackupOperation(this.name, this.item.getItemId());
    }

    @Override
    public boolean shouldNotify() {
        return this.response != null;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return this.getContainer().getOfferWaitNotifyKey();
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        return this.getContainer().getPollWaitNotifyKey();
    }

    @Override
    public boolean shouldWait() {
        return this.getWaitTimeout() != 0L && this.getContainer().size() == 0;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(null);
    }

    @Override
    public int getFactoryId() {
        return QueueDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }
}

