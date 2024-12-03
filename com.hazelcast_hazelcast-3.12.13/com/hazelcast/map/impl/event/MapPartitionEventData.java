/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.event;

import com.hazelcast.map.impl.event.AbstractEventData;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.IOException;

@BinaryInterface
public class MapPartitionEventData
extends AbstractEventData {
    private int partitionId;

    public MapPartitionEventData() {
    }

    public MapPartitionEventData(String source, String mapName, Address caller, int partitionId) {
        super(source, mapName, caller, -1);
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
    public String toString() {
        return "MapPartitionEventData{" + super.toString() + ", partitionId=" + this.partitionId + '}';
    }
}

