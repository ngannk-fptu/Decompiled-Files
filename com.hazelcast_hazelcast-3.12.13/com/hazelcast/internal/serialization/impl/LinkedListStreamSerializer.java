/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

public class LinkedListStreamSerializer
implements StreamSerializer<LinkedList> {
    @Override
    public void write(ObjectDataOutput out, LinkedList linkedList) throws IOException {
        int size = linkedList == null ? -1 : linkedList.size();
        out.writeInt(size);
        if (size > 0) {
            Iterator iterator = linkedList.iterator();
            while (iterator.hasNext()) {
                out.writeObject(iterator.next());
            }
        }
    }

    @Override
    public LinkedList read(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        LinkedList result = null;
        if (size > -1) {
            result = new LinkedList();
            for (int i = 0; i < size; ++i) {
                result.add(i, in.readObject());
            }
        }
        return result;
    }

    @Override
    public int getTypeId() {
        return -27;
    }

    @Override
    public void destroy() {
    }
}

