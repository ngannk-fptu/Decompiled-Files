/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.event;

import com.hazelcast.map.impl.event.EventData;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.IOException;

@BinaryInterface
abstract class AbstractEventData
implements EventData {
    protected String source;
    protected String mapName;
    protected Address caller;
    protected int eventType;

    public AbstractEventData() {
    }

    public AbstractEventData(String source, String mapName, Address caller, int eventType) {
        this.source = source;
        this.mapName = mapName;
        this.caller = caller;
        this.eventType = eventType;
    }

    @Override
    public String getSource() {
        return this.source;
    }

    @Override
    public String getMapName() {
        return this.mapName;
    }

    @Override
    public Address getCaller() {
        return this.caller;
    }

    @Override
    public int getEventType() {
        return this.eventType;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.source);
        out.writeUTF(this.mapName);
        out.writeObject(this.caller);
        out.writeInt(this.eventType);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.source = in.readUTF();
        this.mapName = in.readUTF();
        this.caller = (Address)in.readObject();
        this.eventType = in.readInt();
    }

    public String toString() {
        return "source='" + this.source + '\'' + ", mapName='" + this.mapName + '\'' + ", caller=" + this.caller + ", eventType=" + this.eventType;
    }
}

