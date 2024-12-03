/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.list.operations;

import com.hazelcast.collection.impl.collection.operations.CollectionAddAllOperation;
import com.hazelcast.collection.impl.list.ListContainer;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import java.io.IOException;
import java.util.List;

public class ListAddAllOperation
extends CollectionAddAllOperation {
    private int index = -1;

    public ListAddAllOperation() {
    }

    public ListAddAllOperation(String name, int index, List<Data> valueList) {
        super(name, valueList);
        this.index = index;
    }

    @Override
    public void run() throws Exception {
        if (!this.hasEnoughCapacity(this.valueList.size())) {
            this.response = false;
            return;
        }
        ListContainer listContainer = this.getOrCreateListContainer();
        this.valueMap = listContainer.addAll(this.index, this.valueList);
        this.response = !this.valueMap.isEmpty();
    }

    @Override
    public int getId() {
        return 17;
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

