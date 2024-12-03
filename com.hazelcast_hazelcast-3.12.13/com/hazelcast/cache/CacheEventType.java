/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.event.EventType
 */
package com.hazelcast.cache;

import javax.cache.event.EventType;

public enum CacheEventType {
    CREATED(1),
    UPDATED(2),
    REMOVED(3),
    EXPIRED(4),
    EVICTED(5),
    INVALIDATED(6),
    COMPLETED(7),
    EXPIRATION_TIME_UPDATED(8),
    PARTITION_LOST(9);

    private static final int MIN_TYPE_ID;
    private static final int MAX_TYPE_ID;
    private static final CacheEventType[] CACHED_VALUES;
    private int type;

    private CacheEventType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public static CacheEventType getByType(int eventType) {
        if (MIN_TYPE_ID <= eventType && eventType <= MAX_TYPE_ID) {
            return CACHED_VALUES[eventType - 1];
        }
        return null;
    }

    public static EventType convertToEventType(CacheEventType cacheEventType) {
        return EventType.valueOf((String)cacheEventType.name());
    }

    static {
        MIN_TYPE_ID = CacheEventType.CREATED.type;
        MAX_TYPE_ID = CacheEventType.PARTITION_LOST.type;
        CACHED_VALUES = CacheEventType.values();
    }
}

