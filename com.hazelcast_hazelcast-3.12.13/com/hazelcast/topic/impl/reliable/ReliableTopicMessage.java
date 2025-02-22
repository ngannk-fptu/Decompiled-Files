/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic.impl.reliable;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.topic.impl.TopicDataSerializerHook;
import com.hazelcast.util.Clock;
import java.io.IOException;

@BinaryInterface
public class ReliableTopicMessage
implements IdentifiedDataSerializable {
    private long publishTime;
    private Address publisherAddress;
    private Data payload;

    public ReliableTopicMessage() {
    }

    public ReliableTopicMessage(Data payload, Address publisherAddress) {
        this.publishTime = Clock.currentTimeMillis();
        this.publisherAddress = publisherAddress;
        this.payload = payload;
    }

    public long getPublishTime() {
        return this.publishTime;
    }

    public Address getPublisherAddress() {
        return this.publisherAddress;
    }

    public Data getPayload() {
        return this.payload;
    }

    @Override
    public int getFactoryId() {
        return TopicDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.publishTime);
        out.writeObject(this.publisherAddress);
        out.writeData(this.payload);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.publishTime = in.readLong();
        this.publisherAddress = (Address)in.readObject();
        this.payload = in.readData();
    }
}

