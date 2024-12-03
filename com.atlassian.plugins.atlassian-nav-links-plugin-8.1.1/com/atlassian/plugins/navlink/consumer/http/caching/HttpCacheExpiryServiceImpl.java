/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.failurecache.ExpiringValue
 *  com.atlassian.failurecache.util.date.Clock
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nullable
 *  org.apache.http.HttpResponse
 */
package com.atlassian.plugins.navlink.consumer.http.caching;

import com.atlassian.failurecache.ExpiringValue;
import com.atlassian.failurecache.util.date.Clock;
import com.atlassian.plugins.navlink.consumer.http.HeaderSearcher;
import com.atlassian.plugins.navlink.consumer.http.caching.HttpCacheExpiryService;
import com.google.common.base.MoreObjects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.apache.http.HttpResponse;

class HttpCacheExpiryServiceImpl
implements HttpCacheExpiryService {
    private final Clock clock;

    public HttpCacheExpiryServiceImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public <V> ExpiringValue<V> createExpiringValueFrom(HttpResponse response, @Nullable V value) {
        HeaderSearcher headerSearcher = new HeaderSearcher(response);
        long responseDateInMillis = this.findResponseDateInMillis(headerSearcher, this.clock.getCurrentDate().getTime());
        long maxAgeInMillis = this.findCacheControlMaxAgeInMillis(headerSearcher, 0L);
        long staleInMillis = this.findCacheControlStaleInMillis(headerSearcher, 0L);
        long softLimitInMillis = responseDateInMillis + maxAgeInMillis;
        long hardLimitInMillis = softLimitInMillis + staleInMillis;
        return new ExpiringValue(value, softLimitInMillis, hardLimitInMillis);
    }

    private long findResponseDateInMillis(HeaderSearcher headerSearcher, long defaultValue) {
        Long dateHeaderInMillis = headerSearcher.findFirstHeaderValueAsDateInMillis("Date");
        return (Long)MoreObjects.firstNonNull((Object)dateHeaderInMillis, (Object)defaultValue);
    }

    private long findCacheControlMaxAgeInMillis(HeaderSearcher headerSearcher, long defaultValue) {
        Long maxAgeInSeconds = headerSearcher.findFirstHeaderElementAsLong("Cache-Control", "max-age");
        return maxAgeInSeconds != null ? TimeUnit.SECONDS.toMillis(maxAgeInSeconds) : defaultValue;
    }

    private long findCacheControlStaleInMillis(HeaderSearcher headerSearcher, long defaultValue) {
        Long staleInSeconds = headerSearcher.findFirstHeaderElementAsLong("Cache-Control", "stale-while-revalidate");
        return staleInSeconds != null ? TimeUnit.SECONDS.toMillis(staleInSeconds) : defaultValue;
    }
}

