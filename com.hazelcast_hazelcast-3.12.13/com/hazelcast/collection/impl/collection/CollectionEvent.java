/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection;

import com.hazelcast.collection.impl.collection.CollectionDataSerializerHook;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class CollectionEvent
implements IdentifiedDataSerializable {
    private String name;
    private Data data;
    private ItemEventType eventType;
    private Address caller;

    public CollectionEvent() {
    }

    public CollectionEvent(String name, Data data, ItemEventType eventType, Address caller) {
        this.name = name;
        this.data = data;
        this.eventType = eventType;
        this.caller = caller;
    }

    public String getName() {
        return this.name;
    }

    public Data getData() {
        return this.data;
    }

    public ItemEventType getEventType() {
        return this.eventType;
    }

    public Address getCaller() {
        return this.caller;
    }

    @Override
    public int getFactoryId() {
        return CollectionDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 22;
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
}

