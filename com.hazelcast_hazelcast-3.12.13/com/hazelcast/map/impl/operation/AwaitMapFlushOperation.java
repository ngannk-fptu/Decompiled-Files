/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.mapstore.MapDataStore;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindQueue;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindStore;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;
import com.hazelcast.map.impl.operation.MapFlushWaitNotifyKey;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;

public class AwaitMapFlushOperation
extends MapOperation
implements PartitionAwareOperation,
ReadonlyOperation,
BlockingOperation {
    private long sequence;
    private transient WriteBehindStore store;

    public AwaitMapFlushOperation() {
    }

    public AwaitMapFlushOperation(String name, long sequence) {
        super(name);
        this.sequence = sequence;
    }

    @Override
    public void innerBeforeRun() throws Exception {
        super.innerBeforeRun();
        MapDataStore<Data, Object> mapDataStore = this.recordStore.getMapDataStore();
        if (!(mapDataStore instanceof WriteBehindStore)) {
            return;
        }
        this.store = (WriteBehindStore)mapDataStore;
    }

    @Override
    public void run() {
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        return new MapFlushWaitNotifyKey(this.name, this.getPartitionId(), this.sequence);
    }

    @Override
    public boolean shouldWait() {
        WriteBehindQueue<DelayedEntry> writeBehindQueue = this.store.getWriteBehindQueue();
        DelayedEntry entry = writeBehindQueue.peek();
        if (entry == null) {
            return false;
        }
        long currentSequence = entry.getSequence();
        return currentSequence <= this.sequence && (long)writeBehindQueue.size() + currentSequence - 1L >= this.sequence;
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(false);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.sequence);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.sequence = in.readLong();
    }

    @Override
    public int getId() {
        return 44;
    }
}

