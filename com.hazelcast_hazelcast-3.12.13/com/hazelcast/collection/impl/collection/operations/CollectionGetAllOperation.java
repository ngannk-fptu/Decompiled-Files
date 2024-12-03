/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.impl.SerializableList;
import java.util.List;

public class CollectionGetAllOperation
extends CollectionOperation
implements ReadonlyOperation {
    public CollectionGetAllOperation() {
    }

    public CollectionGetAllOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        List<Data> all = collectionContainer.getAll();
        this.response = new SerializableList(all);
    }

    @Override
    public int getId() {
        return 20;
    }
}

