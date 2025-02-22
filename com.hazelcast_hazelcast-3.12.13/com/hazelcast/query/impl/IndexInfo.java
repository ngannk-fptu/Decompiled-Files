/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class IndexInfo
implements IdentifiedDataSerializable,
Comparable<IndexInfo> {
    private String name;
    private boolean ordered;

    public IndexInfo() {
    }

    public IndexInfo(String name, boolean ordered) {
        this.name = name;
        this.ordered = ordered;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeBoolean(this.ordered);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.ordered = in.readBoolean();
    }

    public String getName() {
        return this.name;
    }

    public boolean isOrdered() {
        return this.ordered;
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 98;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IndexInfo indexInfo = (IndexInfo)o;
        if (this.ordered != indexInfo.ordered) {
            return false;
        }
        return this.name != null ? this.name.equals(indexInfo.name) : indexInfo.name == null;
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.ordered ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(IndexInfo other) {
        int attributeNameCompareResult = this.name.compareTo(other.name);
        if (attributeNameCompareResult == 0) {
            return Boolean.valueOf(this.ordered).compareTo(other.ordered);
        }
        return attributeNameCompareResult;
    }
}

