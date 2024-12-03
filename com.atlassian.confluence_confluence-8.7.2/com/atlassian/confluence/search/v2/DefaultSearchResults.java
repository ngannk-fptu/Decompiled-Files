/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchWithToken;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class DefaultSearchResults
implements SearchResults {
    protected static final SearchResults EMPTY_RESULTS = new DefaultSearchResults(Collections.emptyList(), 0);
    private final List<? extends SearchResult> results;
    private final int unfilteredResultsCount;
    private final List<String> searchAfter;
    private List<String> searchWords;
    private String searchQuery;
    private SearchWithToken nextPageSearch;

    public DefaultSearchResults(List<? extends SearchResult> results, int unfilteredResultsCount, String searchQuery) {
        this(results, unfilteredResultsCount, null, Collections.emptyList(), searchQuery);
    }

    public DefaultSearchResults(List<? extends SearchResult> results, int unfilteredResultsCount) {
        this(results, unfilteredResultsCount, null, Collections.emptyList(), "");
    }

    @Deprecated(since="8.7.0", forRemoval=true)
    public DefaultSearchResults(List<? extends SearchResult> results, int unfilteredResultsCount, SearchWithToken nextPageSearch, List<String> searchWords) {
        this(results, unfilteredResultsCount, nextPageSearch, searchWords, "");
    }

    @Deprecated(since="8.7.0", forRemoval=true)
    public DefaultSearchResults(List<? extends SearchResult> results, int unfilteredResultsCount, SearchWithToken nextPageSearch, List<String> searchWords, String searchQuery) {
        this(results, unfilteredResultsCount, Collections.emptyList());
        this.nextPageSearch = nextPageSearch;
        this.searchWords = searchWords;
        this.searchQuery = searchQuery;
    }

    public DefaultSearchResults(List<? extends SearchResult> results, int unfilteredResultsCount, List<String> searchAfter) {
        this.results = results;
        this.unfilteredResultsCount = unfilteredResultsCount;
        this.searchAfter = searchAfter;
    }

    public List getRawResults() {
        return this.results;
    }

    @Override
    public int getUnfilteredResultsCount() {
        return this.unfilteredResultsCount;
    }

    @Override
    public int size() {
        return this.results.size();
    }

    @Override
    public Iterator<SearchResult> iterator() {
        return new ResultsIterator();
    }

    @Override
    public List<SearchResult> getAll() {
        return new ArrayList<SearchResult>(this.results);
    }

    @Override
    public List<String> getSearchWords() {
        return this.searchWords;
    }

    @Override
    public String getSearchQuery() {
        return this.searchQuery;
    }

    @Override
    public SearchWithToken getNextPageSearch() {
        return this.nextPageSearch;
    }

    @Override
    public boolean isLastPage() {
        return this.nextPageSearch == null;
    }

    @Override
    public List<String> getSearchAfter() {
        return this.searchAfter;
    }

    private class ResultsIterator
    implements Iterator<SearchResult> {
        int offset = 0;

        private ResultsIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.offset < DefaultSearchResults.this.results.size();
        }

        @Override
        public SearchResult next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return DefaultSearchResults.this.results.get(this.offset++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

