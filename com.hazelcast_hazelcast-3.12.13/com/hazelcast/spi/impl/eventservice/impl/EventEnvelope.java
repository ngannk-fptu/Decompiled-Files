/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import java.io.IOException;

public final class EventEnvelope
implements IdentifiedDataSerializable {
    private String id;
    private String serviceName;
    private Object event;

    public EventEnvelope() {
    }

    EventEnvelope(String id, String serviceName, Object event) {
        this.event = event;
        this.id = id;
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public Object getEvent() {
        return this.event;
    }

    public String getEventId() {
        return this.id;
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 6;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.id);
        out.writeUTF(this.serviceName);
        boolean isBinary = this.event instanceof Data;
        out.writeBoolean(isBinary);
        if (isBinary) {
            out.writeData((Data)this.event);
        } else {
            out.writeObject(this.event);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.id = in.readUTF();
        this.serviceName = in.readUTF();
        boolean isBinary = in.readBoolean();
        this.event = isBinary ? in.readData() : in.readObject();
    }

    public String toString() {
        return "EventEnvelope{id='" + this.id + "', serviceName='" + this.serviceName + "', event=" + this.event + '}';
    }
}

