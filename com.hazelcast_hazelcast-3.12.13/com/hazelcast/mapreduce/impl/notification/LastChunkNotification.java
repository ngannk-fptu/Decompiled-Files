/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.notification;

import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.notification.MemberAwareMapReduceNotification;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.util.Map;

public class LastChunkNotification<KeyOut, Value>
extends MemberAwareMapReduceNotification {
    private Map<KeyOut, Value> chunk;
    private int partitionId;
    private Address sender;

    public LastChunkNotification() {
    }

    public LastChunkNotification(Address address, String name, String jobId, Address sender, int partitionId, Map<KeyOut, Value> chunk) {
        super(address, name, jobId);
        this.partitionId = partitionId;
        this.sender = sender;
        this.chunk = chunk;
    }

    public Map<KeyOut, Value> getChunk() {
        return this.chunk;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public Address getSender() {
        return this.sender;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        SerializationUtil.writeMap(this.chunk, out);
        out.writeInt(this.partitionId);
        out.writeObject(this.sender);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.chunk = SerializationUtil.readMap(in);
        this.partitionId = in.readInt();
        this.sender = (Address)in.readObject();
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public String toString() {
        return "LastChunkNotification{chunk=" + this.chunk + ", partitionId=" + this.partitionId + ", sender=" + this.sender + '}';
    }
}

