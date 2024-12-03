/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection$SearcherWithTokenAction
 *  com.atlassian.confluence.internal.search.v2.lucene.LuceneConnection
 *  com.atlassian.confluence.internal.search.v2.lucene.LuceneException
 *  org.apache.lucene.index.IndexWriter
 *  org.apache.lucene.search.Collector
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.SearcherManager
 *  org.apache.lucene.search.Sort
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.internal.index.lucene.AbstractDocumentCollector;
import com.atlassian.confluence.internal.index.lucene.FieldValuesCollector;
import com.atlassian.confluence.internal.index.lucene.LuceneFieldVisitor;
import com.atlassian.confluence.internal.index.lucene.LuceneSearchIndexWriter;
import com.atlassian.confluence.internal.index.lucene.ScoredDocumentCollector;
import com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneConnection;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneException;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneMapperNotFoundException;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchResultFactory;
import com.atlassian.confluence.internal.search.v2.lucene.SearcherWithTokenAction;
import com.atlassian.confluence.internal.search.v2.lucene.TopDocuments;
import com.atlassian.confluence.search.summary.HitHighlighter;
import com.atlassian.confluence.search.v2.BatchUpdateAction;
import com.atlassian.confluence.search.v2.DefaultSearch;
import com.atlassian.confluence.search.v2.DefaultSearchResults;
import com.atlassian.confluence.search.v2.DefaultSearchWithToken;
import com.atlassian.confluence.search.v2.FieldMappings;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.ScannedDocument;
import com.atlassian.confluence.search.v2.SearchConstants;
import com.atlassian.confluence.search.v2.SearchIndexAccessException;
import com.atlassian.confluence.search.v2.SearchIndexAccessor;
import com.atlassian.confluence.search.v2.SearchIndexAction;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SearchWithToken;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class LuceneSearchIndexAccessor
implements SearchIndexAccessor {
    private static final Logger log = LoggerFactory.getLogger(LuceneSearchIndexAccessor.class);
    private final LuceneConnection luceneConnection;
    private final LuceneFieldVisitor luceneFieldVisitor;
    private final LuceneSearchMapper luceneSearchMapper;
    private final FieldMappings fieldMappings;

    public LuceneSearchIndexAccessor(LuceneConnection luceneConnection, LuceneFieldVisitor luceneFieldVisitor, LuceneSearchMapper luceneSearchMapper, FieldMappings fieldMappings) {
        this.luceneConnection = luceneConnection;
        this.luceneFieldVisitor = luceneFieldVisitor;
        this.luceneSearchMapper = luceneSearchMapper;
        this.fieldMappings = fieldMappings;
    }

    @Override
    public SearchResults search(ISearch search, Set<String> requestedFields) throws InvalidSearchException {
        Query luceneQuery = this.toLuceneQuery(search.getQuery());
        int actualOffset = Math.min(search.getStartOffset(), SearchConstants.MAX_START_OFFSET);
        int actualLimit = Math.min(search.getLimit(), SearchConstants.MAX_LIMIT);
        SearcherWithTokenAction action = new SearcherWithTokenAction(luceneQuery, null, this.toLuceneSort(search.getSort()), actualOffset, actualLimit, requestedFields);
        action.setExplain(search.isExplain());
        TopDocuments topDocuments = (TopDocuments)this.luceneConnection.withSearcher((ILuceneConnection.SearcherWithTokenAction)action);
        LinkedList<SearchResult> searchResults = this.createSearchResults(requestedFields, topDocuments, Optional.empty(), search.isExplain());
        return new DefaultSearchResults(searchResults, topDocuments.getTotalHits(), this.getNextPageToken(search, topDocuments, actualOffset, actualLimit), Collections.emptyList(), luceneQuery.toString());
    }

    @Override
    public long scan(SearchQuery searchQuery, Set<String> requestedFields, Consumer<Map<String, String[]>> consumer) {
        FieldValuesCollector collector = new FieldValuesCollector(null, requestedFields, consumer);
        return this.scan(searchQuery, collector);
    }

    @Override
    public long scan(SearchQuery searchQuery, Set<String> requestedFields, Consumer<ScannedDocument> consumer, float defaultSCore) {
        ScoredDocumentCollector collector = new ScoredDocumentCollector(null, requestedFields, consumer, defaultSCore);
        return this.scan(searchQuery, collector);
    }

    private long scan(SearchQuery searchQuery, AbstractDocumentCollector collector) {
        SearcherManager searcherManager = this.luceneConnection.getSearcherManager();
        IndexSearcher searcher = null;
        try {
            searcher = (IndexSearcher)searcherManager.acquire();
            collector.setIndexSearcher(searcher);
            searcher.search(this.luceneSearchMapper.convertToLuceneQuery(searchQuery), null, (Collector)collector);
            long l = collector.getCount();
            return l;
        }
        catch (IOException e) {
            throw new SearchIndexAccessException("Unexpected IOException while scanning", e);
        }
        finally {
            if (searcher != null) {
                try {
                    searcherManager.release((Object)searcher);
                }
                catch (IOException ignored) {
                    log.error("Error when releasing Lucene searcher after usage");
                }
            }
        }
    }

    @Override
    public void execute(SearchIndexAction action) throws SearchIndexAccessException {
        this.luceneConnection.execute(luceneIndexWriter -> {
            try {
                action.accept(new LuceneSearchIndexWriter((IndexWriter)luceneIndexWriter, this.luceneFieldVisitor, this.luceneSearchMapper, this.fieldMappings));
            }
            catch (IOException e) {
                throw new SearchIndexAccessException("Unexpected IOException while executing an index action", e);
            }
        });
    }

    @Override
    public int numDocs() throws SearchIndexAccessException {
        try {
            return this.luceneConnection.getNumDocs();
        }
        catch (LuceneException luceneException) {
            throw new SearchIndexAccessException(luceneException.getMessage());
        }
    }

    @Override
    public void withBatchUpdate(BatchUpdateAction batchUpdateAction) {
        this.luceneConnection.withBatchUpdate(batchUpdateAction::perform);
    }

    @Override
    public void snapshot(File destinationDirectory) throws SearchIndexAccessException {
        throw new UnsupportedOperationException("SearchIndexAccessor does not support index snapshotting of the main content and change indexes.");
    }

    @Override
    public void reset(Runnable replaceIndex) {
        this.luceneConnection.reset(replaceIndex);
    }

    @Override
    public FieldMappings getFieldMappings() {
        return this.fieldMappings;
    }

    public void close() {
        this.luceneConnection.close();
    }

    private Query toLuceneQuery(SearchQuery query) throws InvalidSearchException {
        try {
            return this.luceneSearchMapper.convertToLuceneQuery(query);
        }
        catch (LuceneMapperNotFoundException e) {
            throw new InvalidSearchException(e);
        }
    }

    private Sort toLuceneSort(SearchSort sort) throws InvalidSearchException {
        try {
            return sort == null ? null : this.luceneSearchMapper.convertToLuceneSort(sort);
        }
        catch (LuceneMapperNotFoundException e) {
            throw new InvalidSearchException(e);
        }
    }

    private SearchWithToken getNextPageToken(ISearch search, TopDocuments topDocuments, int offset, int limit) {
        DefaultSearchWithToken nextPageToken = null;
        if (!topDocuments.isLastPage()) {
            ISearch nextPageSearch = this.getNextPageSearch(search, offset, limit);
            nextPageToken = new DefaultSearchWithToken(nextPageSearch, topDocuments.getSearchToken());
        }
        return nextPageToken;
    }

    private ISearch getNextPageSearch(ISearch search, int offset, int limit) {
        return new DefaultSearch(search.getSearchIndexes(), search.getQuery(), search.getSort(), offset + limit, limit);
    }

    private LinkedList<SearchResult> createSearchResults(Set<String> requestedFields, TopDocuments topDocuments, Optional<HitHighlighter> optionalHighlighter, boolean isExplain) {
        LuceneSearchResultFactory searchResultFactory = new LuceneSearchResultFactory(requestedFields);
        return IntStream.range(0, topDocuments.getDocuments().size()).mapToObj(i -> searchResultFactory.createSearchResult(topDocuments.getDocuments().get(i), optionalHighlighter, Optional.ofNullable(isExplain ? topDocuments.getExplanations().get(i) : null))).collect(Collectors.toCollection(LinkedList::new));
    }
}

