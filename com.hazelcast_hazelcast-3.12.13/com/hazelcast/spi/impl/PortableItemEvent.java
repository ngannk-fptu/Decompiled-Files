/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.spi.impl.SpiPortableHook;
import java.io.IOException;

public class PortableItemEvent
implements Portable {
    private Data item;
    private ItemEventType eventType;
    private String uuid;

    public PortableItemEvent() {
    }

    public PortableItemEvent(Data item, ItemEventType eventType, String uuid) {
        this.item = item;
        this.eventType = eventType;
        this.uuid = uuid;
    }

    public Data getItem() {
        return this.item;
    }

    public ItemEventType getEventType() {
        return this.eventType;
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
        return 3;
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        writer.writeInt("e", this.eventType.getType());
        writer.writeUTF("u", this.uuid);
        writer.getRawDataOutput().writeData(this.item);
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        this.eventType = ItemEventType.getByType(reader.readInt("e"));
        this.uuid = reader.readUTF("u");
        this.item = reader.getRawDataInput().readData();
    }
}

