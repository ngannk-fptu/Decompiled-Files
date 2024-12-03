/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber.record;

import com.hazelcast.map.impl.querycache.subscriber.record.QueryCacheRecord;
import com.hazelcast.nio.serialization.Data;

public interface QueryCacheRecordFactory {
    public QueryCacheRecord createRecord(Data var1);

    public boolean isEquals(Object var1, Object var2);
}

