/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class TriggerLoadIfNeededOperation
extends MapOperation
implements PartitionAwareOperation,
ReadonlyOperation {
    private Boolean isLoaded;

    public TriggerLoadIfNeededOperation() {
    }

    public TriggerLoadIfNeededOperation(String name) {
        super(name);
    }

    @Override
    public void run() {
        this.isLoaded = this.recordStore.isKeyLoadFinished();
        this.recordStore.maybeDoInitialLoad();
    }

    @Override
    public Object getResponse() {
        return this.isLoaded;
    }

    @Override
    public boolean returnsResponse() {
        return true;
    }

    @Override
    public int getId() {
        return 132;
    }
}

