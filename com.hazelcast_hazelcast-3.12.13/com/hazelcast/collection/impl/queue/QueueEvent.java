/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue;

import com.hazelcast.collection.impl.queue.QueueDataSerializerHook;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class QueueEvent
implements IdentifiedDataSerializable {
    String name;
    Data data;
    ItemEventType eventType;
    Address caller;

    public QueueEvent() {
    }

    public QueueEvent(String name, Data data, ItemEventType eventType, Address caller) {
        this.name = name;
        this.data = data;
        this.eventType = eventType;
        this.caller = caller;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.eventType.getType());
        this.caller.writeData(out);
        out.writeData(this.data);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.eventType = ItemEventType.getByType(in.readInt());
        this.caller = new Address();
        this.caller.readData(in);
        this.data = in.readData();
    }

    @Override
    public int getFactoryId() {
        return QueueDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 15;
    }

    public String getName() {
        return this.name;
    }

    public ItemEventType getEventType() {
        return this.eventType;
    }
}

