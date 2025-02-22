/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.client;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapPortableHook;
import java.io.IOException;

public class ReplicatedMapPortableEntryEvent
implements Portable {
    private Data key;
    private Data value;
    private Data oldValue;
    private EntryEventType eventType;
    private String uuid;
    private int numberOfAffectedEntries;

    ReplicatedMapPortableEntryEvent() {
    }

    ReplicatedMapPortableEntryEvent(Data key, Data value, Data oldValue, EntryEventType eventType, String uuid) {
        this(key, value, oldValue, eventType, uuid, 0);
    }

    ReplicatedMapPortableEntryEvent(Data key, Data value, Data oldValue, EntryEventType eventType, String uuid, int numberOfAffectedEntries) {
        this.key = key;
        this.value = value;
        this.oldValue = oldValue;
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
    public void writePortable(PortableWriter writer) throws IOException {
        writer.writeInt("e", this.eventType.getType());
        writer.writeUTF("u", this.uuid);
        ObjectDataOutput out = writer.getRawDataOutput();
        out.writeData(this.key);
        out.writeData(this.value);
        out.writeData(this.oldValue);
        out.writeInt(this.numberOfAffectedEntries);
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        this.eventType = EntryEventType.getByType(reader.readInt("e"));
        this.uuid = reader.readUTF("u");
        ObjectDataInput in = reader.getRawDataInput();
        this.key = in.readData();
        this.value = in.readData();
        this.oldValue = in.readData();
        this.numberOfAffectedEntries = in.readInt();
    }

    @Override
    public int getFactoryId() {
        return ReplicatedMapPortableHook.F_ID;
    }

    @Override
    public int getClassId() {
        return 18;
    }
}

