/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.collection.InflatableSet;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class MerkleTreeNodeEntries
implements IdentifiedDataSerializable {
    private int nodeOrder;
    private Set<EntryView<Data, Data>> nodeEntries = Collections.emptySet();

    public MerkleTreeNodeEntries() {
    }

    public MerkleTreeNodeEntries(int nodeOrder, Set<EntryView<Data, Data>> nodeEntries) {
        this.nodeOrder = nodeOrder;
        this.nodeEntries = nodeEntries;
    }

    public int getNodeOrder() {
        return this.nodeOrder;
    }

    public void setNodeOrder(int nodeOrder) {
        this.nodeOrder = nodeOrder;
    }

    public Set<EntryView<Data, Data>> getNodeEntries() {
        return this.nodeEntries;
    }

    public void setNodeEntries(Set<EntryView<Data, Data>> nodeEntries) {
        this.nodeEntries = nodeEntries;
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 150;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.nodeOrder);
        out.writeInt(this.nodeEntries.size());
        for (EntryView<Data, Data> entryView : this.nodeEntries) {
            out.writeObject(entryView);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.nodeOrder = in.readInt();
        int entryCount = in.readInt();
        InflatableSet.Builder entries = InflatableSet.newBuilder(entryCount);
        for (int j = 0; j < entryCount; ++j) {
            entries.add(in.readObject());
        }
        this.nodeEntries = entries.build();
    }
}

