/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.web.filter;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.EntryRefreshPolicy;
import com.opensymphony.oscache.web.filter.ResponseContent;

public class ExpiresRefreshPolicy
implements EntryRefreshPolicy {
    private long refreshPeriod;

    public ExpiresRefreshPolicy(int refreshPeriod) {
        this.refreshPeriod = (long)refreshPeriod * 1000L;
    }

    public boolean needsRefresh(CacheEntry entry) {
        long currentTimeMillis = System.currentTimeMillis();
        if (this.refreshPeriod >= 0L && currentTimeMillis >= entry.getLastUpdate() + this.refreshPeriod) {
            return true;
        }
        if (entry.getContent() instanceof ResponseContent) {
            ResponseContent responseContent = (ResponseContent)entry.getContent();
            return currentTimeMillis >= responseContent.getExpires();
        }
        return false;
    }
}

