/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.search.v2.DefaultSearchResults;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchWithToken;
import java.util.List;

@Deprecated
public class LuceneSearchResults
extends DefaultSearchResults {
    public static final SearchResults EMPTY_RESULTS = DefaultSearchResults.EMPTY_RESULTS;

    public LuceneSearchResults(List<? extends SearchResult> results, int unfilteredResultsCount) {
        super(results, unfilteredResultsCount);
    }

    public LuceneSearchResults(List<? extends SearchResult> results, int unfilteredResultsCount, SearchWithToken nextPageSearch, List<String> searchWords) {
        super(results, unfilteredResultsCount, nextPageSearch, searchWords);
    }
}

