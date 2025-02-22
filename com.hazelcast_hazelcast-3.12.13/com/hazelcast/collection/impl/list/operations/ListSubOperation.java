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
import com.hazelcast.spi.impl.SerializableList;
import java.io.IOException;
import java.util.List;

public class ListSubOperation
extends CollectionOperation
implements ReadonlyOperation {
    private int from;
    private int to;

    public ListSubOperation() {
    }

    public ListSubOperation(String name, int from, int to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    @Override
    public void run() throws Exception {
        ListContainer listContainer = this.getOrCreateListContainer();
        List<Data> sub = listContainer.sub(this.from, this.to);
        this.response = new SerializableList(sub);
    }

    @Override
    public int getId() {
        return 18;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.from);
        out.writeInt(this.to);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.from = in.readInt();
        this.to = in.readInt();
    }
}

