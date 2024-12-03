/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.partition.PartitionEvent;
import java.io.IOException;

@SerializableByConvention(value=SerializableByConvention.Reason.PUBLIC_API)
public class PartitionLostEvent
implements DataSerializable,
PartitionEvent {
    private int partitionId;
    private int lostBackupCount;
    private Address eventSource;

    public PartitionLostEvent() {
    }

    public PartitionLostEvent(int partitionId, int lostBackupCount, Address eventSource) {
        this.partitionId = partitionId;
        this.lostBackupCount = lostBackupCount;
        this.eventSource = eventSource;
    }

    @Override
    public int getPartitionId() {
        return this.partitionId;
    }

    public int getLostBackupCount() {
        return this.lostBackupCount;
    }

    public Address getEventSource() {
        return this.eventSource;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.partitionId);
        out.writeInt(this.lostBackupCount);
        out.writeObject(this.eventSource);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.partitionId = in.readInt();
        this.lostBackupCount = in.readInt();
        this.eventSource = (Address)in.readObject();
    }

    public String toString() {
        return this.getClass().getName() + "{partitionId=" + this.partitionId + ", lostBackupCount=" + this.lostBackupCount + ", eventSource=" + this.eventSource + '}';
    }
}

