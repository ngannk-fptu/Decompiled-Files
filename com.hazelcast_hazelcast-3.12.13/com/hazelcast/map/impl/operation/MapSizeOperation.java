/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class MapSizeOperation
extends MapOperation
implements PartitionAwareOperation,
ReadonlyOperation {
    private int size;

    public MapSizeOperation() {
    }

    public MapSizeOperation(String name) {
        super(name);
    }

    @Override
    public void run() {
        this.recordStore.checkIfLoaded();
        this.size = this.recordStore.size();
    }

    @Override
    public Object getResponse() {
        return this.size;
    }

    @Override
    public int getId() {
        return 25;
    }
}

