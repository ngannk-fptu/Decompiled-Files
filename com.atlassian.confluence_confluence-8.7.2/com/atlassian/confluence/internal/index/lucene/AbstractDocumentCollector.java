/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.document.Document
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.IndexableField
 *  org.apache.lucene.search.Collector
 *  org.apache.lucene.search.IndexSearcher
 */
package com.atlassian.confluence.internal.index.lucene;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;

public abstract class AbstractDocumentCollector<T>
extends Collector {
    private IndexSearcher indexSearcher;
    protected final Set<String> requestedFields;
    private final Consumer<T> consumer;
    private final AtomicLong counter;
    private int docBase;

    public AbstractDocumentCollector(IndexSearcher indexSearcher, Set<String> requestedFields, Consumer<T> consumer) {
        this.indexSearcher = indexSearcher;
        this.requestedFields = requestedFields;
        this.counter = new AtomicLong(0L);
        this.consumer = consumer;
    }

    public void setNextReader(AtomicReaderContext readerContext) {
        this.docBase = readerContext.docBase;
    }

    public boolean acceptsDocsOutOfOrder() {
        return true;
    }

    public void collect(int docID) throws IOException {
        T item = this.toConsumable(this.indexSearcher.doc(this.docBase + docID, this.requestedFields));
        this.consumer.accept(item);
        this.counter.incrementAndGet();
    }

    public abstract T toConsumable(Document var1);

    public long getCount() {
        return this.counter.get();
    }

    public void setIndexSearcher(IndexSearcher indexSearcher) {
        this.indexSearcher = indexSearcher;
    }

    protected Map<String, String[]> getRequestedFields(Document document) {
        Set fieldNames = Optional.ofNullable(this.requestedFields).orElse(document.getFields().stream().map(IndexableField::name).collect(Collectors.toSet()));
        return fieldNames.stream().collect(Collectors.toMap(fieldName -> fieldName, arg_0 -> ((Document)document).getValues(arg_0)));
    }
}

