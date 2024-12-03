/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection;

import com.hazelcast.collection.impl.collection.CollectionDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.EventFilter;
import java.io.IOException;

public class CollectionEventFilter
implements EventFilter,
IdentifiedDataSerializable {
    private boolean includeValue;

    public CollectionEventFilter() {
    }

    public CollectionEventFilter(boolean includeValue) {
        this.includeValue = includeValue;
    }

    public boolean isIncludeValue() {
        return this.includeValue;
    }

    @Override
    public boolean eval(Object arg) {
        return false;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CollectionEventFilter that = (CollectionEventFilter)o;
        return this.includeValue == that.includeValue;
    }

    public int hashCode() {
        return this.includeValue ? 1 : 0;
    }

    @Override
    public int getFactoryId() {
        return CollectionDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 21;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeBoolean(this.includeValue);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.includeValue = in.readBoolean();
    }
}

