/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.event;

import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Clock;
import java.io.IOException;

@BinaryInterface
public class DefaultQueryCacheEventData
implements QueryCacheEventData {
    private Object key;
    private Object value;
    private Data dataKey;
    private Data dataNewValue;
    private Data dataOldValue;
    private long sequence;
    private SerializationService serializationService;
    private final long creationTime;
    private int eventType;
    private int partitionId;

    public DefaultQueryCacheEventData() {
        this.creationTime = Clock.currentTimeMillis();
    }

    public DefaultQueryCacheEventData(DefaultQueryCacheEventData other) {
        this.key = other.key;
        this.value = other.value;
        this.dataKey = other.dataKey;
        this.dataNewValue = other.dataNewValue;
        this.dataOldValue = other.dataOldValue;
        this.sequence = other.sequence;
        this.serializationService = other.serializationService;
        this.creationTime = other.creationTime;
        this.eventType = other.eventType;
        this.partitionId = other.partitionId;
    }

    @Override
    public Object getKey() {
        if (this.key == null && this.dataKey != null) {
            this.key = this.serializationService.toObject(this.dataKey);
        }
        return this.key;
    }

    @Override
    public Object getValue() {
        if (this.value == null && this.dataNewValue != null) {
            this.value = this.serializationService.toObject(this.dataNewValue);
        }
        return this.value;
    }

    @Override
    public Data getDataKey() {
        return this.dataKey;
    }

    @Override
    public Data getDataNewValue() {
        return this.dataNewValue;
    }

    @Override
    public Data getDataOldValue() {
        return this.dataOldValue;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public long getSequence() {
        return this.sequence;
    }

    @Override
    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public int getEventType() {
        return this.eventType;
    }

    @Override
    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public void setDataKey(Data dataKey) {
        this.dataKey = dataKey;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setDataNewValue(Data dataNewValue) {
        this.dataNewValue = dataNewValue;
    }

    public void setDataOldValue(Data dataOldValue) {
        this.dataOldValue = dataOldValue;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    @Override
    public void setSerializationService(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public String getSource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMapName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Address getCaller() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.sequence);
        out.writeData(this.dataKey);
        out.writeData(this.dataNewValue);
        out.writeInt(this.eventType);
        out.writeInt(this.partitionId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.sequence = in.readLong();
        this.dataKey = in.readData();
        this.dataNewValue = in.readData();
        this.eventType = in.readInt();
        this.partitionId = in.readInt();
    }

    public String toString() {
        return "DefaultSingleEventData{creationTime=" + this.creationTime + ", eventType=" + this.eventType + ", sequence=" + this.sequence + ", partitionId=" + this.partitionId + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultQueryCacheEventData that = (DefaultQueryCacheEventData)o;
        if (this.sequence != that.sequence) {
            return false;
        }
        if (this.eventType != that.eventType) {
            return false;
        }
        if (this.partitionId != that.partitionId) {
            return false;
        }
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        if (this.value != null ? !this.value.equals(that.value) : that.value != null) {
            return false;
        }
        if (this.dataKey != null ? !this.dataKey.equals(that.dataKey) : that.dataKey != null) {
            return false;
        }
        if (this.dataNewValue != null ? !this.dataNewValue.equals(that.dataNewValue) : that.dataNewValue != null) {
            return false;
        }
        if (this.dataOldValue != null ? !this.dataOldValue.equals(that.dataOldValue) : that.dataOldValue != null) {
            return false;
        }
        return this.serializationService != null ? this.serializationService.equals(that.serializationService) : that.serializationService == null;
    }

    public int hashCode() {
        int result = this.key != null ? this.key.hashCode() : 0;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        result = 31 * result + (this.dataKey != null ? this.dataKey.hashCode() : 0);
        result = 31 * result + (this.dataNewValue != null ? this.dataNewValue.hashCode() : 0);
        result = 31 * result + (this.dataOldValue != null ? this.dataOldValue.hashCode() : 0);
        result = 31 * result + (int)(this.sequence ^ this.sequence >>> 32);
        result = 31 * result + (this.serializationService != null ? this.serializationService.hashCode() : 0);
        result = 31 * result + this.eventType;
        result = 31 * result + this.partitionId;
        return result;
    }
}

