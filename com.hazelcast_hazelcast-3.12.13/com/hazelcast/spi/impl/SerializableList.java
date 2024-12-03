/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import com.hazelcast.util.UnmodifiableIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class SerializableList
implements IdentifiedDataSerializable,
Iterable<Data> {
    private List<Data> collection;

    public SerializableList() {
    }

    public SerializableList(List<Data> collection) {
        this.collection = collection;
    }

    public List<Data> getCollection() {
        return this.collection;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.collection.size());
        for (Data data : this.collection) {
            out.writeData(data);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.collection = new ArrayList<Data>(size);
        for (int i = 0; i < size; ++i) {
            this.collection.add(in.readData());
        }
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    public Iterator<Data> iterator() {
        final Iterator<Data> iterator = this.collection.iterator();
        return new UnmodifiableIterator<Data>(){

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Data next() {
                return (Data)iterator.next();
            }
        };
    }

    public int size() {
        return this.collection.size();
    }
}

