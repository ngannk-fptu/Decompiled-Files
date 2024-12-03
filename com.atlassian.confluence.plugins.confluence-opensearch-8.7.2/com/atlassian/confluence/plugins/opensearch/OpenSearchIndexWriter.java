/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.search.v2.AtlassianDocument
 *  com.atlassian.confluence.search.v2.Expandable
 *  com.atlassian.confluence.search.v2.FieldMappings
 *  com.atlassian.confluence.search.v2.SearchExpander
 *  com.atlassian.confluence.search.v2.SearchIndexWriter
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.google.common.collect.ArrayListMultimap
 *  jakarta.json.stream.JsonGenerator
 *  org.opensearch.client.json.JsonpMapper
 *  org.opensearch.client.json.JsonpSerializable
 *  org.opensearch.client.json.JsonpUtils
 *  org.opensearch.client.json.jackson.JacksonJsonProvider
 *  org.opensearch.client.opensearch.OpenSearchClient
 *  org.opensearch.client.opensearch._types.Conflicts
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.opensearch.client.opensearch.core.DeleteByQueryRequest$Builder
 *  org.opensearch.client.opensearch.core.DeleteByQueryResponse
 *  org.opensearch.client.opensearch.core.IndexRequest
 *  org.opensearch.client.transport.OpenSearchTransport
 *  org.opensearch.client.util.ObjectBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.opensearch;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.opensearch.DelegatingQueryMapper;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.Expandable;
import com.atlassian.confluence.search.v2.FieldMappings;
import com.atlassian.confluence.search.v2.SearchExpander;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.google.common.collect.ArrayListMultimap;
import jakarta.json.stream.JsonGenerator;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.json.JsonpSerializable;
import org.opensearch.client.json.JsonpUtils;
import org.opensearch.client.json.jackson.JacksonJsonProvider;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Conflicts;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.DeleteByQueryRequest;
import org.opensearch.client.opensearch.core.DeleteByQueryResponse;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.util.ObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSearchIndexWriter
implements SearchIndexWriter {
    private static final Logger log = LoggerFactory.getLogger(OpenSearchIndexWriter.class);
    protected final OpenSearchClient client;
    protected final String indexName;
    protected final DelegatingQueryMapper queryMapperRegistry;
    protected final FieldMappings fieldMappings;

    static String toJson(JsonpSerializable obj, JsonpMapper mapper) {
        StringWriter writer = new StringWriter();
        try (JsonGenerator generator = JacksonJsonProvider.provider().createGenerator((Writer)writer);){
            JsonpUtils.serialize((Object)obj, (JsonGenerator)generator, null, (JsonpMapper)mapper);
        }
        return writer.toString();
    }

    static void tryDelete(OpenSearchClient client, String indexName, Query query) throws IOException {
        long conflictsCount = 0L;
        int attempts = 3;
        do {
            Conflicts conflicts;
            DeleteByQueryResponse response;
            if ((conflictsCount = Optional.ofNullable((response = client.deleteByQuery(arg_0 -> OpenSearchIndexWriter.lambda$tryDelete$0(indexName, query, conflicts = attempts == 1 ? Conflicts.Abort : Conflicts.Proceed, arg_0))).versionConflicts()).orElse(0L).longValue()) <= 0L) continue;
            JsonpMapper mapper = ((OpenSearchTransport)client._transport()).jsonpMapper();
            log.warn("Attempts left: {}; version conflicts: {}; index: '{}'; query: {}; response: {}", new Object[]{attempts - 1, conflictsCount, indexName, OpenSearchIndexWriter.toJson((JsonpSerializable)query, mapper), OpenSearchIndexWriter.toJson((JsonpSerializable)response, mapper)});
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        } while (conflictsCount > 0L && --attempts > 0);
    }

    public OpenSearchIndexWriter(OpenSearchClient client, String indexName, DelegatingQueryMapper queryMapper, FieldMappings fieldMappings) {
        this.client = Objects.requireNonNull(client, "client is required");
        this.indexName = Objects.requireNonNull(indexName, "indexName is required");
        this.queryMapperRegistry = queryMapper;
        this.fieldMappings = Objects.requireNonNull(fieldMappings, "fieldMappings is required");
    }

    public void add(AtlassianDocument document) throws IOException {
        this.fieldMappings.addDocumentFields(document);
        this.client.index(IndexRequest.of(i -> i.index(this.indexName).document(this.convert(document))));
    }

    protected Map<String, Collection<Object>> convert(AtlassianDocument document) {
        ArrayListMultimap multiMap = ArrayListMultimap.create();
        for (FieldDescriptor field : document.getFields()) {
            multiMap.put((Object)field.getName(), field.getRawValue());
        }
        return multiMap.asMap();
    }

    public void delete(SearchQuery searchQuery) throws IOException {
        Query query = this.queryMapperRegistry.mapQueryToOpenSearch((SearchQuery)SearchExpander.expandAll((Expandable)searchQuery));
        OpenSearchIndexWriter.tryDelete(this.client, this.indexName, query);
    }

    public void deleteAll() throws IOException {
        OpenSearchIndexWriter.tryDelete(this.client, this.indexName, Query.of(q -> q.matchAll(m -> m)));
    }

    public void preOptimize() {
    }

    public void postOptimize() {
    }

    private static /* synthetic */ ObjectBuilder lambda$tryDelete$0(String indexName, Query query, Conflicts conflicts, DeleteByQueryRequest.Builder q) {
        return q.index(indexName, new String[0]).query(query).conflicts(conflicts);
    }
}

