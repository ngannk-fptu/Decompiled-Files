/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class CollectionIsEmptyOperation
extends CollectionOperation
implements ReadonlyOperation {
    public CollectionIsEmptyOperation() {
    }

    public CollectionIsEmptyOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        this.response = collectionContainer.size() == 0;
    }

    @Override
    public int getId() {
        return 38;
    }
}

