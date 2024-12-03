/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.list.operations;

import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.collection.operations.CollectionAddBackupOperation;
import com.hazelcast.collection.impl.collection.operations.CollectionAddOperation;
import com.hazelcast.collection.impl.list.ListContainer;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public class ListAddOperation
extends CollectionAddOperation {
    private int index = -1;

    public ListAddOperation() {
    }

    public ListAddOperation(String name, int index, Data value) {
        super(name, value);
        this.index = index;
    }

    @Override
    public void run() throws Exception {
        ListContainer listContainer = this.getOrCreateListContainer();
        this.response = false;
        if (!this.hasEnoughCapacity(1)) {
            return;
        }
        CollectionItem item = listContainer.add(this.index, this.value);
        if (item != null) {
            this.itemId = item.getItemId();
            this.response = true;
        }
    }

    @Override
    public Operation getBackupOperation() {
        return new CollectionAddBackupOperation(this.name, this.itemId, this.value);
    }

    @Override
    public int getId() {
        return 3;
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

