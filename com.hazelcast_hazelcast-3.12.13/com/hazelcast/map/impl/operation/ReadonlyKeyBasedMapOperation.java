/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;

public abstract class ReadonlyKeyBasedMapOperation
extends MapOperation
implements ReadonlyOperation,
PartitionAwareOperation {
    protected Data dataKey;
    protected long threadId;

    public ReadonlyKeyBasedMapOperation() {
    }

    public ReadonlyKeyBasedMapOperation(String name, Data dataKey) {
        this.name = name;
        this.dataKey = dataKey;
    }

    @Override
    public final long getThreadId() {
        return this.threadId;
    }

    @Override
    public final void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.dataKey);
        out.writeLong(this.threadId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.dataKey = in.readData();
        this.threadId = in.readLong();
    }
}

