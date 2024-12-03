/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.search.contentnames.Category
 *  com.atlassian.confluence.search.v2.DefaultSearch
 *  com.atlassian.confluence.search.v2.DefaultSearchResults
 *  com.atlassian.confluence.search.v2.Expandable
 *  com.atlassian.confluence.search.v2.HightlightParams
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.ISearchResultConverter
 *  com.atlassian.confluence.search.v2.Index
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchConstants
 *  com.atlassian.confluence.search.v2.SearchExpander
 *  com.atlassian.confluence.search.v2.SearchFieldMappings
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchManager$Categorizer
 *  com.atlassian.confluence.search.v2.SearchManager$EntityVersionPolicy
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.search.v2.SearchSort$Type
 *  com.atlassian.confluence.search.v2.SearchTokenExpiredException
 *  com.atlassian.confluence.search.v2.SearchWithToken
 *  com.atlassian.confluence.search.v2.lucene.SearchIndex
 *  com.atlassian.confluence.search.v2.query.ContentStatusQuery
 *  com.atlassian.confluence.search.v2.query.MatchNoDocsQuery
 *  com.atlassian.confluence.search.v2.query.SearchQueryUtils
 *  com.atlassian.confluence.search.v2.sort.FieldSort
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.primitives.Ints
 *  org.apache.commons.collections4.ListUtils
 *  org.codehaus.jackson.node.ObjectNode
 *  org.opensearch.client.opensearch.OpenSearchClient
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.opensearch.client.opensearch.core.SearchRequest
 *  org.opensearch.client.opensearch.core.SearchRequest$Builder
 *  org.opensearch.client.opensearch.core.SearchResponse
 *  org.opensearch.client.opensearch.core.search.Highlight
 *  org.opensearch.client.opensearch.core.search.Highlight$Builder
 *  org.opensearch.client.opensearch.core.search.HighlightField
 *  org.opensearch.client.opensearch.core.search.HighlighterFragmenter
 *  org.opensearch.client.opensearch.core.search.Hit
 *  org.opensearch.client.opensearch.core.search.HitsMetadata
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.opensearch;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.plugins.opensearch.DelegatingQueryMapper;
import com.atlassian.confluence.plugins.opensearch.DelegatingSortMapper;
import com.atlassian.confluence.plugins.opensearch.OpenSearchConfig;
import com.atlassian.confluence.plugins.opensearch.OpenSearchSearchResult;
import com.atlassian.confluence.plugins.opensearch.encoder.Encoder;
import com.atlassian.confluence.plugins.opensearch.encoder.HtmlEncoder;
import com.atlassian.confluence.plugins.opensearch.encoder.NoOpEncoder;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.v2.DefaultSearch;
import com.atlassian.confluence.search.v2.DefaultSearchResults;
import com.atlassian.confluence.search.v2.Expandable;
import com.atlassian.confluence.search.v2.HightlightParams;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.ISearchResultConverter;
import com.atlassian.confluence.search.v2.Index;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchConstants;
import com.atlassian.confluence.search.v2.SearchExpander;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SearchTokenExpiredException;
import com.atlassian.confluence.search.v2.SearchWithToken;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.ContentStatusQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import com.atlassian.confluence.search.v2.query.SearchQueryUtils;
import com.atlassian.confluence.search.v2.sort.FieldSort;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.collections4.ListUtils;
import org.codehaus.jackson.node.ObjectNode;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Highlight;
import org.opensearch.client.opensearch.core.search.HighlightField;
import org.opensearch.client.opensearch.core.search.HighlighterFragmenter;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSearchSearchManager
implements SearchManager {
    private static final Logger LOG = LoggerFactory.getLogger(OpenSearchSearchManager.class);
    @VisibleForTesting
    static final Set<String> ALL_FIELDS = Collections.singleton("*");
    private static final int WARN_MAX_SEARCH_WINDOW = 10000;
    private static final int MAX_FRAGMENTS = 2;
    private static final int FRAGMENT_SIZE = 160;
    private static final int NO_MATCH_EXCERPT_SIZE = 320;
    private final OpenSearchClient client;
    private final DelegatingQueryMapper queryMapper;
    private final OpenSearchConfig config;
    private final ISearchResultConverter converter;
    private final DelegatingSortMapper sortMapper;

    public OpenSearchSearchManager(OpenSearchClient client, DelegatingQueryMapper queryMapper, OpenSearchConfig config, ISearchResultConverter converter, DelegatingSortMapper sortMapper) {
        this.client = client;
        this.queryMapper = queryMapper;
        this.config = config;
        this.converter = converter;
        this.sortMapper = sortMapper;
    }

    public SearchResults search(ISearch search) throws InvalidSearchException {
        return this.search(search, Set.of("*"));
    }

    private DefaultSearchResults searchRequest(ISearch search, Query query, Set<String> requestedFields) throws IOException {
        OpenSearchSearchResult lastResult;
        SearchRequest searchRequest = this.searchRequestFor(search, query, requestedFields);
        SearchResponse response = this.client.search(searchRequest, ObjectNode.class);
        List<OpenSearchSearchResult> openSearchResults = this.mapResults(response.hits(), this.encoderFor(search));
        if (!openSearchResults.isEmpty() && !ListUtils.emptyIfNull((lastResult = openSearchResults.get(openSearchResults.size() - 1)).getSort()).isEmpty()) {
            return new DefaultSearchResults(openSearchResults, Ints.saturatedCast((long)response.hits().total().value()), lastResult.getSort());
        }
        return new DefaultSearchResults(openSearchResults, Ints.saturatedCast((long)response.hits().total().value()));
    }

    public SearchResults search(SearchWithToken search) throws SearchTokenExpiredException, InvalidSearchException {
        throw new UnsupportedOperationException();
    }

    public SearchResults search(ISearch search, Set<String> requestedFields) throws InvalidSearchException {
        if (search.getStartOffset() + search.getLimit() > 10000) {
            LOG.warn("[getStartOffset ({}) + limit ({})] exceeds {}. This is NOT recommended because of the memory costs on OpenSearch. Consider limiting this.", new Object[]{search.getStartOffset(), search.getLimit(), 10000});
        }
        Query query = this.queryMapper.mapQueryToOpenSearch((SearchQuery)SearchExpander.expandAll((Expandable)search.getQuery()));
        try {
            return this.searchRequest(search, query, requestedFields);
        }
        catch (IOException e) {
            throw new InvalidSearchException("OpenSearch request failed", (Throwable)e);
        }
    }

    public List<Searchable> searchEntities(ISearch search, SearchManager.EntityVersionPolicy versionPolicy) throws InvalidSearchException {
        return this.convertToEntities(this.search(search), versionPolicy);
    }

    public List<Searchable> convertToEntities(SearchResults searchResults, SearchManager.EntityVersionPolicy versionPolicy) {
        return this.converter.convertToEntities((Iterable)searchResults, versionPolicy);
    }

    public long scan(EnumSet<SearchIndex> indexes, SearchQuery searchQuery, Set<String> requestedFields, Consumer<Map<String, String[]>> consumer) throws InvalidSearchException {
        return this.scan(Index.from(indexes), searchQuery, requestedFields, consumer);
    }

    public long scan(List<Index> indices, SearchQuery searchQuery, Set<String> requestedFields, Consumer<Map<String, String[]>> consumer) throws InvalidSearchException {
        FieldSort searchSort = new FieldSort("_id", SearchSort.Type.STRING, SearchSort.Order.ASCENDING);
        DefaultSearch search = new DefaultSearch(indices, searchQuery, (SearchSort)searchSort, 0, SearchConstants.MAX_LIMIT);
        long count = 0L;
        SearchResults searchResults = null;
        try {
            do {
                DefaultSearch currentSearch = search;
                if (searchResults != null && !ListUtils.emptyIfNull((List)searchResults.getSearchAfter()).isEmpty()) {
                    currentSearch = search.withSearchAfter(searchResults.getSearchAfter());
                }
                searchResults = this.search((ISearch)currentSearch, requestedFields);
                this.consume(searchResults, consumer);
                count += (long)searchResults.size();
            } while (searchResults.size() > 0);
        }
        catch (InvalidSearchException e) {
            LOG.error("Error occurred while scanning the indexes", (Throwable)e);
            throw new InvalidSearchException(String.format("Error occurred while scanning the index. %d scanned", count), (Throwable)e);
        }
        return count;
    }

    private void consume(SearchResults searchResults, Consumer<Map<String, String[]>> consumer) {
        searchResults.forEach(searchResult -> {
            HashMap fieldValuesMap = new HashMap();
            searchResult.getFieldNames().forEach(fieldName -> fieldValuesMap.put(fieldName, searchResult.getFieldValues(fieldName).toArray(new String[0])));
            consumer.accept(fieldValuesMap);
        });
    }

    public <T> Map<T, List<Map<String, String>>> searchCategorised(ISearch search, SearchManager.Categorizer<T> categorizer) throws InvalidSearchException {
        Preconditions.checkArgument((search.getSort() == null ? 1 : 0) != 0, (Object)"sort is not supported");
        SearchQuery modifiedQuery = SearchQueryUtils.appendIfQueryNotPresent((SearchQuery)search.getQuery(), (SearchQuery)ContentStatusQuery.CURRENT, (SearchQuery)ContentStatusQuery.getDefaultContentStatusQuery());
        ISearch modifiedSearch = search.withQuery(modifiedQuery);
        HashMap result = new HashMap();
        Set supportedCategories = categorizer.getCategories();
        supportedCategories.stream().forEach(s -> result.put(s, new ArrayList()));
        SearchResults searchResults = this.search(modifiedSearch);
        searchResults.getAll().stream().forEach(r -> {
            Set categories = Category.getCategories((String)r.getType());
            categories.stream().filter(supportedCategories::contains).forEach(c -> {
                int numHits = categorizer.getLimit(c);
                if (((List)result.get(c)).size() < numHits) {
                    HashMap fieldValues = new HashMap();
                    OpenSearchSearchResult OSResult = (OpenSearchSearchResult)((Object)r);
                    categorizer.getFields(c).forEach(fieldName -> fieldValues.put(fieldName, OSResult.getFieldValue((String)fieldName)));
                    ((List)result.get(c)).add(fieldValues);
                }
            });
        });
        return result;
    }

    private SearchRequest searchRequestFor(ISearch search, Query query, Set<String> requestedFields) {
        SearchRequest.Builder requestBuilder = new SearchRequest.Builder();
        List indices = search.getIndices().stream().map(this.config::getIndexName).collect(Collectors.toList());
        requestBuilder.index(indices).query(query).size(Integer.valueOf(search.getLimit())).sort(this.sortMapper.mapSortToOpenSearch((SearchSort)SearchExpander.expandAll((Expandable)search.getSort()))).source(src -> src.fetch(Boolean.valueOf(false))).storedFields(new ArrayList(Optional.ofNullable(requestedFields).orElse(ALL_FIELDS))).highlight((Highlight)search.getHighlight().map(params -> this.highlightFor((HightlightParams)params, query)).orElse(null));
        if (ListUtils.emptyIfNull((List)search.getSearchAfter()).isEmpty()) {
            requestBuilder.from(Integer.valueOf(search.getStartOffset()));
        } else {
            requestBuilder.searchAfter(search.getSearchAfter());
        }
        return requestBuilder.build();
    }

    private List<OpenSearchSearchResult> mapResults(HitsMetadata<?> hits, Encoder encoder) {
        return ListUtils.emptyIfNull(hits.hits().stream().map(hit -> new OpenSearchSearchResult((Hit<?>)hit, encoder)).collect(Collectors.toList()));
    }

    private Encoder encoderFor(ISearch search) {
        return search.getHighlight().map(params -> "html".equals(params.getEncoder()) ? new HtmlEncoder() : new NoOpEncoder()).orElse(new NoOpEncoder());
    }

    private Highlight highlightFor(HightlightParams highlightParams, Query query) {
        Query highlightQuery;
        try {
            highlightQuery = highlightParams.getQuery().equals(MatchNoDocsQuery.getInstance()) ? query : this.queryMapper.mapQueryToOpenSearch((SearchQuery)SearchExpander.expandAll((Expandable)highlightParams.getQuery()));
        }
        catch (Exception e) {
            LOG.warn(String.format("Failed to convert highlight query to OpenSearch Query %s", highlightParams.getQuery()), (Throwable)e);
            return null;
        }
        return new Highlight.Builder().preTags(highlightParams.getPreTag(), new String[0]).postTags(highlightParams.getPostTag(), new String[0]).fragmenter(HighlighterFragmenter.Simple).highlightQuery(highlightQuery).fields(SearchFieldMappings.TITLE.getName(), this.highlightField(true)).fields(SearchFieldMappings.DISPLAY_TITLE.getName(), this.highlightField(true)).fields(SearchFieldMappings.CONTENT.getName(), this.highlightField(false)).build();
    }

    private HighlightField highlightField(boolean noFragment) {
        return HighlightField.of(hf -> {
            hf.noMatchSize(Integer.valueOf(320)).requireFieldMatch(Boolean.valueOf(false));
            if (noFragment) {
                hf.numberOfFragments(Integer.valueOf(0));
            } else {
                hf.numberOfFragments(Integer.valueOf(2)).fragmentSize(Integer.valueOf(160));
            }
            return hf;
        });
    }
}

