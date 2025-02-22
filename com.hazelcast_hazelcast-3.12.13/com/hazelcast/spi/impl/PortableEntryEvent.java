/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.spi.impl.SpiPortableHook;
import java.io.IOException;

public class PortableEntryEvent
implements Portable {
    private Data key;
    private Data value;
    private Data oldValue;
    private Data mergingValue;
    private EntryEventType eventType;
    private String uuid;
    private int numberOfAffectedEntries = 1;

    public PortableEntryEvent() {
    }

    public PortableEntryEvent(Data key, Data value, Data oldValue, Data mergingValue, EntryEventType eventType, String uuid) {
        this.key = key;
        this.value = value;
        this.oldValue = oldValue;
        this.mergingValue = mergingValue;
        this.eventType = eventType;
        this.uuid = uuid;
    }

    public PortableEntryEvent(EntryEventType eventType, String uuid, int numberOfAffectedEntries) {
        this.eventType = eventType;
        this.uuid = uuid;
        this.numberOfAffectedEntries = numberOfAffectedEntries;
    }

    public Data getKey() {
        return this.key;
    }

    public Data getValue() {
        return this.value;
    }

    public Data getOldValue() {
        return this.oldValue;
    }

    public Data getMergingValue() {
        return this.mergingValue;
    }

    public EntryEventType getEventType() {
        return this.eventType;
    }

    public String getUuid() {
        return this.uuid;
    }

    public int getNumberOfAffectedEntries() {
        return this.numberOfAffectedEntries;
    }

    @Override
    public int getFactoryId() {
        return SpiPortableHook.ID;
    }

    @Override
    public int getClassId() {
        return 4;
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        writer.writeInt("e", this.eventType.getType());
        writer.writeUTF("u", this.uuid);
        writer.writeInt("n", this.numberOfAffectedEntries);
        ObjectDataOutput out = writer.getRawDataOutput();
        out.writeData(this.key);
        out.writeData(this.value);
        out.writeData(this.oldValue);
        out.writeData(this.mergingValue);
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        this.eventType = EntryEventType.getByType(reader.readInt("e"));
        this.uuid = reader.readUTF("u");
        this.numberOfAffectedEntries = reader.readInt("n");
        ObjectDataInput in = reader.getRawDataInput();
        this.key = in.readData();
        this.value = in.readData();
        this.oldValue = in.readData();
        this.mergingValue = in.readData();
    }
}

