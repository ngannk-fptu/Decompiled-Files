/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class KeyLoadStatusOperation
extends MapOperation
implements PartitionAwareOperation,
MutatingOperation {
    private Throwable exception;

    public KeyLoadStatusOperation() {
    }

    public KeyLoadStatusOperation(String name, Throwable exception) {
        super(name);
        this.exception = exception;
    }

    @Override
    public void run() throws Exception {
        this.recordStore.updateLoadStatus(true, this.exception);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.exception);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.exception = (Throwable)in.readObject();
    }

    @Override
    public int getId() {
        return 17;
    }
}

