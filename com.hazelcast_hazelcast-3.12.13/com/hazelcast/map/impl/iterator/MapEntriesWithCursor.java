/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.iterator;

import com.hazelcast.map.impl.iterator.AbstractCursor;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class MapEntriesWithCursor
extends AbstractCursor<Map.Entry<Data, Data>> {
    public MapEntriesWithCursor() {
    }

    public MapEntriesWithCursor(List<Map.Entry<Data, Data>> entries, int nextTableIndexToReadFrom) {
        super(entries, nextTableIndexToReadFrom);
    }

    @Override
    void writeElement(ObjectDataOutput out, Map.Entry<Data, Data> entry) throws IOException {
        out.writeData(entry.getKey());
        out.writeData(entry.getValue());
    }

    @Override
    Map.Entry<Data, Data> readElement(ObjectDataInput in) throws IOException {
        Data key = in.readData();
        Data value = in.readData();
        return new AbstractMap.SimpleEntry<Data, Data>(key, value);
    }

    @Override
    public int getId() {
        return 14;
    }
}

