/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.cache.rest.events;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.cachemanagement.config.update")
public class UpdateCacheConfigEvent {
    private final String cacheName;

    public UpdateCacheConfigEvent(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getCacheName() {
        return this.cacheName;
    }
}

