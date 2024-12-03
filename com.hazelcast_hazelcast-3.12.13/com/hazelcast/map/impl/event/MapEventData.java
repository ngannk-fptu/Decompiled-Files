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
public class MapEventData
extends AbstractEventData {
    protected int numberOfEntries;

    public MapEventData() {
    }

    public MapEventData(String source, String mapName, Address caller, int eventType, int numberOfEntries) {
        super(source, mapName, caller, eventType);
        this.numberOfEntries = numberOfEntries;
    }

    public int getNumberOfEntries() {
        return this.numberOfEntries;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeInt(this.numberOfEntries);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.numberOfEntries = in.readInt();
    }

    @Override
    public String toString() {
        return "MapEventData{" + super.toString() + ", numberOfEntries=" + this.numberOfEntries + '}';
    }
}

