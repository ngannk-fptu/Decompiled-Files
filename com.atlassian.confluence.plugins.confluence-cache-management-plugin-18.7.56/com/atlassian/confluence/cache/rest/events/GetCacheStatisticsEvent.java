/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.cache.rest.events;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.cachemanagement.statistics.get")
public class GetCacheStatisticsEvent {
    private final long timeToFetchStatsInMillis;

    public GetCacheStatisticsEvent(long timeToFetchStatsInMillis) {
        this.timeToFetchStatsInMillis = timeToFetchStatsInMillis;
    }

    public long getTimeToFetchStatsInMillis() {
        return this.timeToFetchStatsInMillis;
    }
}

