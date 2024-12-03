/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ContainsOperation
extends QueueOperation
implements ReadonlyOperation {
    private Collection<Data> dataList;

    public ContainsOperation() {
    }

    public ContainsOperation(String name, Collection<Data> dataList) {
        super(name);
        this.dataList = dataList;
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        this.response = queueContainer.contains(this.dataList);
    }

    @Override
    public void afterRun() throws Exception {
        LocalQueueStatsImpl stats = this.getQueueService().getLocalQueueStatsImpl(this.name);
        stats.incrementOtherOperations();
    }

    @Override
    public int getId() {
        return 11;
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

