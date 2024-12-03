/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.search.v2.AtlassianDocument
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  jakarta.json.stream.JsonGenerator
 *  org.apache.commons.collections4.IteratorUtils
 *  org.opensearch.client.json.JsonpMapper
 *  org.opensearch.client.json.NdJsonpSerializable
 *  org.opensearch.client.json.jackson.JacksonJsonpMapper
 *  org.opensearch.client.opensearch.core.BulkRequest
 *  org.opensearch.client.opensearch.core.BulkResponse
 *  org.opensearch.client.opensearch.core.bulk.BulkOperation
 *  org.opensearch.client.opensearch.core.bulk.IndexOperation$Builder
 */
package com.atlassian.confluence.plugins.opensearch;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.plugins.opensearch.OpenSearchIndexWriter;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.SearchQuery;
import jakarta.json.stream.JsonGenerator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.IteratorUtils;
import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.json.NdJsonpSerializable;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.opensearch.client.opensearch.core.bulk.BulkOperation;
import org.opensearch.client.opensearch.core.bulk.IndexOperation;

public class OpenSearchBulkIndexWriter
extends OpenSearchIndexWriter {
    private final List<BulkOperation> operations = new ArrayList<BulkOperation>();
    private final int batchSize;
    private int currentBatchSize = 0;
    private final JsonpMapper jsonpMapper = new JacksonJsonpMapper();

    public OpenSearchBulkIndexWriter(OpenSearchIndexWriter baseWriter, int batchSize) {
        super(Objects.requireNonNull(baseWriter).client, baseWriter.indexName, baseWriter.queryMapperRegistry, baseWriter.fieldMappings);
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Negative batch size: " + batchSize);
        }
        this.batchSize = batchSize;
    }

    @Override
    public synchronized void add(AtlassianDocument document) throws IOException {
        this.fieldMappings.addDocumentFields(document);
        BulkOperation bulkOp = BulkOperation.of(b -> b.index(i -> ((IndexOperation.Builder)i.index(this.indexName)).document(this.convert(document))));
        int bulkOpSize = this.calcBulkOpSize(bulkOp);
        if (this.currentBatchSize + bulkOpSize > this.batchSize) {
            this.flush();
        }
        this.operations.add(bulkOp);
        this.currentBatchSize += bulkOpSize;
    }

    @Override
    public void delete(SearchQuery searchQuery) throws IOException {
        this.flush();
        super.delete(searchQuery);
    }

    @Override
    public void deleteAll() throws IOException {
        this.flush();
        super.deleteAll();
    }

    @VisibleForTesting
    private synchronized int calcBulkOpSize(BulkOperation bulkOp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        JsonGenerator generator = this.jsonpMapper.jsonProvider().createGenerator((OutputStream)byteArrayOutputStream);
        LinkedList<BulkOperation> items = new LinkedList<BulkOperation>(Collections.singletonList(bulkOp));
        while (!items.isEmpty()) {
            BulkOperation opItem = items.pop();
            if (opItem instanceof NdJsonpSerializable) {
                items.addAll(IteratorUtils.toList((Iterator)((NdJsonpSerializable)opItem)._serializables()).stream().filter(o -> !o.equals(opItem)).collect(Collectors.toList()));
            }
            this.jsonpMapper.serialize((Object)opItem, generator);
        }
        generator.close();
        return byteArrayOutputStream.size();
    }

    public synchronized void flush() throws IOException {
        if (!this.operations.isEmpty()) {
            try {
                BulkResponse bulkResponse = this.client.bulk(BulkRequest.of(b -> b.operations(new ArrayList<BulkOperation>(this.operations))));
                if (bulkResponse.errors()) {
                    String errorMessage = bulkResponse.items().stream().filter(item -> Objects.nonNull(item.error())).map(item -> String.format("index %s - %s: %s", item.index(), item.error().type(), item.error().reason())).collect(Collectors.joining(", "));
                    throw new IOException("Errors occurred while processing bulk request: " + errorMessage);
                }
            }
            finally {
                this.operations.clear();
                this.currentBatchSize = 0;
            }
        }
    }
}

