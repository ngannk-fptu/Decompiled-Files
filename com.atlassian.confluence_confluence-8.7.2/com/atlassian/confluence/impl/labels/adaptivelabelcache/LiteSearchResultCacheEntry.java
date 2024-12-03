/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.labels.adaptivelabelcache;

import com.atlassian.confluence.labels.dto.LiteLabelSearchResult;
import java.io.Serializable;
import java.util.List;

public class LiteSearchResultCacheEntry
implements Serializable {
    private List<LiteLabelSearchResult> list;
    private int requestedLimit;
    private long requestTs;
    private long expirationTs;

    public LiteSearchResultCacheEntry() {
    }

    public LiteSearchResultCacheEntry(List<LiteLabelSearchResult> list, int requestedLimit, long expirationTs, long requestTs) {
        this.list = list;
        this.requestedLimit = requestedLimit;
        this.expirationTs = expirationTs;
        this.requestTs = requestTs;
    }

    public List<LiteLabelSearchResult> getList() {
        return this.list;
    }

    public List<LiteLabelSearchResult> getList(int limit) {
        return this.list.subList(0, Math.min(this.list.size(), limit));
    }

    public void setList(List<LiteLabelSearchResult> list) {
        this.list = list;
    }

    public int getRequestedLimit() {
        return this.requestedLimit;
    }

    public void setRequestedLimit(int requestedLimit) {
        this.requestedLimit = requestedLimit;
    }

    public long getExpirationTs() {
        return this.expirationTs;
    }

    public void setExpirationTs(long expirationTs) {
        this.expirationTs = expirationTs;
    }

    public boolean hasEnoughRecordsForTheNewLimit(int newLimit) {
        if (this.getRequestedLimit() >= newLimit) {
            return true;
        }
        return this.getList().size() < this.getRequestedLimit();
    }

    public long getRequestTs() {
        return this.requestTs;
    }
}

