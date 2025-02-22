/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.spi.impl.SpiPortableHook;
import java.io.IOException;

public class PortablePartitionLostEvent
implements Portable {
    private int partitionId;
    private int lostBackupCount;
    private Address source;

    public PortablePartitionLostEvent() {
    }

    public PortablePartitionLostEvent(int partitionId, int lostBackupCount, Address source) {
        this.partitionId = partitionId;
        this.lostBackupCount = lostBackupCount;
        this.source = source;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public int getLostBackupCount() {
        return this.lostBackupCount;
    }

    public Address getSource() {
        return this.source;
    }

    @Override
    public int getFactoryId() {
        return SpiPortableHook.ID;
    }

    @Override
    public int getClassId() {
        return 7;
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        writer.writeInt("p", this.partitionId);
        writer.writeInt("l", this.lostBackupCount);
        ObjectDataOutput out = writer.getRawDataOutput();
        out.writeObject(this.source);
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        this.partitionId = reader.readInt("p");
        this.lostBackupCount = reader.readInt("l");
        ObjectDataInput in = reader.getRawDataInput();
        this.source = (Address)in.readObject();
    }
}

