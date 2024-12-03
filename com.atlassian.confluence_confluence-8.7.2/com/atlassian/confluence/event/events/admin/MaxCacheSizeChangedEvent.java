/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.ConfigurationEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;

public class MaxCacheSizeChangedEvent
extends ConfigurationEvent
implements ClusterEvent {
    private static final long serialVersionUID = 7824717436134869972L;
    private final String cacheName;
    private final int maxCacheSize;
    private final int previousMaxCacheSize;

    @Deprecated
    public MaxCacheSizeChangedEvent(Object src, String cacheName, int maxCacheSize) {
        this(src, cacheName, -1, maxCacheSize);
    }

    public MaxCacheSizeChangedEvent(Object src, String cacheName, int previousMaxCacheSize, int maxCacheSize) {
        super(src);
        this.cacheName = cacheName;
        this.maxCacheSize = maxCacheSize;
        this.previousMaxCacheSize = previousMaxCacheSize;
    }

    public int getMaxCacheSize() {
        return this.maxCacheSize;
    }

    public String getCacheName() {
        return this.cacheName;
    }

    public int getPreviousMaxCacheSize() {
        return this.previousMaxCacheSize;
    }
}

