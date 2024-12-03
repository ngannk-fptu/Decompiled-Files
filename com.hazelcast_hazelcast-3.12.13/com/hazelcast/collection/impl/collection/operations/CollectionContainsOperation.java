/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Set;

public class CollectionContainsOperation
extends CollectionOperation
implements ReadonlyOperation {
    private Set<Data> valueSet;

    public CollectionContainsOperation() {
    }

    public CollectionContainsOperation(String name, Set<Data> valueSet) {
        super(name);
        this.valueSet = valueSet;
    }

    @Override
    public void run() throws Exception {
        CollectionContainer collectionContainer = this.getOrCreateContainer();
        this.response = collectionContainer.contains(this.valueSet);
    }

    @Override
    public int getId() {
        return 14;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.valueSet.size());
        for (Data value : this.valueSet) {
            out.writeData(value);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.valueSet = SetUtil.createHashSet(size);
        for (int i = 0; i < size; ++i) {
            Data value = in.readData();
            this.valueSet.add(value);
        }
    }
}

