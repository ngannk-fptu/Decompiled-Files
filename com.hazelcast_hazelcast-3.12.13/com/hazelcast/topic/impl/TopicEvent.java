/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic.impl;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.topic.impl.TopicDataSerializerHook;
import com.hazelcast.util.Clock;
import java.io.IOException;

class TopicEvent
implements IdentifiedDataSerializable {
    String name;
    long publishTime;
    Address publisherAddress;
    Data data;

    public TopicEvent() {
    }

    TopicEvent(String name, Data data, Address publisherAddress) {
        this.name = name;
        this.publishTime = Clock.currentTimeMillis();
        this.publisherAddress = publisherAddress;
        this.data = data;
    }

    @Override
    public int getFactoryId() {
        return TopicDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeLong(this.publishTime);
        out.writeObject(this.publisherAddress);
        out.writeData(this.data);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.publishTime = in.readLong();
        this.publisherAddress = (Address)in.readObject();
        this.data = in.readData();
    }

    public String toString() {
        return "TopicEvent{name='" + this.name + '\'' + ", publishTime=" + this.publishTime + ", publisherAddress=" + this.publisherAddress + '}';
    }
}

