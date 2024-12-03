/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.events.MauEvent
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Maps
 *  io.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.analytics.client.pipeline.serialize.properties.extractors.mau;

import com.atlassian.analytics.api.events.MauEvent;
import com.atlassian.analytics.client.hash.AnalyticsEmailHasher;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.mau.IsMauEventAvailable;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import io.atlassian.util.concurrent.LazyReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class MauService {
    private final LazyReference<Boolean> isMauEventAvailable = new IsMauEventAvailable();
    private final Cache<Optional<String>, Optional<String>> emailHashCache;
    private static final String MAU_EMAIL_KEY = "email";
    private static final String MAU_EMAIL_HASH_KEY = "emailHash";
    @VisibleForTesting
    public static final String MAU_SUPPRESSED_USERNAME_VALUE = "suppressed";

    public MauService(CacheManager cacheManager, AnalyticsEmailHasher analyticsEmailHasher) {
        this.emailHashCache = cacheManager.getCache(MauService.class.getName() + ".emailHash", maybeEmail -> maybeEmail.map(analyticsEmailHasher::hash), new CacheSettingsBuilder().local().expireAfterAccess(2L, TimeUnit.HOURS).build());
    }

    public boolean isMauEvent(Object event) {
        return (Boolean)this.isMauEventAvailable.get() != false && event != null && MauEvent.class.getCanonicalName().equals(event.getClass().getCanonicalName());
    }

    Map<String, Object> hashEmailPropertyForMauEvent(Object event, Map<String, Object> properties) {
        if (this.isMauEvent(event)) {
            HashMap eventProperties = Maps.newHashMap(properties);
            Optional maybeEmailHash = (Optional)this.emailHashCache.get(Optional.ofNullable((String)eventProperties.get(MAU_EMAIL_KEY)));
            maybeEmailHash.ifPresent(hash -> eventProperties.put(MAU_EMAIL_HASH_KEY, hash));
            eventProperties.remove(MAU_EMAIL_KEY);
            return eventProperties;
        }
        return properties;
    }
}

