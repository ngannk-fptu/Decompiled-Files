/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.internal.search.v2.lucene.SearchTokenExpiredException
 *  com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.time.StopWatch
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.document.Document
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.IndexableField
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.DocIdSetIterator
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.QueryWrapperFilter
 *  org.apache.lucene.search.ScoreDoc
 *  org.apache.lucene.search.Sort
 *  org.apache.lucene.search.TopDocs
 *  org.apache.lucene.search.highlight.Encoder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.event.events.search.V2QueryExecutionEvent;
import com.atlassian.confluence.impl.search.summary.HitHighlighterImpl;
import com.atlassian.confluence.impl.search.summary.HtmlEncoder;
import com.atlassian.confluence.impl.search.summary.NoOpEncoder;
import com.atlassian.confluence.impl.search.summary.WrappingFormatter;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneMapperNotFoundException;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchResultFactory;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneTextFieldTokenizer;
import com.atlassian.confluence.internal.search.v2.lucene.MultiConnection;
import com.atlassian.confluence.internal.search.v2.lucene.SearchTokenExpiredException;
import com.atlassian.confluence.internal.search.v2.lucene.SearcherWithTokenAction;
import com.atlassian.confluence.internal.search.v2.lucene.TopDocuments;
import com.atlassian.confluence.internal.search.v2.lucene.TopScoreDocCategorisedCollector;
import com.atlassian.confluence.internal.search.v2.lucene.analyzer.LuceneAnalyzerFactory;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.search.summary.HitHighlighter;
import com.atlassian.confluence.search.v2.DefaultSearch;
import com.atlassian.confluence.search.v2.DefaultSearchResults;
import com.atlassian.confluence.search.v2.DefaultSearchWithToken;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.Index;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResultConverter;
import com.atlassian.confluence.search.v2.SearchResultType;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SearchWithToken;
import com.atlassian.confluence.search.v2.SubClause;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentStatusQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import com.atlassian.confluence.search.v2.query.MultiTextFieldQuery;
import com.atlassian.confluence.search.v2.query.SearchQueryUtils;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.search.v2.query.TextFieldQuery;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class LuceneSearchManager
implements SearchManager {
    private static final Logger log = LoggerFactory.getLogger(LuceneSearchManager.class);
    private final LuceneSearchMapper mapper;
    private final LuceneAnalyzerFactory luceneAnalyzerFactory;
    private final MultiConnection multiConnection;
    private final EventPublisher eventPublisher;
    private final SearchResultConverter converter;

    public LuceneSearchManager(LuceneSearchMapper luceneSearchMapper, LuceneAnalyzerFactory luceneAnalyzerFactory, MultiConnection multiConnection, SearchResultConverter converter, EventPublisher eventPublisher) {
        this.mapper = Objects.requireNonNull(luceneSearchMapper);
        this.luceneAnalyzerFactory = Objects.requireNonNull(luceneAnalyzerFactory);
        this.multiConnection = Objects.requireNonNull(multiConnection);
        this.converter = Objects.requireNonNull(converter);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public SearchResults search(SearchWithToken search) throws com.atlassian.confluence.search.v2.SearchTokenExpiredException, InvalidSearchException {
        TopDocuments topDocuments;
        Preconditions.checkNotNull((Object)search, (Object)"search cannot be null.");
        Preconditions.checkArgument((search.getSearchToken() > 0L ? 1 : 0) != 0, (Object)("Invalid search token in search. Got " + search.getSearchToken() + ", expected a value greater than 0."));
        Query luceneQuery = this.appendContentStatusQueryIfQueryNotPresent(search.getQuery());
        Sort luceneSort = this.toLuceneSort(search.getSort());
        try {
            long start = System.currentTimeMillis();
            SearcherWithTokenAction action = new SearcherWithTokenAction(luceneQuery, null, luceneSort, search.getStartOffset(), search.getLimit(), null);
            action.setExplain(search.isExplain());
            topDocuments = this.multiConnection.withSearcher(search.getSearchIndexes(), search.getSearchToken(), action);
            long end = System.currentTimeMillis();
            this.eventPublisher.publish((Object)new V2QueryExecutionEvent(start, end, topDocuments.getTotalHits(), search.getStartOffset(), search.getLimit(), search.getSearchIndexes()));
        }
        catch (SearchTokenExpiredException e) {
            throw new com.atlassian.confluence.search.v2.SearchTokenExpiredException(e.getSearchToken());
        }
        Optional<HitHighlighter> optionalHighlighter = this.getHitHighlighterFor(search, luceneQuery);
        LinkedList<SearchResult> searchResults = this.createSearchResults(Collections.emptySet(), topDocuments, optionalHighlighter, search.isExplain());
        return new DefaultSearchResults(searchResults, topDocuments.getTotalHits(), this.getNextPageToken(search, topDocuments), this.getSearchWords(search), luceneQuery.toString());
    }

    @Override
    public SearchResults search(ISearch search) throws InvalidSearchException {
        return this.search(search, null);
    }

    @Override
    public SearchResults search(ISearch search, Set<String> requestedFields) throws InvalidSearchException {
        StopWatch globalWatch = StopWatch.createStarted();
        StopWatch filterPreparationWatch = StopWatch.createStarted();
        Query luceneQuery = this.appendContentStatusQueryIfQueryNotPresent(search.getQuery());
        SearcherWithTokenAction action = new SearcherWithTokenAction(luceneQuery, null, this.toLuceneSort(search.getSort()), search.getStartOffset(), search.getLimit(), requestedFields);
        action.setExplain(search.isExplain());
        filterPreparationWatch.stop();
        StopWatch documentsScanningWatch = StopWatch.createStarted();
        TopDocuments topDocuments = this.multiConnection.withSearcher(search.getSearchIndexes(), action);
        this.eventPublisher.publish((Object)new V2QueryExecutionEvent(globalWatch.getTime(), filterPreparationWatch.getTime(), documentsScanningWatch.getTime(), topDocuments.getTotalHits(), search.getStartOffset(), search.getLimit(), search.getSearchIndexes()));
        Optional<HitHighlighter> optionalHighlighter = this.getHitHighlighterFor(search, luceneQuery);
        LinkedList<SearchResult> searchResults = this.createSearchResults(requestedFields, topDocuments, optionalHighlighter, search.isExplain());
        return new DefaultSearchResults(searchResults, topDocuments.getTotalHits(), this.getNextPageToken(search, topDocuments), this.getSearchWords(search), luceneQuery.toString());
    }

    private SearchWithToken getNextPageToken(ISearch search, TopDocuments topDocuments) {
        DefaultSearchWithToken nextPageToken = null;
        if (!topDocuments.isLastPage()) {
            ISearch nextPageSearch = this.getNextPageSearch(search);
            nextPageToken = new DefaultSearchWithToken(nextPageSearch, topDocuments.getSearchToken());
        }
        return nextPageToken;
    }

    private Optional<HitHighlighter> getHitHighlighterFor(ISearch search, Query luceneQuery) {
        return search.getHighlight().flatMap(params -> {
            Query query;
            try {
                query = params.getQuery().equals(MatchNoDocsQuery.getInstance()) ? luceneQuery : this.toLuceneQuery(params.getQuery());
            }
            catch (InvalidSearchException e) {
                log.warn(String.format("Failed to convert highlight query to Lucene Query %s", params.getQuery()), (Throwable)e);
                return Optional.empty();
            }
            Object encoder = "html".equals(params.getEncoder()) ? new HtmlEncoder() : new NoOpEncoder();
            WrappingFormatter formatter = new WrappingFormatter(params.getPreTag(), params.getPostTag());
            Analyzer analyzer = this.luceneAnalyzerFactory.createAnalyzer();
            return Optional.ofNullable(query == null ? null : new HitHighlighterImpl(query, analyzer, formatter, (Encoder)encoder));
        });
    }

    private ISearch getNextPageSearch(ISearch search) {
        return new DefaultSearch(search.getSearchIndexes(), search.getQuery(), search.getSort(), search.getStartOffset() + search.getLimit(), search.getLimit());
    }

    private Query toLuceneQuery(SearchQuery query) throws InvalidSearchException {
        try {
            return this.mapper.convertToLuceneQuery(query);
        }
        catch (LuceneMapperNotFoundException e) {
            throw new InvalidSearchException(e);
        }
    }

    private Query appendContentStatusQueryIfQueryNotPresent(SearchQuery searchQuery) throws InvalidSearchException {
        SearchQuery modifiedQuery = SearchQueryUtils.appendIfQueryNotPresent(searchQuery, ContentStatusQuery.CURRENT, ContentStatusQuery.getDefaultContentStatusQuery());
        return this.toLuceneQuery(modifiedQuery);
    }

    private Sort toLuceneSort(SearchSort sort) throws InvalidSearchException {
        try {
            return sort == null ? null : this.mapper.convertToLuceneSort(sort);
        }
        catch (LuceneMapperNotFoundException e) {
            throw new InvalidSearchException(e);
        }
    }

    private LinkedList<SearchResult> createSearchResults(Set<String> requestedFields, TopDocuments topDocuments, Optional<HitHighlighter> optionalHighlighter, boolean isExplain) {
        LuceneSearchResultFactory searchResultFactory = new LuceneSearchResultFactory(requestedFields);
        return IntStream.range(0, topDocuments.getDocuments().size()).mapToObj(i -> searchResultFactory.createSearchResult(topDocuments.getDocuments().get(i), optionalHighlighter, Optional.ofNullable(isExplain ? topDocuments.getExplanations().get(i) : null))).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<Searchable> searchEntities(ISearch search, SearchManager.EntityVersionPolicy versionPolicy) throws InvalidSearchException {
        return this.convertToEntities(this.search(search, Sets.newHashSet((Object[])new String[]{SearchFieldNames.HANDLE, SearchFieldNames.CONTENT_VERSION})), versionPolicy);
    }

    @Override
    public List<Searchable> convertToEntities(SearchResults searchResults, SearchManager.EntityVersionPolicy versionPolicy) {
        return this.converter.convertToEntities(searchResults, versionPolicy);
    }

    static String getRawQuery(SearchQuery query) {
        if (query instanceof TextFieldQuery) {
            return ((TextFieldQuery)query).getRawQuery();
        }
        if (query instanceof MultiTextFieldQuery) {
            return ((MultiTextFieldQuery)query).getQuery();
        }
        return query.getSubClauses().map(SubClause::getClause).map(LuceneSearchManager::getRawQuery).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @VisibleForTesting
    List<String> getSearchWords(ISearch search) {
        return new LuceneTextFieldTokenizer(this.luceneAnalyzerFactory).tokenize(SearchFieldNames.CONTENT, LuceneSearchManager.getRawQuery(search.getQuery()));
    }

    @Override
    public <T> Map<T, List<Map<String, String>>> searchCategorised(ISearch search, SearchManager.Categorizer<T> categorizer) throws InvalidSearchException {
        Preconditions.checkArgument((search.getSort() == null ? 1 : 0) != 0, (Object)"sort is not supported");
        Query luceneQuery = this.appendContentStatusQueryIfQueryNotPresent(search.getQuery());
        Map resultFactoryByCategory = categorizer.getCategories().stream().collect(Collectors.toMap(Function.identity(), x -> new ResultFactory(categorizer.getFields(x))));
        HashMap result = new HashMap();
        this.multiConnection.withSearch(search.getSearchIndexes(), searcher -> {
            TopScoreDocCategorisedCollector<Object> collector = new TopScoreDocCategorisedCollector<Object>(categorizer);
            searcher.search(luceneQuery, null, collector);
            collector.forEach((category, scoreDocs) -> result.put(category, ((ResultFactory)resultFactoryByCategory.get(category)).create(searcher, (ScoreDoc[])scoreDocs)));
        });
        return result;
    }

    @Override
    public String explain(ISearch search, long contentId) {
        AtomicReference<String> result = new AtomicReference<String>("");
        this.multiConnection.withSearch(search.getSearchIndexes(), indexSearcher -> {
            try {
                SearchQuery contentIdQueryFilter = BooleanQuery.orQuery(new TermQuery(SearchFieldNames.HANDLE, Page.class.getName() + "-" + contentId), new TermQuery(SearchFieldNames.HANDLE, BlogPost.class.getName() + "-" + contentId));
                SearchQuery completeQueryFilter = BooleanQuery.builder().addFilters((Set<SearchQuery>)ImmutableSet.of((Object)contentIdQueryFilter, (Object)new TermQuery(SearchFieldNames.DOCUMENT_TYPE, SearchResultType.CONTENT.toString()))).build();
                TopDocs topDocs = indexSearcher.search(this.toLuceneQuery(completeQueryFilter), 1);
                if (topDocs != null && topDocs.totalHits == 1) {
                    result.set(indexSearcher.explain(this.toLuceneQuery(search.getQuery()), topDocs.scoreDocs[0].doc).toString());
                }
            }
            catch (InvalidSearchException e) {
                throw new RuntimeException(e);
            }
        });
        return result.get();
    }

    @Override
    public long scan(EnumSet<SearchIndex> indexes, SearchQuery searchQuery, Set<String> requestedFields, Consumer<Map<String, String[]>> consumer) throws InvalidSearchException {
        return this.internalScan(indexes, (Filter)new QueryWrapperFilter(this.appendContentStatusQueryIfQueryNotPresent(searchQuery)), requestedFields, consumer);
    }

    @Override
    public long scan(List<Index> indices, SearchQuery searchQuery, Set<String> requestedFields, Consumer<Map<String, String[]>> consumer) throws InvalidSearchException {
        EnumSet<SearchIndex> searchIndexes = EnumSet.copyOf(indices.stream().map(SearchIndex::fromIndex).collect(Collectors.toSet()));
        return this.scan(searchIndexes, searchQuery, requestedFields, consumer);
    }

    private long internalScan(EnumSet<SearchIndex> indexes, Filter filter, Set<String> requestedFields, Consumer<Map<String, String[]>> consumer) {
        AtomicLong counter = new AtomicLong();
        this.multiConnection.withReader(indexes, reader -> {
            for (AtomicReaderContext context : reader.leaves()) {
                DocIdSetIterator iterator;
                DocIdSet set = filter.getDocIdSet(context, context.reader().getLiveDocs());
                if (set == null || (iterator = set.iterator()) == null) continue;
                while (iterator.nextDoc() != Integer.MAX_VALUE) {
                    Document doc;
                    int docId = iterator.docID();
                    if (requestedFields == null) {
                        doc = context.reader().document(docId);
                        consumer.accept(doc.getFields().stream().map(IndexableField::name).collect(Collectors.toMap(fieldName -> fieldName, arg_0 -> ((Document)doc).getValues(arg_0))));
                    } else {
                        doc = context.reader().document(docId, requestedFields);
                        consumer.accept(requestedFields.stream().collect(Collectors.toMap(fieldName -> fieldName, arg_0 -> ((Document)doc).getValues(arg_0))));
                    }
                    counter.incrementAndGet();
                }
            }
            return null;
        });
        return counter.get();
    }

    private static class FieldsLoader {
        private final Set<String> fields;

        FieldsLoader(Set<String> fields) {
            this.fields = fields;
        }

        Map<String, String> load(IndexSearcher indexSearcher, int docId) {
            try {
                Document document = indexSearcher.doc(docId, FieldsLoader.getFieldToLoad(this.fields));
                HashMap<String, String> result = new HashMap<String, String>();
                this.fields.forEach(x -> {
                    if (document.get(x) != null) {
                        result.put((String)x, document.get(x));
                    }
                });
                return result;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private static Set<String> getFieldToLoad(Set<String> requestedFields) {
            if (requestedFields == null || requestedFields.isEmpty()) {
                return null;
            }
            return requestedFields;
        }
    }

    private static class ResultFactory {
        private final FieldsLoader fieldsLoader;

        ResultFactory(Set<String> fields) {
            this.fieldsLoader = new FieldsLoader(fields);
        }

        List<Map<String, String>> create(IndexSearcher indexSearcher, ScoreDoc[] scoreDocs) {
            return Stream.of(scoreDocs).map(x -> x.doc).map(x -> this.fieldsLoader.load(indexSearcher, (int)x)).collect(Collectors.toList());
        }
    }
}

