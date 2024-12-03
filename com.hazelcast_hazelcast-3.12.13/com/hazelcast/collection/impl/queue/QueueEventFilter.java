/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue;

import com.hazelcast.collection.impl.queue.QueueDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.EventFilter;
import java.io.IOException;

public class QueueEventFilter
implements EventFilter,
IdentifiedDataSerializable {
    private boolean includeValue;

    public QueueEventFilter() {
    }

    public QueueEventFilter(boolean includeValue) {
        this.includeValue = includeValue;
    }

    public boolean isIncludeValue() {
        return this.includeValue;
    }

    @Override
    public boolean eval(Object arg) {
        return false;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeBoolean(this.includeValue);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.includeValue = in.readBoolean();
    }

    @Override
    public int getFactoryId() {
        return QueueDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 16;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        QueueEventFilter that = (QueueEventFilter)o;
        return this.includeValue == that.includeValue;
    }

    public int hashCode() {
        return this.includeValue ? 1 : 0;
    }
}

