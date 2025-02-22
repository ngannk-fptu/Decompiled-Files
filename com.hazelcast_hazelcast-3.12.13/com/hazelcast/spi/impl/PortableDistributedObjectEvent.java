/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.spi.impl.SpiPortableHook;
import java.io.IOException;

public class PortableDistributedObjectEvent
implements Portable {
    private DistributedObjectEvent.EventType eventType;
    private String name;
    private String serviceName;

    public PortableDistributedObjectEvent() {
    }

    public PortableDistributedObjectEvent(DistributedObjectEvent.EventType eventType, String name, String serviceName) {
        this.eventType = eventType;
        this.name = name;
        this.serviceName = serviceName;
    }

    public DistributedObjectEvent.EventType getEventType() {
        return this.eventType;
    }

    public String getName() {
        return this.name;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    @Override
    public int getFactoryId() {
        return SpiPortableHook.ID;
    }

    @Override
    public int getClassId() {
        return 5;
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        writer.writeUTF("n", this.name);
        writer.writeUTF("s", this.serviceName);
        writer.writeUTF("t", this.eventType.name());
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        this.name = reader.readUTF("n");
        this.serviceName = reader.readUTF("s");
        this.eventType = DistributedObjectEvent.EventType.valueOf(reader.readUTF("t"));
    }
}

