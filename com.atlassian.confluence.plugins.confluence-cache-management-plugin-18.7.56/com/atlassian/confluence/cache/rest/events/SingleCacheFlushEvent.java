/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.cache.rest.events;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.cachemanagement.flush.single")
public class SingleCacheFlushEvent {
    private final String cacheName;

    public SingleCacheFlushEvent(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getCacheName() {
        return this.cacheName;
    }
}

