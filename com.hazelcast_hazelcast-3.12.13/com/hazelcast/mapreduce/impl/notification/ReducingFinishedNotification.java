/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.notification;

import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.notification.MemberAwareMapReduceNotification;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class ReducingFinishedNotification
extends MemberAwareMapReduceNotification {
    private int partitionId;

    public ReducingFinishedNotification() {
    }

    public ReducingFinishedNotification(Address address, String name, String jobId, int partitionId) {
        super(address, name, jobId);
        this.partitionId = partitionId;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeInt(this.partitionId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.partitionId = in.readInt();
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 11;
    }

    @Override
    public String toString() {
        return "ReducingFinishedNotification{partitionId=" + this.partitionId + '}';
    }
}

