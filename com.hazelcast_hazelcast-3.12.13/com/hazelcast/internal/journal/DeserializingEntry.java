/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.journal;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.internal.journal.EventJournalDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.SerializationServiceSupport;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.util.Map;

public class DeserializingEntry<K, V>
implements Map.Entry<K, V>,
HazelcastInstanceAware,
IdentifiedDataSerializable {
    private Data dataKey;
    private Data dataValue;
    private transient K key;
    private transient V value;
    private transient SerializationService serializationService;

    DeserializingEntry() {
    }

    public DeserializingEntry(Data dataKey, Data dataValue) {
        this.dataKey = dataKey;
        this.dataValue = dataValue;
    }

    @Override
    public K getKey() {
        if (this.key == null && this.dataKey != null) {
            this.key = this.serializationService.toObject(this.dataKey);
        }
        return this.key;
    }

    @Override
    public V getValue() {
        if (this.value == null && this.dataValue != null) {
            this.value = this.serializationService.toObject(this.dataValue);
        }
        return this.value;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return this.debugInfo(this.key, this.dataKey) + "=" + this.debugInfo(this.value, this.dataValue);
    }

    private String debugInfo(Object deserialized, Data serialized) {
        if (deserialized != null) {
            return deserialized.toString();
        }
        if (serialized == null) {
            return "{serialized, null}";
        }
        return "{serialized, " + serialized.totalSize() + " bytes}";
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.serializationService = ((SerializationServiceSupport)((Object)hazelcastInstance)).getSerializationService();
    }

    @Override
    public int getFactoryId() {
        return EventJournalDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeData(this.toData(this.key, this.dataKey));
        out.writeData(this.toData(this.value, this.dataValue));
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.dataKey = in.readData();
        this.dataValue = in.readData();
    }

    private Data toData(Object value, Data defaultValue) {
        return value != null ? this.serializationService.toData(value) : defaultValue;
    }
}

