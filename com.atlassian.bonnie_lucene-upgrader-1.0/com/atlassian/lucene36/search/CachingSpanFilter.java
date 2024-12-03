/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.CachingWrapperFilter;
import com.atlassian.lucene36.search.DocIdSet;
import com.atlassian.lucene36.search.SpanFilter;
import com.atlassian.lucene36.search.SpanFilterResult;
import java.io.IOException;

public class CachingSpanFilter
extends SpanFilter {
    private SpanFilter filter;
    private final CachingWrapperFilter.FilterCache<SpanFilterResult> cache;
    int hitCount;
    int missCount;

    public CachingSpanFilter(SpanFilter filter) {
        this(filter, CachingWrapperFilter.DeletesMode.RECACHE);
    }

    public CachingSpanFilter(SpanFilter filter, CachingWrapperFilter.DeletesMode deletesMode) {
        this.filter = filter;
        if (deletesMode == CachingWrapperFilter.DeletesMode.DYNAMIC) {
            throw new IllegalArgumentException("DeletesMode.DYNAMIC is not supported");
        }
        this.cache = new CachingWrapperFilter.FilterCache<SpanFilterResult>(deletesMode){

            @Override
            protected SpanFilterResult mergeDeletes(IndexReader reader, SpanFilterResult value) {
                throw new IllegalStateException("DeletesMode.DYNAMIC is not supported");
            }
        };
    }

    public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
        SpanFilterResult result = this.getCachedResult(reader);
        return result != null ? result.getDocIdSet() : null;
    }

    private SpanFilterResult getCachedResult(IndexReader reader) throws IOException {
        Object delCoreKey;
        Object coreKey = reader.getCoreCacheKey();
        SpanFilterResult result = this.cache.get(reader, coreKey, delCoreKey = reader.hasDeletions() ? reader.getDeletesCacheKey() : coreKey);
        if (result != null) {
            ++this.hitCount;
            return result;
        }
        ++this.missCount;
        result = this.filter.bitSpans(reader);
        this.cache.put(coreKey, delCoreKey, result);
        return result;
    }

    public SpanFilterResult bitSpans(IndexReader reader) throws IOException {
        return this.getCachedResult(reader);
    }

    public String toString() {
        return "CachingSpanFilter(" + this.filter + ")";
    }

    public boolean equals(Object o) {
        if (!(o instanceof CachingSpanFilter)) {
            return false;
        }
        return this.filter.equals(((CachingSpanFilter)o).filter);
    }

    public int hashCode() {
        return this.filter.hashCode() ^ 0x1117BF25;
    }
}

