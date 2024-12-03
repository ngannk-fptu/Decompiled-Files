/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.event.events.search;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.annotations.Internal;

@EventName(value="confluence.search.v2ShadowQueryExecution")
@Internal
public class V2ShadowQueryExecutionEvent {
    private final long durationMillis;
    private final String searchType;
    private final String shadowStatus;
    private final long discrepancyValue;

    public V2ShadowQueryExecutionEvent(long startMillis, long endMillis, String searchType, String shadowStatus, long discrepancyValue) {
        this.durationMillis = endMillis - startMillis;
        this.searchType = searchType;
        this.shadowStatus = shadowStatus;
        this.discrepancyValue = discrepancyValue;
    }

    public V2ShadowQueryExecutionEvent(long startMillis, long endMillis, String searchType, String shadowStatus) {
        this(startMillis, endMillis, searchType, shadowStatus, -1L);
    }

    public long getDurationMillis() {
        return this.durationMillis;
    }

    public String getSearchType() {
        return this.searchType;
    }

    public String getShadowStatus() {
        return this.shadowStatus;
    }

    public long getDiscrepancyValue() {
        return this.discrepancyValue;
    }
}

