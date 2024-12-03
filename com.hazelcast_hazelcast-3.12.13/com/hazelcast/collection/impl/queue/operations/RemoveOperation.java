/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueBackupAwareOperation;
import com.hazelcast.collection.impl.queue.operations.RemoveBackupOperation;
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

public class RemoveOperation
extends QueueBackupAwareOperation
implements Notifier,
MutatingOperation {
    private Data data;
    private long itemId = -1L;

    public RemoveOperation() {
    }

    public RemoveOperation(String name, Data data) {
        super(name);
        this.data = data;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        this.itemId = queueContainer.remove(this.data);
        this.response = this.itemId != -1L;
    }

    @Override
    public void afterRun() throws Exception {
        LocalQueueStatsImpl stats = this.getQueueService().getLocalQueueStatsImpl(this.name);
        stats.incrementOtherOperations();
        if (this.itemId != -1L) {
            this.publishEvent(ItemEventType.REMOVED, this.data);
        }
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public Operation getBackupOperation() {
        return new RemoveBackupOperation(this.name, this.itemId);
    }

    @Override
    public boolean shouldNotify() {
        return this.itemId != -1L;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return this.getContainer().getOfferWaitNotifyKey();
    }

    @Override
    public int getId() {
        return 20;
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

