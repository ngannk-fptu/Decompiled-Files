/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.Member;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

public class DataAwareEntryEvent<K, V>
extends EntryEvent<K, V> {
    private static final long serialVersionUID = 1L;
    private final transient Data dataKey;
    private final transient Data dataNewValue;
    private final transient Data dataOldValue;
    private final transient Data dataMergingValue;
    private final transient SerializationService serializationService;

    public DataAwareEntryEvent(Member from, int eventType, String source, Data dataKey, Data dataNewValue, Data dataOldValue, Data dataMergingValue, SerializationService serializationService) {
        super(source, from, eventType, null, null);
        this.dataKey = dataKey;
        this.dataNewValue = dataNewValue;
        this.dataOldValue = dataOldValue;
        this.dataMergingValue = dataMergingValue;
        this.serializationService = serializationService;
    }

    public Data getKeyData() {
        return this.dataKey;
    }

    public Data getNewValueData() {
        return this.dataNewValue;
    }

    public Data getOldValueData() {
        return this.dataOldValue;
    }

    public Data getMergingValueData() {
        return this.dataMergingValue;
    }

    @Override
    public K getKey() {
        if (this.key == null && this.dataKey != null) {
            this.key = this.serializationService.toObject(this.dataKey);
        }
        return (K)this.key;
    }

    @Override
    public V getOldValue() {
        if (this.oldValue == null && this.dataOldValue != null) {
            this.oldValue = this.serializationService.toObject(this.dataOldValue);
        }
        return (V)this.oldValue;
    }

    @Override
    public V getValue() {
        if (this.value == null && this.dataNewValue != null) {
            this.value = this.serializationService.toObject(this.dataNewValue);
        }
        return (V)this.value;
    }

    @Override
    public V getMergingValue() {
        if (this.mergingValue == null && this.dataMergingValue != null) {
            this.mergingValue = this.serializationService.toObject(this.dataMergingValue);
        }
        return (V)this.mergingValue;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        throw new NotSerializableException();
    }
}

