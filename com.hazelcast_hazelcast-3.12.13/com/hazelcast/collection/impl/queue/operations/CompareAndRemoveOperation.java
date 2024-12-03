/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.CompareAndRemoveBackupOperation;
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

public class CompareAndRemoveOperation
extends QueueBackupAwareOperation
implements Notifier,
MutatingOperation {
    private Collection<Data> dataList;
    private Map<Long, Data> dataMap;
    private boolean retain;

    public CompareAndRemoveOperation() {
    }

    public CompareAndRemoveOperation(String name, Collection<Data> dataList, boolean retain) {
        super(name);
        this.dataList = dataList;
        this.retain = retain;
    }

    @Override
    public void run() {
        QueueContainer queueContainer = this.getContainer();
        this.dataMap = queueContainer.compareAndRemove(this.dataList, this.retain);
        this.response = this.dataMap.size() > 0;
    }

    @Override
    public void afterRun() throws Exception {
        LocalQueueStatsImpl stats = this.getQueueService().getLocalQueueStatsImpl(this.name);
        stats.incrementOtherOperations();
        if (this.hasListener()) {
            for (Data data : this.dataMap.values()) {
                this.publishEvent(ItemEventType.REMOVED, data);
            }
        }
    }

    @Override
    public boolean shouldBackup() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public Operation getBackupOperation() {
        return new CompareAndRemoveBackupOperation(this.name, this.dataMap.keySet());
    }

    @Override
    public boolean shouldNotify() {
        return Boolean.TRUE.equals(this.response);
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return this.getContainer().getOfferWaitNotifyKey();
    }

    @Override
    public int getId() {
        return 10;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.retain);
        out.writeInt(this.dataList.size());
        for (Data data : this.dataList) {
            out.writeData(data);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.retain = in.readBoolean();
        int size = in.readInt();
        this.dataList = new ArrayList<Data>(size);
        for (int i = 0; i < size; ++i) {
            this.dataList.add(in.readData());
        }
    }
}

