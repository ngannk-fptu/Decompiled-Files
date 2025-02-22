/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.event;

import com.hazelcast.map.impl.event.AbstractEventData;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.Data;
import java.io.IOException;

@BinaryInterface
public class EntryEventData
extends AbstractEventData {
    protected Data dataKey;
    protected Data dataNewValue;
    protected Data dataOldValue;
    protected Data dataMergingValue;

    public EntryEventData() {
    }

    public EntryEventData(String source, String mapName, Address caller, Data dataKey, Data dataNewValue, Data dataOldValue, int eventType) {
        super(source, mapName, caller, eventType);
        this.dataKey = dataKey;
        this.dataNewValue = dataNewValue;
        this.dataOldValue = dataOldValue;
    }

    public EntryEventData(String source, String mapName, Address caller, Data dataKey, Data dataNewValue, Data dataOldValue, Data dataMergingValue, int eventType) {
        super(source, mapName, caller, eventType);
        this.dataKey = dataKey;
        this.dataNewValue = dataNewValue;
        this.dataOldValue = dataOldValue;
        this.dataMergingValue = dataMergingValue;
    }

    public Data getDataKey() {
        return this.dataKey;
    }

    public Data getDataNewValue() {
        return this.dataNewValue;
    }

    public Data getDataOldValue() {
        return this.dataOldValue;
    }

    public Data getDataMergingValue() {
        return this.dataMergingValue;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeData(this.dataKey);
        out.writeData(this.dataNewValue);
        out.writeData(this.dataOldValue);
        out.writeData(this.dataMergingValue);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.dataKey = in.readData();
        this.dataNewValue = in.readData();
        this.dataOldValue = in.readData();
        this.dataMergingValue = in.readData();
    }

    @Override
    public String toString() {
        return "EntryEventData{" + super.toString() + '}';
    }
}

