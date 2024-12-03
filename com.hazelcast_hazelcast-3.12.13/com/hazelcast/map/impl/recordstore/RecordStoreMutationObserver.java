/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.recordstore;

import com.hazelcast.map.impl.record.Record;
import com.hazelcast.nio.serialization.Data;

public interface RecordStoreMutationObserver<R extends Record> {
    public void onClear();

    public void onPutRecord(Data var1, R var2);

    public void onReplicationPutRecord(Data var1, R var2);

    public void onUpdateRecord(Data var1, R var2, Object var3);

    public void onRemoveRecord(Data var1, R var2);

    public void onEvictRecord(Data var1, R var2);

    public void onLoadRecord(Data var1, R var2);

    public void onDestroy(boolean var1);

    public void onReset();
}

