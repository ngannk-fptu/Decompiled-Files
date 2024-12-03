/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.search.v2.BatchUpdateAction;
import com.atlassian.confluence.search.v2.CustomSearchIndexRegistry;
import com.atlassian.confluence.search.v2.FieldMappings;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.ScannedDocument;
import com.atlassian.confluence.search.v2.ScoringStrategy;
import com.atlassian.confluence.search.v2.SearchIndexAccessException;
import com.atlassian.confluence.search.v2.SearchIndexAccessor;
import com.atlassian.confluence.search.v2.SearchIndexAction;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public abstract class DelegatingSearchIndexAccessor
implements SearchIndexAccessor {
    private final SearchIndexAccessor delegate;

    @Deprecated
    public DelegatingSearchIndexAccessor(CustomSearchIndexRegistry customSearchIndexRegistry, String indexName, String relativeIndexPath, ScoringStrategy scoringStrategy, AnalyzerDescriptorProvider analyzerDescriptorProvider) {
        this.delegate = customSearchIndexRegistry.add(indexName, relativeIndexPath, scoringStrategy, analyzerDescriptorProvider);
    }

    @Deprecated
    protected DelegatingSearchIndexAccessor(CustomSearchIndexRegistry customSearchIndexRegistry, String indexName, ScoringStrategy scoringStrategy, AnalyzerDescriptorProvider analyzerDescriptorProvider) {
        this.delegate = customSearchIndexRegistry.add(indexName, scoringStrategy, analyzerDescriptorProvider);
    }

    protected DelegatingSearchIndexAccessor(CustomSearchIndexRegistry customSearchIndexRegistry, String indexName, AnalyzerDescriptorProvider analyzerDescriptorProvider) {
        this.delegate = customSearchIndexRegistry.add(indexName, analyzerDescriptorProvider);
    }

    @Override
    public SearchResults search(ISearch search, Set<String> requestedFields) throws InvalidSearchException {
        return this.delegate.search(search, requestedFields);
    }

    @Override
    public long scan(SearchQuery searchQuery, Set<String> requestedFields, Consumer<Map<String, String[]>> consumer) {
        return this.delegate.scan(searchQuery, requestedFields, consumer);
    }

    @Override
    public long scan(SearchQuery searchQuery, Set<String> requestedFields, Consumer<ScannedDocument> consumer, float defaultScore) {
        return this.delegate.scan(searchQuery, requestedFields, consumer, defaultScore);
    }

    @Override
    public void execute(SearchIndexAction action) throws SearchIndexAccessException {
        this.delegate.execute(action);
    }

    @Override
    public int numDocs() throws SearchIndexAccessException {
        return this.delegate.numDocs();
    }

    @Override
    public void withBatchUpdate(BatchUpdateAction batchUpdateAction) {
        this.delegate.withBatchUpdate(batchUpdateAction);
    }

    @Override
    public void snapshot(File destinationDirectory) throws SearchIndexAccessException {
        this.delegate.snapshot(destinationDirectory);
    }

    @Override
    public void reset(Runnable replaceIndex) {
        this.delegate.reset(replaceIndex);
    }

    @Override
    public FieldMappings getFieldMappings() {
        return this.delegate.getFieldMappings();
    }
}

