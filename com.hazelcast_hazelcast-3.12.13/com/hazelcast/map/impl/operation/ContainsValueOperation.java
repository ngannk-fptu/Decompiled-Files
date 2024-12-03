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

public class ContainsValueOperation
extends MapOperation
implements PartitionAwareOperation,
ReadonlyOperation {
    private boolean contains;
    private Data testValue;

    public ContainsValueOperation(String name, Data testValue) {
        super(name);
        this.testValue = testValue;
    }

    public ContainsValueOperation() {
    }

    @Override
    public void run() {
        this.contains = this.recordStore.containsValue(this.testValue);
    }

    @Override
    public Object getResponse() {
        return this.contains;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.testValue);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.testValue = in.readData();
    }

    @Override
    public int getId() {
        return 45;
    }
}

