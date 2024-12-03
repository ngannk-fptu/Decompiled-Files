/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.lucene.document.Document
 *  org.apache.lucene.index.IndexWriter
 *  org.apache.lucene.index.LiveIndexWriterConfig
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.internal.index.lucene.LuceneFieldVisitor;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.FieldMappings;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.LiveIndexWriterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
class LuceneSearchIndexWriter
implements SearchIndexWriter {
    private static final Logger log = LoggerFactory.getLogger(LuceneSearchIndexWriter.class);
    private final IndexWriter luceneIndexWriter;
    private final LuceneFieldVisitor luceneFieldVisitor;
    private final LuceneQueryMapper<SearchQuery> luceneQueryMapper;
    private final FieldMappings fieldMappings;

    LuceneSearchIndexWriter(IndexWriter luceneIndexWriter, LuceneFieldVisitor luceneFieldVisitor, LuceneQueryMapper<SearchQuery> luceneQueryMapper, FieldMappings fieldMappings) {
        this.luceneIndexWriter = luceneIndexWriter;
        this.luceneFieldVisitor = luceneFieldVisitor;
        this.luceneQueryMapper = luceneQueryMapper;
        this.fieldMappings = fieldMappings;
    }

    @Override
    public void add(AtlassianDocument atlassianDocument) throws IOException {
        this.fieldMappings.addDocumentFields(atlassianDocument);
        Document document = new Document();
        for (FieldDescriptor field : atlassianDocument.getFields()) {
            if (field.getValue() == null) continue;
            document.add(field.accept(this.luceneFieldVisitor));
        }
        if (!document.getFields().isEmpty()) {
            this.luceneIndexWriter.addDocument((Iterable)document);
        }
    }

    @Override
    public void delete(SearchQuery searchQuery) throws IOException {
        this.luceneIndexWriter.deleteDocuments(this.luceneQueryMapper.convertToLuceneQuery(searchQuery));
    }

    @Override
    public void deleteAll() throws IOException {
        this.luceneIndexWriter.deleteAll();
    }

    @Override
    public void preOptimize() {
        LiveIndexWriterConfig config = this.luceneIndexWriter.getConfig();
        config.setRAMBufferSizeMB((double)Integer.getInteger("confluence.reindex.ram", 48).intValue());
        config.setMaxBufferedDocs(-1);
    }

    @Override
    public void postOptimize() {
        try {
            this.luceneIndexWriter.deleteUnusedFiles();
            this.luceneIndexWriter.commit();
        }
        catch (IOException e) {
            log.error("unable to force writer to clean-up", (Throwable)e);
        }
    }
}

