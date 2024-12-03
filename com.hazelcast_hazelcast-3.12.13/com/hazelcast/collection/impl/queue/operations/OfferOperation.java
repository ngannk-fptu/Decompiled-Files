/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.QueueDataSerializerHook;
import com.hazelcast.collection.impl.queue.operations.OfferBackupOperation;
import com.hazelcast.collection.impl.queue.operations.QueueBackupAwareOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public final class OfferOperation
extends QueueBackupAwareOperation
implements BlockingOperation,
Notifier,
IdentifiedDataSerializable,
MutatingOperation {
    private Data data;
    private long itemId;

    public OfferOperation() {
    }

    public OfferOperation(String name, long timeout, Data data) {
        super(name, timeout);
        this.data = data;
    }

    @Override
    public void run() {
        QueueContainer queueContainer = this.getContainer();
        if (queueContainer.hasEnoughCapacity()) {
            this.itemId = queueContainer.offer(this.data);
            this.response = true;
        } else {
            this.response = false;
        }
    }

    @Override
    public void afterRun() throws Exception {
        LocalQueueStatsImpl stats = this.getQueueService().getLocalQueueStatsImpl(this.name);
        if (Boolean.TRUE.equals(this.response)) {
            stats.incrementOffers();
            this.publishEvent(ItemEventType.ADDED, this.data);
        } else {
            stats.incrementRejectedOffers();
        }
    }

    @Override
    public Operation getBackupOperation() {
        return new OfferBackupOperation(this.name, this.data, this.itemId);
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
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
    public WaitNotifyKey getWaitKey() {
        return this.getContainer().getOfferWaitNotifyKey();
    }

    @Override
    public boolean shouldWait() {
        QueueContainer container = this.getContainer();
        return this.getWaitTimeout() != 0L && !container.hasEnoughCapacity();
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
    }

    @Override
    public int getFactoryId() {
        return QueueDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 0;
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

