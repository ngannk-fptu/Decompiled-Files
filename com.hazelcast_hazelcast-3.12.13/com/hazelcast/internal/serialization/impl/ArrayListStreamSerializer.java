/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import java.io.IOException;
import java.util.ArrayList;

public class ArrayListStreamSerializer
implements StreamSerializer<ArrayList> {
    @Override
    public void write(ObjectDataOutput out, ArrayList arrayList) throws IOException {
        int size = arrayList == null ? -1 : arrayList.size();
        out.writeInt(size);
        for (int i = 0; i < size; ++i) {
            out.writeObject(arrayList.get(i));
        }
    }

    @Override
    public ArrayList read(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        ArrayList result = null;
        if (size > -1) {
            result = new ArrayList(size);
            for (int i = 0; i < size; ++i) {
                result.add(i, in.readObject());
            }
        }
        return result;
    }

    @Override
    public int getTypeId() {
        return -26;
    }

    @Override
    public void destroy() {
    }
}

