/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection;

import com.hazelcast.collection.impl.collection.CollectionDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Clock;
import java.io.IOException;

public class CollectionItem
implements Comparable<CollectionItem>,
IdentifiedDataSerializable {
    protected long itemId;
    protected Data value;
    protected final long creationTime = Clock.currentTimeMillis();

    public CollectionItem() {
    }

    public CollectionItem(long itemId, Data value) {
        this();
        this.itemId = itemId;
        this.value = value;
    }

    public long getItemId() {
        return this.itemId;
    }

    public Data getValue() {
        return this.value;
    }

    public void setValue(Data value) {
        this.value = value;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public int compareTo(CollectionItem o) {
        long otherItemId = o.getItemId();
        if (this.itemId > otherItemId) {
            return 1;
        }
        if (this.itemId < otherItemId) {
            return -1;
        }
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CollectionItem)) {
            return false;
        }
        CollectionItem item = (CollectionItem)o;
        return !(this.value != null ? !this.value.equals(item.value) : item.value != null);
    }

    public int hashCode() {
        return this.value != null ? this.value.hashCode() : 0;
    }

    @Override
    public int getFactoryId() {
        return CollectionDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 23;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.itemId);
        out.writeData(this.value);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.itemId = in.readLong();
        this.value = in.readData();
    }
}

