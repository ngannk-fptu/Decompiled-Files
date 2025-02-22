/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.event;

import com.hazelcast.map.impl.event.EventData;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;

@BinaryInterface
public class LocalEntryEventData<K, V>
implements EventData {
    private K key;
    private V value;
    private V oldValue;
    private String source;
    private int eventType;
    private Data keyData;
    private Data valueData;
    private Data oldValueData;
    private final SerializationService serializationService;
    private final int partitionId;

    public LocalEntryEventData(SerializationService serializationService, String source, int eventType, Object key, Object oldValue, Object value, int partitionId) {
        this.serializationService = serializationService;
        this.partitionId = partitionId;
        if (key instanceof Data) {
            this.keyData = (Data)key;
        } else {
            this.key = key;
        }
        if (value instanceof Data) {
            this.valueData = (Data)value;
        } else {
            this.value = value;
        }
        if (oldValue instanceof Data) {
            this.oldValueData = (Data)oldValue;
        } else {
            this.oldValue = oldValue;
        }
        this.source = source;
        this.eventType = eventType;
    }

    public V getValue() {
        if (this.value == null && this.serializationService != null) {
            this.value = this.serializationService.toObject(this.valueData);
        }
        return this.value;
    }

    public V getOldValue() {
        if (this.oldValue == null && this.serializationService != null) {
            this.oldValue = this.serializationService.toObject(this.oldValueData);
        }
        return this.oldValue;
    }

    public K getKey() {
        if (this.key == null && this.serializationService != null) {
            this.key = this.serializationService.toObject(this.keyData);
        }
        return this.key;
    }

    public Data getKeyData() {
        if (this.keyData == null && this.serializationService != null) {
            this.keyData = this.serializationService.toData(this.key);
        }
        return this.keyData;
    }

    public Data getValueData() {
        if (this.valueData == null && this.serializationService != null) {
            this.valueData = this.serializationService.toData(this.value);
        }
        return this.valueData;
    }

    public Data getOldValueData() {
        if (this.oldValueData == null && this.serializationService != null) {
            this.oldValueData = this.serializationService.toData(this.oldValue);
        }
        return this.oldValueData;
    }

    @Override
    public String getSource() {
        return this.source;
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
    public int getEventType() {
        return this.eventType;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public LocalEntryEventData<K, V> cloneWithoutValue() {
        return new LocalEntryEventData<K, V>(this.serializationService, this.source, this.eventType, this.key, null, null, this.partitionId);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return "LocalEntryEventData{eventType=" + this.eventType + ", key=" + this.getKey() + ", source='" + this.source + '\'' + '}';
    }
}

