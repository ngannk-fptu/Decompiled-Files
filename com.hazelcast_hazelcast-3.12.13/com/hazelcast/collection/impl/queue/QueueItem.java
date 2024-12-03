/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.QueueDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Clock;
import java.io.IOException;

public class QueueItem
implements IdentifiedDataSerializable,
Comparable<QueueItem> {
    protected long itemId;
    protected Data data;
    protected final long creationTime = Clock.currentTimeMillis();
    protected QueueContainer container;

    public QueueItem() {
    }

    public QueueItem(QueueContainer container, long itemId, Data data) {
        this();
        this.container = container;
        this.itemId = itemId;
        this.data = data;
    }

    public Data getData() {
        if (this.data == null && this.container != null) {
            this.data = this.container.getDataFromMap(this.itemId);
        }
        return this.data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public long getItemId() {
        return this.itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public QueueContainer getContainer() {
        return this.container;
    }

    @Override
    public int getFactoryId() {
        return QueueDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 17;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.itemId);
        out.writeData(this.data);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.itemId = in.readLong();
        this.data = in.readData();
    }

    @Override
    public int compareTo(QueueItem o) {
        if (this.itemId < o.getItemId()) {
            return -1;
        }
        if (this.itemId > o.getItemId()) {
            return 1;
        }
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueueItem)) {
            return false;
        }
        QueueItem item = (QueueItem)o;
        if (this.itemId != item.itemId) {
            return false;
        }
        return !(this.data != null ? !this.data.equals(item.data) : item.data != null);
    }

    public int hashCode() {
        int result = (int)(this.itemId ^ this.itemId >>> 32);
        result = 31 * result + (this.data != null ? this.data.hashCode() : 0);
        return result;
    }
}

