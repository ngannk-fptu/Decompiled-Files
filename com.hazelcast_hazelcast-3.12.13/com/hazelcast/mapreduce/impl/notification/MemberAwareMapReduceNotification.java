/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.notification;

import com.hazelcast.mapreduce.impl.notification.MapReduceNotification;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public abstract class MemberAwareMapReduceNotification
extends MapReduceNotification {
    private Address address;

    protected MemberAwareMapReduceNotification() {
    }

    protected MemberAwareMapReduceNotification(Address address, String name, String jobId) {
        super(name, jobId);
        this.address = address;
    }

    public Address getAddress() {
        return this.address;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        this.address.writeData(out);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.address = new Address();
        this.address.readData(in);
    }

    @Override
    public String toString() {
        return "MemberAwareMapReduceNotification{address=" + this.address + '}';
    }
}

