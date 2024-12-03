/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache;

import com.hazelcast.internal.adapter.DataStructureAdapter;
import com.hazelcast.internal.nearcache.NearCacheRecord;
import com.hazelcast.internal.nearcache.impl.invalidation.StaleReadDetector;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InitializingObject;

public interface NearCacheRecordStore<K, V>
extends InitializingObject {
    public V get(K var1);

    public void put(K var1, Data var2, V var3, Data var4);

    public long tryReserveForUpdate(K var1, Data var2);

    public V tryPublishReserved(K var1, V var2, long var3, boolean var5);

    public void invalidate(K var1);

    public void clear();

    public void destroy();

    public int size();

    public NearCacheRecord getRecord(K var1);

    public NearCacheStats getNearCacheStats();

    public void doExpiration();

    public void doEviction(boolean var1);

    public void loadKeys(DataStructureAdapter<Object, ?> var1);

    public void storeKeys();

    public void setStaleReadDetector(StaleReadDetector var1);
}

