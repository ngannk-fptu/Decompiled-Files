/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.cache.impl.CacheEventContext;
import com.hazelcast.nio.serialization.Data;

public final class CacheEventContextUtil {
    private CacheEventContextUtil() {
    }

    public static CacheEventContext createCacheCompleteEvent(int completionId) {
        CacheEventContext cacheEventContext = new CacheEventContext();
        cacheEventContext.setEventType(CacheEventType.COMPLETED);
        cacheEventContext.setCompletionId(completionId);
        return cacheEventContext;
    }

    public static CacheEventContext createCacheCompleteEvent(Data dataKey, int completionId) {
        CacheEventContext cacheEventContext = new CacheEventContext();
        cacheEventContext.setEventType(CacheEventType.COMPLETED);
        cacheEventContext.setDataKey(dataKey);
        cacheEventContext.setCompletionId(completionId);
        return cacheEventContext;
    }

    public static CacheEventContext createCacheCompleteEvent(Data dataKey, long expirationTime, String origin, int completionId) {
        CacheEventContext cacheEventContext = new CacheEventContext();
        cacheEventContext.setEventType(CacheEventType.COMPLETED);
        cacheEventContext.setDataKey(dataKey);
        cacheEventContext.setCompletionId(completionId);
        cacheEventContext.setOrigin(origin);
        cacheEventContext.setExpirationTime(expirationTime);
        return cacheEventContext;
    }

    public static CacheEventContext createCacheExpiredEvent(Data dataKey, Data dataValue, long expirationTime, String origin, int completionId) {
        CacheEventContext cacheEventContext = CacheEventContextUtil.createBaseEventContext(CacheEventType.EXPIRED, dataKey, dataValue, expirationTime, origin, completionId);
        return cacheEventContext;
    }

    public static CacheEventContext createCacheCreatedEvent(Data dataKey, Data dataValue, long expirationTime, String origin, int completionId) {
        CacheEventContext cacheEventContext = CacheEventContextUtil.createBaseEventContext(CacheEventType.CREATED, dataKey, dataValue, expirationTime, origin, completionId);
        return cacheEventContext;
    }

    public static CacheEventContext createCacheUpdatedEvent(Data dataKey, Data dataValue, Data dataOldValue, long creationTime, long expirationTime, long lastAccessTime, long accessHit, String origin, int completionId, Data expiryPolicy) {
        CacheEventContext cacheEventContext = CacheEventContextUtil.createBaseEventContext(CacheEventType.UPDATED, dataKey, dataValue, expirationTime, origin, completionId);
        cacheEventContext.setDataOldValue(dataOldValue);
        cacheEventContext.setIsOldValueAvailable(true);
        cacheEventContext.setCreationTime(creationTime);
        cacheEventContext.setLastAccessTime(lastAccessTime);
        cacheEventContext.setAccessHit(accessHit);
        cacheEventContext.setExpiryPolicy(expiryPolicy);
        return cacheEventContext;
    }

    public static CacheEventContext createCacheUpdatedEvent(Data dataKey, Data dataValue, Data dataOldValue, long creationTime, long expirationTime, long lastAccessTime, long accessHit, Data expiryPolicy) {
        return CacheEventContextUtil.createCacheUpdatedEvent(dataKey, dataValue, dataOldValue, creationTime, expirationTime, lastAccessTime, accessHit, null, -1, expiryPolicy);
    }

    public static CacheEventContext createCacheRemovedEvent(Data dataKey, Data dataValue, long expirationTime, String origin, int completionId) {
        CacheEventContext cacheEventContext = CacheEventContextUtil.createBaseEventContext(CacheEventType.REMOVED, dataKey, dataValue, expirationTime, origin, completionId);
        return cacheEventContext;
    }

    public static CacheEventContext createCacheRemovedEvent(Data dataKey) {
        return CacheEventContextUtil.createCacheRemovedEvent(dataKey, null, -1L, null, -1);
    }

    public static CacheEventContext createBaseEventContext(CacheEventType eventType, Data dataKey, Data dataValue, long expirationTime, String origin, int completionId) {
        CacheEventContext cacheEventContext = new CacheEventContext();
        cacheEventContext.setEventType(eventType);
        cacheEventContext.setDataKey(dataKey);
        cacheEventContext.setDataValue(dataValue);
        cacheEventContext.setExpirationTime(expirationTime);
        cacheEventContext.setOrigin(origin);
        cacheEventContext.setCompletionId(completionId);
        return cacheEventContext;
    }
}

