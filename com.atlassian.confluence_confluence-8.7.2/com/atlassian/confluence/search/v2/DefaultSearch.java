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
import java.util.EnumSet;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DefaultSearch
extends AbstractSearch {
    @Deprecated(since="8.7.0", forRemoval=true)
    public DefaultSearch(EnumSet<SearchIndex> indexes, SearchQuery query, SearchSort sort, int startOffset, int limit) {
        super(indexes, query, sort, startOffset, limit);
    }

    public DefaultSearch(List<Index> indices, SearchQuery query, SearchSort sort) {
        this(indices, query, sort, 0, 10);
    }

    public DefaultSearch(List<Index> indices, SearchQuery query, SearchSort sort, int startOffset, int limit) {
        super(indices, query, sort, startOffset, limit);
    }

    public DefaultSearch(List<Index> indices, @NonNull SearchQuery query, SearchSort sort, int startOffset, int limit, List<String> searchAfter) {
        super(indices, query, sort, startOffset, limit, searchAfter);
    }

    @Override
    public DefaultSearch withSearchAfter(@NonNull List<String> searchAfter) {
        return new DefaultSearch(this.getIndices(), this.getQuery(), this.getSort(), this.getStartOffset(), this.getLimit(), searchAfter);
    }

    @Override
    public ISearch withIndices(List<Index> indices) {
        return new DefaultSearch(indices, this.getQuery(), this.getSort(), this.getStartOffset(), this.getLimit(), this.getSearchAfter());
    }

    @Override
    public ISearch withQuery(SearchQuery query) {
        return new DefaultSearch(this.getIndices(), query, this.getSort(), this.getStartOffset(), this.getLimit(), this.getSearchAfter());
    }
}

