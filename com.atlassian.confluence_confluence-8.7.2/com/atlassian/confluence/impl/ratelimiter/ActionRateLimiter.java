/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.util.concurrent.RateLimiter
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.ratelimiter;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ActionRateLimiter {
    private static final Logger log = LoggerFactory.getLogger(ActionRateLimiter.class);
    private final Double permitsPerSecondValue;
    private final Double permitsPerSecondGlobalValue;
    private final Cache<String, RateLimiter> limiterCache;

    public ActionRateLimiter(@NonNull String permitsPerSecondKey, @NonNull double defaultPermitsPerSecond, @NonNull String permitsPerSecondGlobalKey, @NonNull double defaultPermitsPerSecondGlobal, @NonNull String cacheExpiryLimitKey, @NonNull long defaultCacheExpiryLimit) {
        this.permitsPerSecondValue = this.getPermitsPerSecond(permitsPerSecondKey, defaultPermitsPerSecond);
        this.permitsPerSecondGlobalValue = this.getPermitsPerSecondGlobal(permitsPerSecondGlobalKey, defaultPermitsPerSecondGlobal);
        long cacheExpiryLimitValue = this.getCacheExpiryDurationMinutes(cacheExpiryLimitKey, defaultCacheExpiryLimit);
        this.limiterCache = CacheBuilder.newBuilder().expireAfterAccess(cacheExpiryLimitValue, TimeUnit.MINUTES).build();
    }

    public String isRequestAllowed(String usernameOrEmail, String actionId) {
        try {
            String key = usernameOrEmail + actionId;
            if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
                if (((RateLimiter)this.getLimiterCache().get((Object)key, () -> RateLimiter.create((double)this.permitsPerSecondGlobalValue))).tryAcquire()) {
                    return "success";
                }
                return "request-denied-global";
            }
            if (((RateLimiter)this.getLimiterCache().get((Object)key, () -> RateLimiter.create((double)this.permitsPerSecondValue))).tryAcquire()) {
                return "success";
            }
            return "request-denied";
        }
        catch (ExecutionException e) {
            log.warn("Error while accessing LIMITER_CACHE", (Throwable)e);
            return "error";
        }
    }

    private Double getPermitsPerSecond(String permitsPerSecondKey, double permitsPerSecondDefault) {
        String permits = System.getProperty(permitsPerSecondKey);
        try {
            double parsedPermits;
            double d = parsedPermits = permits != null ? Double.parseDouble(permits) : permitsPerSecondDefault;
            if (parsedPermits < 0.0) {
                log.warn("Invalid value for {}: {}, Setting default value: {}", new Object[]{permitsPerSecondKey, permits, permitsPerSecondDefault});
                parsedPermits = permitsPerSecondDefault;
            }
            return parsedPermits;
        }
        catch (NumberFormatException ex) {
            log.warn("Invalid value for {}: {}, Setting default value: {}", new Object[]{permitsPerSecondKey, permits, permitsPerSecondDefault});
            return permitsPerSecondDefault;
        }
    }

    private Double getPermitsPerSecondGlobal(String permitsPerSecondGlobalKey, double permitsPerSecondGlobalDefault) {
        String permits = System.getProperty(permitsPerSecondGlobalKey);
        try {
            double parsedPermits;
            double d = parsedPermits = permits != null ? Double.parseDouble(permits) : permitsPerSecondGlobalDefault;
            if (parsedPermits < 0.0) {
                log.warn("Invalid value for {}:{}, Setting default value: {}", new Object[]{permitsPerSecondGlobalKey, permits, permitsPerSecondGlobalDefault});
                parsedPermits = permitsPerSecondGlobalDefault;
            }
            return parsedPermits;
        }
        catch (NumberFormatException ex) {
            log.warn("Invalid value for {}: {}, Setting default value: {}", new Object[]{permitsPerSecondGlobalKey, permits, permitsPerSecondGlobalDefault, ex});
            return permitsPerSecondGlobalDefault;
        }
    }

    private long getCacheExpiryDurationMinutes(String cacheExpiryLimitKey, long cacheExpiryLimitDefault) {
        try {
            Long cacheExpiryDurationMinutes = Long.getLong(cacheExpiryLimitKey) != null ? Long.getLong(cacheExpiryLimitKey) : cacheExpiryLimitDefault;
            if ((double)cacheExpiryDurationMinutes.longValue() < 0.0) {
                log.warn("Invalid value for {}: {}, Setting default value: {}", new Object[]{cacheExpiryLimitKey, cacheExpiryDurationMinutes, cacheExpiryLimitDefault});
                return cacheExpiryLimitDefault;
            }
            long minimumRequiredCacheExpiryDurationMinutes = (long)Math.max(1.0 / (this.permitsPerSecondValue * 60.0), 1.0 / (this.permitsPerSecondGlobalValue * 60.0));
            if (cacheExpiryDurationMinutes < minimumRequiredCacheExpiryDurationMinutes) {
                log.warn("Value for {} should not be less than permitsPerSecondValue : {}, Setting value: {}", new Object[]{cacheExpiryLimitKey, cacheExpiryDurationMinutes, minimumRequiredCacheExpiryDurationMinutes});
                return minimumRequiredCacheExpiryDurationMinutes;
            }
            return cacheExpiryDurationMinutes;
        }
        catch (Exception e) {
            log.info("Exception occurred while getting cacheExpiryDurationMinutes.Setting default value: {}", (Object)cacheExpiryLimitDefault);
            return cacheExpiryLimitDefault;
        }
    }

    private Cache<String, RateLimiter> getLimiterCache() {
        return this.limiterCache;
    }
}

