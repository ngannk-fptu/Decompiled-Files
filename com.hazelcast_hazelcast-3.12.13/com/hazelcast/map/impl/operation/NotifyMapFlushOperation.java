/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapFlushWaitNotifyKey;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;

public class NotifyMapFlushOperation
extends MapOperation
implements PartitionAwareOperation,
ReadonlyOperation,
Notifier {
    private long sequence;

    public NotifyMapFlushOperation(String name, long sequence) {
        super(name);
        this.sequence = sequence;
    }

    public NotifyMapFlushOperation() {
    }

    @Override
    public void run() throws Exception {
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return new MapFlushWaitNotifyKey(this.name, this.getPartitionId(), this.sequence);
    }

    @Override
    public boolean shouldNotify() {
        return Boolean.TRUE;
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
        return 55;
    }
}

