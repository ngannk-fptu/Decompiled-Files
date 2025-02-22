/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ThrottlingCache {
    static final int MAX_THROTTLING_TIME_SEC = 3600;
    static int DEFAULT_THROTTLING_TIME_SEC = 120;
    static final int CACHE_SIZE_LIMIT_TO_TRIGGER_EXPIRED_ENTITIES_REMOVAL = 100;
    static Map<String, Long> requestsToThrottle = new ConcurrentHashMap<String, Long>();

    ThrottlingCache() {
    }

    static void set(String requestHash, Long expirationTimestamp) {
        ThrottlingCache.removeInvalidCacheEntities();
        requestsToThrottle.put(requestHash, expirationTimestamp);
    }

    static long retryInMs(String requestHash) {
        ThrottlingCache.removeInvalidCacheEntities();
        if (requestsToThrottle.containsKey(requestHash)) {
            long expirationTimestamp = requestsToThrottle.get(requestHash);
            long currentTimestamp = System.currentTimeMillis();
            if (ThrottlingCache.isCacheEntryValid(currentTimestamp, expirationTimestamp)) {
                return expirationTimestamp - currentTimestamp;
            }
            requestsToThrottle.remove(requestHash);
        }
        return 0L;
    }

    private static boolean isCacheEntryValid(long currentTimestamp, long expirationTimestamp) {
        return currentTimestamp < expirationTimestamp && currentTimestamp >= expirationTimestamp - 3600000L;
    }

    private static void removeInvalidCacheEntities() {
        long currentTimestamp = System.currentTimeMillis();
        if (requestsToThrottle.size() > 100) {
            requestsToThrottle.values().removeIf(value -> !ThrottlingCache.isCacheEntryValid(value, currentTimestamp));
        }
    }

    static void clear() {
        requestsToThrottle.clear();
    }
}

