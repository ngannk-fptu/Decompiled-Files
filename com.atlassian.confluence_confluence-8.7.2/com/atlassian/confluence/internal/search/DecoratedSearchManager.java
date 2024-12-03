/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.internal.search;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.internal.search.v2.SearchDecoratorProvider;
import com.atlassian.confluence.search.v2.HightlightParams;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.Index;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchDecorator;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SearchTokenExpiredException;
import com.atlassian.confluence.search.v2.SearchWithToken;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DecoratedSearchManager
implements SearchManager {
    private final SearchManager delegate;
    private final SearchDecoratorProvider decoratorProvider;

    public DecoratedSearchManager(SearchManager delegate, SearchDecoratorProvider decoratorProvider) {
        this.delegate = Objects.requireNonNull(delegate);
        this.decoratorProvider = Objects.requireNonNull(decoratorProvider);
    }

    @Override
    public SearchResults search(ISearch search) throws InvalidSearchException {
        return this.delegate.search(this.decorate(search));
    }

    @Override
    public SearchResults search(SearchWithToken search) throws SearchTokenExpiredException, InvalidSearchException {
        return this.delegate.search(search);
    }

    @Override
    public SearchResults search(ISearch search, Set<String> requestedFields) throws InvalidSearchException {
        return this.delegate.search(this.decorate(search), requestedFields);
    }

    @Override
    public List<Searchable> searchEntities(ISearch search, SearchManager.EntityVersionPolicy versionPolicy) throws InvalidSearchException {
        return this.delegate.searchEntities(this.decorate(search), versionPolicy);
    }

    @Override
    public List<Searchable> convertToEntities(SearchResults searchResults, SearchManager.EntityVersionPolicy versionPolicy) {
        return this.delegate.convertToEntities(searchResults, versionPolicy);
    }

    @Override
    public String explain(ISearch search, long contentId) {
        return this.delegate.explain(this.decorate(search), contentId);
    }

    @Override
    public <T> Map<T, List<Map<String, String>>> searchCategorised(ISearch search, SearchManager.Categorizer<T> categorizer) throws InvalidSearchException {
        return this.delegate.searchCategorised(this.decorate(search), categorizer);
    }

    @Override
    public long scan(EnumSet<SearchIndex> indexes, SearchQuery searchQuery, Set<String> requestedFields, Consumer<Map<String, String[]>> consumer) throws InvalidSearchException {
        return this.delegate.scan(indexes, searchQuery, requestedFields, consumer);
    }

    @Override
    public long scan(List<Index> indices, SearchQuery searchQuery, Set<String> requestedFields, Consumer<Map<String, String[]>> consumer) throws InvalidSearchException {
        return this.delegate.scan(indices, searchQuery, requestedFields, consumer);
    }

    private ISearch decorate(ISearch input) {
        if (input instanceof DecoratedSearch) {
            return input;
        }
        ISearch search = input;
        for (SearchDecorator decorator : this.decoratorProvider.get()) {
            search = decorator.decorate(search);
        }
        return new DecoratedSearch(search);
    }

    private static class DecoratedSearch
    implements ISearch {
        private final ISearch delegate;

        DecoratedSearch(ISearch delegate) {
            this.delegate = delegate;
        }

        @Override
        public @NonNull SearchQuery getQuery() {
            return this.delegate.getQuery();
        }

        @Override
        public SearchSort getSort() {
            return this.delegate.getSort();
        }

        @Override
        public int getStartOffset() {
            return this.delegate.getStartOffset();
        }

        @Override
        public int getLimit() {
            return this.delegate.getLimit();
        }

        @Override
        public String getSearchType() {
            return this.delegate.getSearchType();
        }

        @Override
        public Optional<HightlightParams> getHighlight() {
            return this.delegate.getHighlight();
        }

        @Override
        public boolean isExplain() {
            return this.delegate.isExplain();
        }

        @Override
        public EnumSet<SearchIndex> getSearchIndexes() {
            return this.delegate.getSearchIndexes();
        }

        @Override
        public List<Index> getIndices() {
            return this.delegate.getIndices();
        }

        @Override
        public List<String> getSearchAfter() {
            return this.delegate.getSearchAfter();
        }

        @Override
        public ISearch withSearchAfter(List<String> searchAfter) {
            return this.delegate.withSearchAfter(searchAfter);
        }

        @Override
        public ISearch withIndices(List<Index> indices) {
            return this.delegate.withIndices(indices);
        }

        @Override
        public ISearch withQuery(SearchQuery query) {
            return this.delegate.withQuery(query);
        }
    }
}

