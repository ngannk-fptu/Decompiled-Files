/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.client;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapPortableHook;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ReplicatedMapEntries
implements Portable {
    private List<Data> keys;
    private List<Data> values;

    public ReplicatedMapEntries() {
    }

    public ReplicatedMapEntries(int initialSize) {
        this.keys = new ArrayList<Data>(initialSize);
        this.values = new ArrayList<Data>(initialSize);
    }

    public ReplicatedMapEntries(List<Map.Entry<Data, Data>> entries) {
        int initialSize = entries.size();
        this.keys = new ArrayList<Data>(initialSize);
        this.values = new ArrayList<Data>(initialSize);
        for (Map.Entry<Data, Data> entry : entries) {
            this.keys.add(entry.getKey());
            this.values.add(entry.getValue());
        }
    }

    public void add(Data key, Data value) {
        this.ensureEntriesCreated();
        this.keys.add(key);
        this.values.add(value);
    }

    public List<Map.Entry<Data, Data>> entries() {
        ArrayList<Map.Entry<Data, Data>> entries = new ArrayList<Map.Entry<Data, Data>>(this.keys.size());
        this.putAllToList(entries);
        return entries;
    }

    public Data getKey(int index) {
        return this.keys.get(index);
    }

    public Data getValue(int index) {
        return this.values.get(index);
    }

    public int size() {
        return this.keys == null ? 0 : this.keys.size();
    }

    private void putAllToList(Collection<Map.Entry<Data, Data>> targetList) {
        if (this.keys == null) {
            return;
        }
        Iterator<Data> keyIterator = this.keys.iterator();
        Iterator<Data> valueIterator = this.values.iterator();
        while (keyIterator.hasNext()) {
            targetList.add(new AbstractMap.SimpleImmutableEntry<Data, Data>(keyIterator.next(), valueIterator.next()));
        }
    }

    private void ensureEntriesCreated() {
        if (this.keys == null) {
            this.keys = new ArrayList<Data>();
            this.values = new ArrayList<Data>();
        }
    }

    @Override
    public int getFactoryId() {
        return ReplicatedMapPortableHook.F_ID;
    }

    @Override
    public int getClassId() {
        return 12;
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        int size = this.size();
        writer.writeInt("size", size);
        ObjectDataOutput out = writer.getRawDataOutput();
        for (int i = 0; i < size; ++i) {
            out.writeData(this.keys.get(i));
            out.writeData(this.values.get(i));
        }
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        int size = reader.readInt("size");
        this.keys = new ArrayList<Data>(size);
        this.values = new ArrayList<Data>(size);
        ObjectDataInput in = reader.getRawDataInput();
        for (int i = 0; i < size; ++i) {
            this.keys.add(in.readData());
            this.values.add(in.readData());
        }
    }
}

