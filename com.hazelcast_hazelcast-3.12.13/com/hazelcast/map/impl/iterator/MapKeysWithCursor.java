/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.iterator;

import com.hazelcast.map.impl.iterator.AbstractCursor;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import java.io.IOException;
import java.util.List;

public class MapKeysWithCursor
extends AbstractCursor<Data> {
    public MapKeysWithCursor() {
    }

    public MapKeysWithCursor(List<Data> keys, int nextTableIndexToReadFrom) {
        super(keys, nextTableIndexToReadFrom);
    }

    public int getCount() {
        return this.getBatch() != null ? this.getBatch().size() : 0;
    }

    @Override
    void writeElement(ObjectDataOutput out, Data element) throws IOException {
        out.writeData(element);
    }

    @Override
    Data readElement(ObjectDataInput in) throws IOException {
        return in.readData();
    }

    @Override
    public int getId() {
        return 13;
    }
}

