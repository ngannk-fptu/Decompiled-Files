/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.business.insights.api.LogRecord
 *  com.atlassian.business.insights.api.exceptions.EntityFetchingException
 *  com.atlassian.business.insights.core.extract.EntityPage
 *  com.atlassian.business.insights.core.extract.EntityPageIterator
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchManager$EntityVersionPolicy
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.lucene.SearchIndex
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.business.insights.confluence.prefetch;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.bonnie.Searchable;
import com.atlassian.business.insights.api.LogRecord;
import com.atlassian.business.insights.api.exceptions.EntityFetchingException;
import com.atlassian.business.insights.confluence.prefetch.DocIdsHolder;
import com.atlassian.business.insights.confluence.prefetch.EntityPrefetchProvider;
import com.atlassian.business.insights.core.extract.EntityPage;
import com.atlassian.business.insights.core.extract.EntityPageIterator;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SearchQueryBasedEntityPrefetchProvider
implements EntityPrefetchProvider {
    private final SearchManager searchManager;
    private final TransactionTemplate transactionTemplate;

    public SearchQueryBasedEntityPrefetchProvider(@Nonnull SearchManager searchManager, @Nonnull TransactionTemplate transactionTemplate) {
        this.searchManager = Objects.requireNonNull(searchManager, "searchManager must not be null");
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate, "transactionTemplate must not be null");
    }

    @Override
    @Nonnull
    public Stream<LogRecord> prefetchAndConvert(int maxPageSize, @Nonnull SearchQuery searchQuery, @Nonnull Function<Searchable, LogRecord> logConverterFn) {
        int pageSize = Math.min(maxPageSize, 500);
        DocIdsHolder idsHolder = this.prefetchDocIds(searchQuery);
        Iterable entityIterable = () -> new EntityPageIterator(pageSize, (offset, limit) -> this.fetchPage(idsHolder, (int)offset, (int)limit, logConverterFn));
        return StreamSupport.stream(entityIterable.spliterator(), false).map(EntityPage::getValues).flatMap(Collection::stream);
    }

    @Override
    @Nonnull
    public DocIdsHolder prefetchDocIds(@Nonnull SearchQuery searchQuery) {
        DocIdsHolder docIdsHolder = new DocIdsHolder();
        try {
            this.searchManager.scan(EnumSet.of(SearchIndex.CONTENT), searchQuery, (Set)Sets.newHashSet((Iterable)ImmutableList.of((Object)"handle")), resultFieldsMap -> docIdsHolder.addDocId(this.getDocIdFromScanResult((Map<String, String[]>)resultFieldsMap)));
        }
        catch (InvalidSearchException e) {
            throw new EntityFetchingException((Throwable)e);
        }
        return docIdsHolder;
    }

    @Override
    @Nonnull
    public ContentSearch contentSearch(@Nonnull SearchQuery searchQuery, @Nullable SearchSort searchSort, int offset, int limit) {
        return new ContentSearch(searchQuery, searchSort, offset, limit);
    }

    @Nonnull
    @VisibleForTesting
    List<LogRecord> fetchPage(DocIdsHolder docIdsHolder, int offset, int limit, Function<Searchable, LogRecord> logConverterFn) {
        if (offset > docIdsHolder.size() - 1) {
            return Collections.emptyList();
        }
        return (List)this.transactionTemplate.execute(() -> {
            SearchQuery filter = (SearchQuery)new BooleanQuery.Builder().addFilter(this.constructIdsFilter(docIdsHolder, offset, limit)).build();
            try {
                List searchableList = this.searchManager.searchEntities((ISearch)new ContentSearch(filter, null, 0, limit), SearchManager.EntityVersionPolicy.INDEXED_VERSION);
                return searchableList.stream().map(logConverterFn).filter(Objects::nonNull).collect(Collectors.toList());
            }
            catch (InvalidSearchException e) {
                throw new EntityFetchingException((Throwable)e);
            }
        });
    }

    @Nonnull
    private SearchQuery constructIdsFilter(@Nonnull DocIdsHolder docIdsHolder, int offset, int limit) {
        List docIdsTermQueries = docIdsHolder.getIds(offset, limit).stream().map(id -> new TermQuery("handle", id)).collect(Collectors.toList());
        return (SearchQuery)new BooleanQuery.Builder().addShould(docIdsTermQueries).build();
    }

    private String getDocIdFromScanResult(Map<String, String[]> scanResult) {
        return scanResult.get("handle")[0];
    }
}

