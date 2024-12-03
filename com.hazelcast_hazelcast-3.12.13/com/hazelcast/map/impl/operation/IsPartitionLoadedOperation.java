/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class IsPartitionLoadedOperation
extends MapOperation
implements PartitionAwareOperation,
ReadonlyOperation {
    private boolean isFinished;

    public IsPartitionLoadedOperation() {
    }

    public IsPartitionLoadedOperation(String name) {
        super(name);
    }

    @Override
    public void run() {
        this.isFinished = this.recordStore.isLoaded();
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
        return 38;
    }
}

