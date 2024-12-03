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
import com.atlassian.plugin.webresource.analytics.events.ServerResourceCacheInvalidationCause;

@AsynchronousPreferred
@EventName(value="wrm.caching.resource.invalidation.server")
public class ServerResourceCacheInvalidationEvent {
    private static final int EVENT_VERSION = 1;
    private final ServerResourceCacheInvalidationCause cacheInvalidationCause;

    public ServerResourceCacheInvalidationEvent(ServerResourceCacheInvalidationCause resourceCacheInvalidationCause) {
        this.cacheInvalidationCause = resourceCacheInvalidationCause;
    }

    public int getEventVersion() {
        return 1;
    }

    public String getCause() {
        return this.cacheInvalidationCause.toString();
    }
}

