/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache;

import com.hazelcast.internal.eviction.Evictable;
import com.hazelcast.internal.eviction.Expirable;
import java.util.UUID;

public interface NearCacheRecord<V>
extends Expirable,
Evictable<V> {
    public static final int TIME_NOT_SET = -1;
    public static final long NOT_RESERVED = -1L;
    public static final long RESERVED = -2L;
    public static final long UPDATE_STARTED = -3L;
    public static final long READ_PERMITTED = -4L;

    public void setValue(V var1);

    public void setCreationTime(long var1);

    public void setAccessTime(long var1);

    public void setAccessHit(int var1);

    public void incrementAccessHit();

    public void resetAccessHit();

    public boolean isIdleAt(long var1, long var3);

    public long getRecordState();

    public boolean casRecordState(long var1, long var3);

    public int getPartitionId();

    public void setPartitionId(int var1);

    public long getInvalidationSequence();

    public void setInvalidationSequence(long var1);

    public void setUuid(UUID var1);

    public boolean hasSameUuid(UUID var1);
}

