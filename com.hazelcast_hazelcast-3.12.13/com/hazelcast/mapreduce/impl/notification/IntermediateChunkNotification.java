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

public class IntermediateChunkNotification<KeyOut, Value>
extends MemberAwareMapReduceNotification {
    private Map<KeyOut, Value> chunk;
    private int partitionId;

    public IntermediateChunkNotification() {
    }

    public IntermediateChunkNotification(Address address, String name, String jobId, Map<KeyOut, Value> chunk, int partitionId) {
        super(address, name, jobId);
        this.chunk = chunk;
        this.partitionId = partitionId;
    }

    public Map<KeyOut, Value> getChunk() {
        return this.chunk;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        SerializationUtil.writeMap(this.chunk, out);
        out.writeInt(this.partitionId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.chunk = SerializationUtil.readMap(in);
        this.partitionId = in.readInt();
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public String toString() {
        return "IntermediateChunkNotification{chunk=" + this.chunk + '}';
    }
}

