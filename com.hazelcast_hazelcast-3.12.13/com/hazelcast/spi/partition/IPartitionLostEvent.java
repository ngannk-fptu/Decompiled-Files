/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.partition;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.SerializableByConvention;
import java.io.IOException;

@SerializableByConvention(value=SerializableByConvention.Reason.PUBLIC_API)
public class IPartitionLostEvent
implements DataSerializable {
    private int partitionId;
    private int lostReplicaIndex;
    private Address eventSource;

    public IPartitionLostEvent() {
    }

    public IPartitionLostEvent(int partitionId, int lostReplicaIndex, Address eventSource) {
        this.partitionId = partitionId;
        this.lostReplicaIndex = lostReplicaIndex;
        this.eventSource = eventSource;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public int getLostReplicaIndex() {
        return this.lostReplicaIndex;
    }

    public Address getEventSource() {
        return this.eventSource;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.partitionId);
        out.writeInt(this.lostReplicaIndex);
        this.eventSource.writeData(out);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.partitionId = in.readInt();
        this.lostReplicaIndex = in.readInt();
        this.eventSource = new Address();
        this.eventSource.readData(in);
    }

    public String toString() {
        return this.getClass().getName() + "{partitionId=" + this.partitionId + ", lostReplicaIndex=" + this.lostReplicaIndex + ", eventSource=" + this.eventSource + '}';
    }
}

