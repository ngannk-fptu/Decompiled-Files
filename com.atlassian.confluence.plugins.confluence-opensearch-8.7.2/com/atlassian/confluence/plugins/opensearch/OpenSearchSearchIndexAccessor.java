/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.BatchUpdateAction
 *  com.atlassian.confluence.search.v2.FieldMappings
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.Index
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.ScannedDocument
 *  com.atlassian.confluence.search.v2.SearchIndexAccessException
 *  com.atlassian.confluence.search.v2.SearchIndexAccessor
 *  com.atlassian.confluence.search.v2.SearchIndexAction
 *  com.atlassian.confluence.search.v2.SearchIndexWriter
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResults
 *  org.opensearch.client.opensearch.OpenSearchClient
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.opensearch;

import com.atlassian.confluence.plugins.opensearch.DelegatingQueryMapper;
import com.atlassian.confluence.plugins.opensearch.OpenSearchBulkIndexWriter;
import com.atlassian.confluence.plugins.opensearch.OpenSearchConfig;
import com.atlassian.confluence.plugins.opensearch.OpenSearchIndexWriter;
import com.atlassian.confluence.plugins.opensearch.OpenSearchSearchManager;
import com.atlassian.confluence.search.v2.BatchUpdateAction;
import com.atlassian.confluence.search.v2.FieldMappings;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.Index;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.ScannedDocument;
import com.atlassian.confluence.search.v2.SearchIndexAccessException;
import com.atlassian.confluence.search.v2.SearchIndexAccessor;
import com.atlassian.confluence.search.v2.SearchIndexAction;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSearchSearchIndexAccessor
implements SearchIndexAccessor {
    static final Logger LOG = LoggerFactory.getLogger(OpenSearchSearchIndexAccessor.class);
    private final Index index;
    private final OpenSearchClient client;
    private final FieldMappings fieldMappings;
    private final OpenSearchConfig openSearchConfig;
    private final OpenSearchIndexWriter writer;
    private final ThreadLocal<OpenSearchIndexWriter> currentWriter = new ThreadLocal();
    private final OpenSearchSearchManager searchManager;

    public OpenSearchSearchIndexAccessor(Index index, OpenSearchClient client, FieldMappings fieldMappings, OpenSearchSearchManager searchManager, DelegatingQueryMapper queryMapper, OpenSearchConfig openSearchConfig) {
        this.index = index;
        this.client = client;
        this.fieldMappings = fieldMappings;
        this.searchManager = searchManager;
        this.openSearchConfig = Objects.requireNonNull(openSearchConfig);
        this.writer = new OpenSearchIndexWriter(client, openSearchConfig.getIndexName(index), queryMapper, fieldMappings);
    }

    public SearchResults search(ISearch search, Set<String> requestedFields) throws InvalidSearchException {
        return this.searchManager.search(search.withIndices(List.of(this.index)), requestedFields);
    }

    public long scan(SearchQuery searchQuery, Set<String> requestedFields, Consumer<Map<String, String[]>> consumer) {
        try {
            return this.searchManager.scan(List.of(this.index), searchQuery, requestedFields, consumer);
        }
        catch (InvalidSearchException e) {
            throw new SearchIndexAccessException(String.format("Error occurred while scanning %s", this.index), (Throwable)e);
        }
    }

    public long scan(SearchQuery searchQuery, Set<String> requestedFields, Consumer<ScannedDocument> consumer, float defaultScore) {
        return this.scan(searchQuery, requestedFields, fieldToValuesMap -> consumer.accept(new ScannedDocument(defaultScore, fieldToValuesMap)));
    }

    public void execute(SearchIndexAction action) throws SearchIndexAccessException {
        try {
            action.accept((SearchIndexWriter)this.getIndexWriter());
        }
        catch (IOException e) {
            throw new SearchIndexAccessException("Unexpected IOException while executing an index action", (Throwable)e);
        }
    }

    public int numDocs() throws SearchIndexAccessException {
        try {
            return (int)this.client.count().count();
        }
        catch (IOException e) {
            throw new SearchIndexAccessException("Unexpected IOException while executing index count", (Throwable)e);
        }
    }

    public void withBatchUpdate(BatchUpdateAction batchUpdateAction) {
        OpenSearchBulkIndexWriter bulkIndexWriter = new OpenSearchBulkIndexWriter(this.writer, this.openSearchConfig.getBulkApiBatchSize());
        this.currentWriter.set(bulkIndexWriter);
        try {
            batchUpdateAction.perform();
            bulkIndexWriter.flush();
        }
        catch (Exception e) {
            throw new SearchIndexAccessException("Unexpected Exception while executing a batched index action", (Throwable)e);
        }
        finally {
            this.currentWriter.remove();
        }
    }

    public void snapshot(File destinationDirectory) throws SearchIndexAccessException {
        throw new UnsupportedOperationException("SearchIndexAccessor does not support index snapshotting of the main content and change indexes.");
    }

    public void reset(Runnable replaceIndex) {
        replaceIndex.run();
    }

    public FieldMappings getFieldMappings() {
        return this.fieldMappings;
    }

    private OpenSearchIndexWriter getIndexWriter() {
        OpenSearchIndexWriter writer = this.currentWriter.get();
        if (writer != null) {
            return writer;
        }
        return this.writer;
    }
}

