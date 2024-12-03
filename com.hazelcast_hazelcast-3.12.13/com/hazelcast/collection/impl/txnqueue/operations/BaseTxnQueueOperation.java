/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnqueue.operations;

import com.hazelcast.collection.impl.queue.operations.QueueBackupAwareOperation;
import com.hazelcast.collection.impl.txncollection.CollectionTxnOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Notifier;
import java.io.IOException;

public abstract class BaseTxnQueueOperation
extends QueueBackupAwareOperation
implements Notifier,
CollectionTxnOperation {
    private long itemId;

    public BaseTxnQueueOperation() {
    }

    public BaseTxnQueueOperation(String name, long itemId) {
        super(name);
        this.itemId = itemId;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.itemId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.itemId = in.readLong();
    }

    @Override
    public long getItemId() {
        return this.itemId;
    }
}

