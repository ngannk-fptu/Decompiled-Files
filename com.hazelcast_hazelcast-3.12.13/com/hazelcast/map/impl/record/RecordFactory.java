/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.map.impl.record.Record;
import com.hazelcast.nio.serialization.Data;

public interface RecordFactory<T> {
    public Record<T> newRecord(Data var1, Object var2);

    public void setValue(Record<T> var1, Object var2);
}

