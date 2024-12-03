/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.list.operations;

import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.collection.impl.list.ListContainer;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;

public class ListGetOperation
extends CollectionOperation
implements ReadonlyOperation {
    private int index;

    public ListGetOperation() {
    }

    public ListGetOperation(String name, int index) {
        super(name);
        this.index = index;
    }

    @Override
    public void run() throws Exception {
        ListContainer listContainer = this.getOrCreateListContainer();
        CollectionItem item = listContainer.get(this.index);
        this.response = item.getValue();
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.index);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.index = in.readInt();
    }
}

