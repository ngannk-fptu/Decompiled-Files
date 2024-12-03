/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.map.impl.querycache.subscriber.record.QueryCacheRecord;
import com.hazelcast.nio.serialization.Data;
import java.util.Map;
import java.util.Set;

public interface QueryCacheRecordStore {
    public QueryCacheRecord add(Data var1, Data var2);

    public QueryCacheRecord addWithoutEvictionCheck(Data var1, Data var2);

    public QueryCacheRecord get(Data var1);

    public QueryCacheRecord remove(Data var1);

    public boolean containsKey(Data var1);

    public boolean containsValue(Object var1);

    public Set<Data> keySet();

    public Set<Map.Entry<Data, QueryCacheRecord>> entrySet();

    public int clear();

    public boolean isEmpty();

    public int size();
}

