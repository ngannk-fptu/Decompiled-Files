/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.list.operations;

import com.hazelcast.collection.impl.collection.operations.CollectionOperation;
import com.hazelcast.collection.impl.list.ListContainer;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;

public class ListIndexOfOperation
extends CollectionOperation
implements ReadonlyOperation {
    private boolean last;
    private Data value;

    public ListIndexOfOperation() {
    }

    public ListIndexOfOperation(String name, boolean last, Data value) {
        super(name);
        this.last = last;
        this.value = value;
    }

    @Override
    public void run() throws Exception {
        ListContainer listContainer = this.getOrCreateListContainer();
        this.response = listContainer.indexOf(this.last, this.value);
    }

    @Override
    public int getId() {
        return 13;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.last);
        out.writeData(this.value);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.last = in.readBoolean();
        this.value = in.readData();
    }
}

