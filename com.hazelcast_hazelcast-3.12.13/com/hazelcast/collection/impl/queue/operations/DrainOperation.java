/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.DrainBackupOperation;
import com.hazelcast.collection.impl.queue.operations.QueueBackupAwareOperation;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.spi.impl.SerializableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class DrainOperation
extends QueueBackupAwareOperation
implements Notifier,
MutatingOperation {
    private int maxSize;
    private Map<Long, Data> dataMap;

    public DrainOperation() {
    }

    public DrainOperation(String name, int maxSize) {
        super(name);
        this.maxSize = maxSize;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        this.dataMap = queueContainer.drain(this.maxSize);
        this.response = new SerializableList(new ArrayList<Data>(this.dataMap.values()));
    }

    @Override
    public void afterRun() throws Exception {
        LocalQueueStatsImpl stats = this.getQueueService().getLocalQueueStatsImpl(this.name);
        stats.incrementOtherOperations();
        for (Data data : this.dataMap.values()) {
            this.publishEvent(ItemEventType.REMOVED, data);
        }
    }

    @Override
    public boolean shouldBackup() {
        return this.dataMap.size() > 0;
    }

    @Override
    public Operation getBackupOperation() {
        return new DrainBackupOperation(this.name, this.dataMap.keySet());
    }

    @Override
    public boolean shouldNotify() {
        return this.dataMap.size() > 0;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return this.getContainer().getOfferWaitNotifyKey();
    }

    @Override
    public int getId() {
        return 13;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.maxSize);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.maxSize = in.readInt();
    }
}

