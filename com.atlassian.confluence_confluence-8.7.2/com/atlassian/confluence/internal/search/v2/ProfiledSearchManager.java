/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 */
package com.atlassian.confluence.internal.search.v2;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.Index;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchTokenExpiredException;
import com.atlassian.confluence.search.v2.SearchWithToken;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class ProfiledSearchManager
implements SearchManager {
    public static final String SEARCH_MANAGER_TIMER_NAME = "search.manager";
    @VisibleForTesting
    static final String METHOD_NAME = "methodName";
    @VisibleForTesting
    static final String SEARCH_TYPE = "searchType";
    @VisibleForTesting
    static final String RESULT_SIZE = "resultSize";
    private final SearchManager delegate;

    public ProfiledSearchManager(SearchManager delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public SearchResults search(ISearch search) throws InvalidSearchException {
        try (Ticker ignored = ProfiledSearchManager.startTimer("search", search);){
            SearchResults searchResults = this.delegate.search(search);
            return searchResults;
        }
    }

    @Override
    public SearchResults search(SearchWithToken search) throws SearchTokenExpiredException, InvalidSearchException {
        try (Ticker ignored = ProfiledSearchManager.startTimer("searchWithToken", search);){
            SearchResults searchResults = this.delegate.search(search);
            return searchResults;
        }
    }

    @Override
    public SearchResults search(ISearch search, Set<String> requestedFields) throws InvalidSearchException {
        try (Ticker ignored = ProfiledSearchManager.startTimer("searchWithRequestedFields", search);){
            SearchResults searchResults = this.delegate.search(search, requestedFields);
            return searchResults;
        }
    }

    @Override
    public List<Searchable> searchEntities(ISearch search, SearchManager.EntityVersionPolicy versionPolicy) throws InvalidSearchException {
        try (Ticker ignored = ProfiledSearchManager.startTimer("searchEntities", search);){
            List<Searchable> list = this.delegate.searchEntities(search, versionPolicy);
            return list;
        }
    }

    @Override
    public List<Searchable> convertToEntities(SearchResults searchResults, SearchManager.EntityVersionPolicy versionPolicy) {
        try (Ticker ignored = ProfiledSearchManager.startTimer("convertToEntities", searchResults);){
            List<Searchable> list = this.delegate.convertToEntities(searchResults, versionPolicy);
            return list;
        }
    }

    @Override
    public String explain(ISearch search, long contentId) {
        try (Ticker ignored = ProfiledSearchManager.startTimer("explain", search);){
            String string = this.delegate.explain(search, contentId);
            return string;
        }
    }

    @Override
    public <T> Map<T, List<Map<String, String>>> searchCategorised(ISearch search, SearchManager.Categorizer<T> categorizer) throws InvalidSearchException {
        try (Ticker ignored = ProfiledSearchManager.startTimer("searchCategorised", search);){
            Map<T, List<Map<String, String>>> map = this.delegate.searchCategorised(search, categorizer);
            return map;
        }
    }

    @Override
    public long scan(EnumSet<SearchIndex> indexes, SearchQuery searchQuery, Set<String> requestedFields, Consumer<Map<String, String[]>> consumer) throws InvalidSearchException {
        try (Ticker ignored = ProfiledSearchManager.startLongRunningTimer("scanWithIndexesAndSearchQuery");){
            long l = this.delegate.scan(indexes, searchQuery, requestedFields, consumer);
            return l;
        }
    }

    @Override
    public long scan(List<Index> indices, SearchQuery searchQuery, Set<String> requestedFields, Consumer<Map<String, String[]>> consumer) throws InvalidSearchException {
        try (Ticker ignored = ProfiledSearchManager.startLongRunningTimer("scanWithIndexesAndSearchQuery");){
            long l = this.delegate.scan(indices, searchQuery, requestedFields, consumer);
            return l;
        }
    }

    private static Ticker startTimer(String methodName, ISearch search) {
        return Metrics.metric((String)SEARCH_MANAGER_TIMER_NAME).withAnalytics().withInvokerPluginKey().tag(METHOD_NAME, methodName).tag(SEARCH_TYPE, search != null && search.getSearchType() != null ? search.getSearchType() : "unknown").startTimer();
    }

    private static Ticker startTimer(String methodName, SearchResults searchResults) {
        return Metrics.metric((String)SEARCH_MANAGER_TIMER_NAME).withAnalytics().withInvokerPluginKey().tag(METHOD_NAME, methodName).tag(RESULT_SIZE, searchResults != null ? searchResults.size() : 0).startTimer();
    }

    private static Ticker startLongRunningTimer(String methodName) {
        return Metrics.metric((String)SEARCH_MANAGER_TIMER_NAME).withAnalytics().withInvokerPluginKey().tag(METHOD_NAME, methodName).startLongRunningTimer();
    }
}

