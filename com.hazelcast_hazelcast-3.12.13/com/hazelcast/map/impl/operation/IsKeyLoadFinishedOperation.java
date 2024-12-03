/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class IsKeyLoadFinishedOperation
extends MapOperation
implements PartitionAwareOperation,
ReadonlyOperation {
    private boolean isFinished;

    public IsKeyLoadFinishedOperation() {
    }

    public IsKeyLoadFinishedOperation(String name) {
        super(name);
    }

    @Override
    public void run() {
        this.isFinished = this.recordStore.isKeyLoadFinished();
    }

    @Override
    public Object getResponse() {
        return this.isFinished;
    }

    @Override
    public boolean returnsResponse() {
        return true;
    }

    @Override
    public int getId() {
        return 133;
    }
}

