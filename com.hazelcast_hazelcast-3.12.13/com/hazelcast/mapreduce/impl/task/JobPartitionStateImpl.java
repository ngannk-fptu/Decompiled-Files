/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.mapreduce.JobPartitionState;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;

@BinaryInterface
public class JobPartitionStateImpl
implements JobPartitionState,
DataSerializable {
    private Address address;
    private JobPartitionState.State state;

    public JobPartitionStateImpl(Address address, JobPartitionState.State state) {
        this.address = address;
        this.state = state;
    }

    @Override
    public Address getOwner() {
        return this.address;
    }

    @Override
    public JobPartitionState.State getState() {
        return this.state;
    }

    public String toString() {
        return "JobPartitionStateImpl{state=" + (Object)((Object)this.state) + ", address=" + this.address + '}';
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.address);
        out.writeInt(this.state.ordinal());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.address = (Address)in.readObject();
        this.state = JobPartitionState.State.byOrdinal(in.readInt());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JobPartitionStateImpl that = (JobPartitionStateImpl)o;
        if (this.address != null ? !this.address.equals(that.address) : that.address != null) {
            return false;
        }
        return this.state == that.state;
    }

    public int hashCode() {
        int result = this.address != null ? this.address.hashCode() : 0;
        result = 31 * result + (this.state != null ? this.state.hashCode() : 0);
        return result;
    }
}

