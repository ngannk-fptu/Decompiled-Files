/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.proxyservice.impl;

import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;

@BinaryInterface
public final class DistributedObjectEventPacket
implements DataSerializable {
    private DistributedObjectEvent.EventType eventType;
    private String serviceName;
    private String name;

    public DistributedObjectEventPacket() {
    }

    public DistributedObjectEventPacket(DistributedObjectEvent.EventType eventType, String serviceName, String name) {
        this.eventType = eventType;
        this.serviceName = serviceName;
        this.name = name;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public DistributedObjectEvent.EventType getEventType() {
        return this.eventType;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeBoolean(this.eventType == DistributedObjectEvent.EventType.CREATED);
        out.writeUTF(this.serviceName);
        out.writeObject(this.name);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.eventType = in.readBoolean() ? DistributedObjectEvent.EventType.CREATED : DistributedObjectEvent.EventType.DESTROYED;
        this.serviceName = in.readUTF();
        this.name = (String)in.readObject();
    }

    public String toString() {
        return "DistributedObjectEvent{eventType=" + (Object)((Object)this.eventType) + ", serviceName='" + this.serviceName + '\'' + ", name=" + this.name + '}';
    }
}

