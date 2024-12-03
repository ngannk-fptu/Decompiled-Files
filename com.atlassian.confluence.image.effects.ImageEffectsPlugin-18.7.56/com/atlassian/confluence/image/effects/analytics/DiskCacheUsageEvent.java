/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.image.effects.analytics;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.imageeffects.diskcacheusage")
public class DiskCacheUsageEvent {
    private final boolean cacheHit;
    private final String cacheEntryName;

    public DiskCacheUsageEvent(boolean cacheHit, String cacheEntryName) {
        this.cacheHit = cacheHit;
        this.cacheEntryName = cacheEntryName;
    }

    public boolean isCacheHit() {
        return this.cacheHit;
    }

    public String getCacheEntryName() {
        return this.cacheEntryName;
    }
}

