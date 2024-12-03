/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.event.events.search;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@EventName(value="confluence.search.v2QueryExecution")
public class V2QueryExecutionEvent {
    private final long durationMillis;
    private final long filterPreparationDurationMillis;
    private final long indexScanningDurationMillis;
    private final int totalHits;
    private final int startOffset;
    private final int limit;
    private final Set<SearchIndex> searchIndexes;

    public V2QueryExecutionEvent(long durationMillis, long filterPreparationDurationMillis, long indexScanningDurationMillis, int totalHits, int startOffset, int limit, Set<SearchIndex> searchIndexes) {
        this.searchIndexes = new HashSet<SearchIndex>(searchIndexes);
        this.durationMillis = durationMillis;
        this.filterPreparationDurationMillis = filterPreparationDurationMillis;
        this.indexScanningDurationMillis = indexScanningDurationMillis;
        this.totalHits = totalHits;
        this.startOffset = startOffset;
        this.limit = limit;
    }

    @Deprecated
    public V2QueryExecutionEvent(long startMillis, long endMillis, int totalHits, int startOffset, int limit, Set<SearchIndex> searchIndexes) {
        this(endMillis - startMillis, 0L, 0L, totalHits, startOffset, limit, searchIndexes);
    }

    @Deprecated
    public V2QueryExecutionEvent(long startMillis, long endMillis, int totalHits, int startOffset, int limit) {
        this(startMillis, endMillis, totalHits, startOffset, limit, Collections.emptySet());
    }

    public long getDurationMillis() {
        return this.durationMillis;
    }

    public long getTotalHits() {
        return this.totalHits;
    }

    public int getStartOffset() {
        return this.startOffset;
    }

    public int getLimit() {
        return this.limit;
    }

    public Set<SearchIndex> getSearchIndexes() {
        return Collections.unmodifiableSet(this.searchIndexes);
    }

    public long getFilterPreparationDurationMillis() {
        return this.filterPreparationDurationMillis;
    }

    public long getIndexScanningDurationMillis() {
        return this.indexScanningDurationMillis;
    }
}

