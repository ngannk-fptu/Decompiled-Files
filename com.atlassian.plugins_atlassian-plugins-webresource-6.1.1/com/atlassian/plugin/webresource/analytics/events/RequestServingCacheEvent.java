/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.plugin.webresource.analytics.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.plugin.webresource.impl.support.http.ServingType;

@AsynchronousPreferred
@EventName(value="wrm.caching.request.server")
public class RequestServingCacheEvent {
    private static final int EVENT_VERSION = 1;
    private final boolean cacheableRequest;
    private final boolean cacheHit;
    private final boolean cachingEnabled;
    private final boolean isSourceMap;
    private final ServingType servingType;
    private final long sizeInBytes;

    public RequestServingCacheEvent(boolean cacheableRequest, boolean cacheHit, boolean cachingEnabled, boolean isSourceMap, ServingType servingType, long sizeInBytes) {
        this.cacheableRequest = cacheableRequest;
        this.cacheHit = cacheHit;
        this.cachingEnabled = cachingEnabled;
        this.isSourceMap = isSourceMap;
        this.servingType = servingType;
        this.sizeInBytes = sizeInBytes;
    }

    public int getEventVersion() {
        return 1;
    }

    public boolean getCacheableRequest() {
        return this.cacheableRequest;
    }

    public boolean getCacheHit() {
        return this.cacheHit;
    }

    public boolean getCachingEnabled() {
        return this.cachingEnabled;
    }

    public boolean getIsSourceMap() {
        return this.isSourceMap;
    }

    public String getServingType() {
        return this.servingType.toString();
    }

    public long getSizeInBytes() {
        return this.sizeInBytes;
    }
}

