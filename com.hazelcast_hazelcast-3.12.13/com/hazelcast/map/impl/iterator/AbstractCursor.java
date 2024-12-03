/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.iterator;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCursor<T>
implements IdentifiedDataSerializable {
    private List<T> objects;
    private int nextTableIndexToReadFrom;

    public AbstractCursor() {
    }

    public AbstractCursor(List<T> entries, int nextTableIndexToReadFrom) {
        this.objects = entries;
        this.nextTableIndexToReadFrom = nextTableIndexToReadFrom;
    }

    public List<T> getBatch() {
        return this.objects;
    }

    public int getNextTableIndexToReadFrom() {
        return this.nextTableIndexToReadFrom;
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    abstract void writeElement(ObjectDataOutput var1, T var2) throws IOException;

    abstract T readElement(ObjectDataInput var1) throws IOException;

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.nextTableIndexToReadFrom);
        int size = this.objects.size();
        out.writeInt(size);
        for (T entry : this.objects) {
            this.writeElement(out, entry);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.nextTableIndexToReadFrom = in.readInt();
        int size = in.readInt();
        this.objects = new ArrayList<T>(size);
        for (int i = 0; i < size; ++i) {
            this.objects.add(this.readElement(in));
        }
    }
}

