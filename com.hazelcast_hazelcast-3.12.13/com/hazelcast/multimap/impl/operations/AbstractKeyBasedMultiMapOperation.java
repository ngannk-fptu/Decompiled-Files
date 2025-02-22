/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.multimap.impl.operations.AbstractMultiMapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.PartitionAwareOperation;
import java.io.IOException;

public abstract class AbstractKeyBasedMultiMapOperation
extends AbstractMultiMapOperation
implements PartitionAwareOperation {
    protected Data dataKey;
    protected long threadId;

    protected AbstractKeyBasedMultiMapOperation() {
    }

    protected AbstractKeyBasedMultiMapOperation(String name, Data dataKey) {
        super(name);
        this.dataKey = dataKey;
    }

    protected AbstractKeyBasedMultiMapOperation(String name, Data dataKey, long threadId) {
        super(name);
        this.dataKey = dataKey;
        this.threadId = threadId;
    }

    public final void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.threadId);
        out.writeData(this.dataKey);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.threadId = in.readLong();
        this.dataKey = in.readData();
    }
}

