/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.spi.impl.SpiPortableHook;
import java.io.IOException;

public class PortableMapPartitionLostEvent
implements Portable {
    private int partitionId;
    private String uuid;

    public PortableMapPartitionLostEvent() {
    }

    public PortableMapPartitionLostEvent(int partitionId, String uuid) {
        this.partitionId = partitionId;
        this.uuid = uuid;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public String getUuid() {
        return this.uuid;
    }

    @Override
    public int getFactoryId() {
        return SpiPortableHook.ID;
    }

    @Override
    public int getClassId() {
        return 6;
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        writer.writeInt("p", this.partitionId);
        writer.writeUTF("u", this.uuid);
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        this.partitionId = reader.readInt("p");
        this.uuid = reader.readUTF("u");
    }
}

