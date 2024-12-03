/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class CollectionSizeOperation
extends CollectionOperation
implements ReadonlyOperation {
    public CollectionSizeOperation() {
    }

    public CollectionSizeOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        this.response = collectionContainer.size();
    }

    @Override
    public int getId() {
        return 7;
    }
}

