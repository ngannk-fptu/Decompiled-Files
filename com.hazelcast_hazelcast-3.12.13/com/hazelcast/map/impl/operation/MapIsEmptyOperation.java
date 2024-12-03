/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class MapIsEmptyOperation
extends MapOperation
implements PartitionAwareOperation,
ReadonlyOperation {
    private boolean empty;

    public MapIsEmptyOperation(String name) {
        super(name);
    }

    public MapIsEmptyOperation() {
    }

    @Override
    public void run() {
        this.empty = this.recordStore.isEmpty();
    }

    @Override
    public Object getResponse() {
        return this.empty;
    }

    @Override
    public int getId() {
        return 34;
    }
}

