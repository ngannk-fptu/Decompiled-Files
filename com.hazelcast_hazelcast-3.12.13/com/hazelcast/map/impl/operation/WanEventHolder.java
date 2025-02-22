/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.internal.util.ToHeapDataConverter;
import com.hazelcast.nio.serialization.Data;

public class WanEventHolder {
    private final Data key;
    private final Data value;
    private final EntryEventType eventType;

    public WanEventHolder(Data key, Data value, EntryEventType eventType) {
        this.key = ToHeapDataConverter.toHeapData(key);
        this.value = ToHeapDataConverter.toHeapData(value);
        this.eventType = eventType;
    }

    public Data getKey() {
        return this.key;
    }

    public Data getValue() {
        return this.value;
    }

    public EntryEventType getEventType() {
        return this.eventType;
    }
}

