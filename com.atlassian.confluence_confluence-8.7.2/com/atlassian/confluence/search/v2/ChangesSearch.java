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

public class ChangesSearch
extends AbstractSearch {
    public static final String RECENT_UPDATES_SEARCH = "RecentUpdatesSearch";
    public static final String NETWORK_FEED_SEARCH = "NetworkFeedSearch";
    public static final String CHANGES_SEARCH = "ChangesSearch";

    public ChangesSearch(@NonNull SearchQuery query, SearchSort sort) {
        super(EnumSet.of(SearchIndex.CHANGE), query, sort);
    }

    public ChangesSearch(@NonNull SearchQuery query, SearchSort sort, int startOffset, int limit) {
        super(EnumSet.of(SearchIndex.CHANGE), query, sort, startOffset, limit);
    }

    public ChangesSearch(List<Index> indices, @NonNull SearchQuery query, SearchSort sort, int startOffset, int limit, List<String> searchAfter) {
        super(indices, query, sort, startOffset, limit, searchAfter);
    }

    @Override
    public String getSearchType() {
        return CHANGES_SEARCH;
    }

    @Override
    public ISearch withSearchAfter(List<String> searchAfter) {
        return new ChangesSearch(List.of(Index.CHANGE), this.getQuery(), this.getSort(), this.getStartOffset(), this.getLimit(), searchAfter);
    }

    @Override
    public ISearch withQuery(SearchQuery query) {
        return new ChangesSearch(this.getIndices(), query, this.getSort(), this.getStartOffset(), this.getLimit(), this.getSearchAfter());
    }
}

