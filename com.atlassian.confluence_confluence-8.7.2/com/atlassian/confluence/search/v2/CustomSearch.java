/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.AbstractSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.Index;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated(since="8.7.0", forRemoval=true)
public class CustomSearch
extends AbstractSearch {
    public CustomSearch(@NonNull SearchQuery query, SearchSort sort) {
        super(EnumSet.of(SearchIndex.CUSTOM), query, sort);
    }

    public CustomSearch(@NonNull SearchQuery query, SearchSort sort, int startOffset, int limit) {
        super(EnumSet.of(SearchIndex.CUSTOM), query, sort, startOffset, limit);
    }

    public CustomSearch(List<Index> indices, @NonNull SearchQuery query, SearchSort sort, int startOffset, int limit) {
        super(indices, query, sort, startOffset, limit, Collections.emptyList());
    }

    public CustomSearch(List<Index> indices, @NonNull SearchQuery query, SearchSort sort, int startOffset, int limit, List<String> searchAfter) {
        super(indices, query, sort, startOffset, limit, searchAfter);
    }

    @Override
    public ISearch withSearchAfter(List<String> searchAfter) {
        return new CustomSearch(this.indices, this.getQuery(), this.getSort(), this.getStartOffset(), this.getLimit(), searchAfter);
    }

    @Override
    public ISearch withIndices(List<Index> indices) {
        return new CustomSearch(indices, this.getQuery(), this.getSort(), this.getStartOffset(), this.getLimit(), this.getSearchAfter());
    }

    @Override
    public ISearch withQuery(SearchQuery query) {
        return new CustomSearch(this.getIndices(), query, this.getSort(), this.getStartOffset(), this.getLimit(), this.getSearchAfter());
    }
}

