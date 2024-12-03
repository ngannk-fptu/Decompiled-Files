/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Metadata;

public interface Record<V> {
    public static final Object NOT_CACHED = new Object();
    public static final int NOT_AVAILABLE = -1;

    public Data getKey();

    public void setKey(Data var1);

    public V getValue();

    public void setValue(V var1);

    public void onAccess(long var1);

    public void onUpdate(long var1);

    public void onStore();

    public long getCost();

    public long getVersion();

    public void setVersion(long var1);

    public Object getCachedValueUnsafe();

    public boolean casCachedValue(Object var1, Object var2);

    public long getTtl();

    public void setTtl(long var1);

    public long getMaxIdle();

    public void setMaxIdle(long var1);

    public long getLastAccessTime();

    public void setLastAccessTime(long var1);

    public long getLastUpdateTime();

    public void setLastUpdateTime(long var1);

    public long getCreationTime();

    public void setCreationTime(long var1);

    public long getHits();

    public void setHits(long var1);

    public long getExpirationTime();

    public void setExpirationTime(long var1);

    public long getLastStoredTime();

    public void setLastStoredTime(long var1);

    public long getSequence();

    public void setSequence(long var1);

    public void setMetadata(Metadata var1);

    public Metadata getMetadata();
}

