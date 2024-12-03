/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.AddAllBackupOperation;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class AddAllOperation
extends QueueBackupAwareOperation
implements Notifier,
MutatingOperation {
    private Collection<Data> dataList;
    private Map<Long, Data> dataMap;

    public AddAllOperation() {
    }

    public AddAllOperation(String name, Collection<Data> dataList) {
        super(name);
        this.dataList = dataList;
    }

    @Override
    public void run() {
        QueueContainer queueContainer = this.getContainer();
        if (queueContainer.hasEnoughCapacity()) {
            this.dataMap = queueContainer.addAll(this.dataList);
            this.response = true;
        } else {
            this.response = false;
        }
    }

    @Override
    public void afterRun() throws Exception {
        LocalQueueStatsImpl stats = this.getQueueService().getLocalQueueStatsImpl(this.name);
        stats.incrementOtherOperations();
        if (Boolean.TRUE.equals(this.response)) {
            for (Data data : this.dataList) {
                this.publishEvent(ItemEventType.ADDED, data);
            }
        }
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public Operation getBackupOperation() {
        return new AddAllBackupOperation(this.name, this.dataMap);
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
    public int getId() {
        return 6;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.dataList.size());
        for (Data data : this.dataList) {
            out.writeData(data);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.dataList = new ArrayList<Data>(size);
        for (int i = 0; i < size; ++i) {
            this.dataList.add(in.readData());
        }
    }
}

