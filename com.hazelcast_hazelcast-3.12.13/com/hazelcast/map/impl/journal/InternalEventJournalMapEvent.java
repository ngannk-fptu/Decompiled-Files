/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.journal;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class InternalEventJournalMapEvent
implements IdentifiedDataSerializable {
    protected Data dataKey;
    protected Data dataNewValue;
    protected Data dataOldValue;
    protected int eventType;

    public InternalEventJournalMapEvent() {
    }

    public InternalEventJournalMapEvent(Data dataKey, Data dataNewValue, Data dataOldValue, int eventType) {
        this.eventType = eventType;
        this.dataKey = dataKey;
        this.dataNewValue = dataNewValue;
        this.dataOldValue = dataOldValue;
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

    public int getEventType() {
        return this.eventType;
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 144;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.eventType);
        out.writeData(this.dataKey);
        out.writeData(this.dataNewValue);
        out.writeData(this.dataOldValue);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.eventType = in.readInt();
        this.dataKey = in.readData();
        this.dataNewValue = in.readData();
        this.dataOldValue = in.readData();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        InternalEventJournalMapEvent that = (InternalEventJournalMapEvent)o;
        if (this.eventType != that.eventType) {
            return false;
        }
        if (this.dataKey != null ? !this.dataKey.equals(that.dataKey) : that.dataKey != null) {
            return false;
        }
        if (this.dataNewValue != null ? !this.dataNewValue.equals(that.dataNewValue) : that.dataNewValue != null) {
            return false;
        }
        return this.dataOldValue != null ? this.dataOldValue.equals(that.dataOldValue) : that.dataOldValue == null;
    }

    public int hashCode() {
        int result = this.dataKey != null ? this.dataKey.hashCode() : 0;
        result = 31 * result + (this.dataNewValue != null ? this.dataNewValue.hashCode() : 0);
        result = 31 * result + (this.dataOldValue != null ? this.dataOldValue.hashCode() : 0);
        result = 31 * result + this.eventType;
        return result;
    }

    public String toString() {
        return "InternalEventJournalMapEvent{eventType=" + this.eventType + '}';
    }
}

