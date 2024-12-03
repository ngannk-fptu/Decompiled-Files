/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timer
 *  javax.annotation.Nonnull
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.IndexWriter
 *  org.apache.lucene.index.IndexWriterConfig
 *  org.apache.lucene.index.IndexableField
 *  org.apache.lucene.index.MergePolicy$OneMerge
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.store.Directory
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneIndexMetrics;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timer;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;

public class InstrumentedIndexWriter
extends IndexWriter {
    private final Timer addDocumentMetric;
    private final Timer addDocumentsMetric;
    private final Timer deleteDocumentsMetric;
    private final Timer updateDocumentMetric;
    private final Timer updateDocumentsMetric;
    private final Timer mergeDocumentsMetric;
    private final Timer deleteAllMetric;

    public InstrumentedIndexWriter(@Nonnull Directory d, @Nonnull IndexWriterConfig conf, @Nonnull LuceneIndexMetrics metrics) throws IOException {
        super(d, conf);
        this.addDocumentMetric = metrics.timer("IndexWriter", "IndexWriter.AddDocument");
        this.addDocumentsMetric = metrics.timer("IndexWriter", "IndexWriter.AddDocuments");
        this.deleteDocumentsMetric = metrics.timer("IndexWriter", "IndexWriter.DeleteDocuments");
        this.updateDocumentMetric = metrics.timer("IndexWriter", "IndexWriter.UpdateDocument");
        this.updateDocumentsMetric = metrics.timer("IndexWriter", "IndexWriter.UpdateDocuments");
        this.mergeDocumentsMetric = metrics.timer("IndexWriter", "IndexWriter.MergeDocuments");
        this.deleteAllMetric = metrics.timer("IndexWriter", "IndexWriter.DeleteAll");
    }

    public void deleteDocuments(Term term) throws IOException {
        try (Ticker ignored = this.deleteDocumentsMetric.start(new String[0]);){
            super.deleteDocuments(term);
        }
    }

    public synchronized boolean tryDeleteDocument(IndexReader readerIn, int docID) throws IOException {
        try (Ticker ignored = this.deleteDocumentsMetric.start(new String[0]);){
            boolean bl = super.tryDeleteDocument(readerIn, docID);
            return bl;
        }
    }

    public void updateDocuments(Term delTerm, Iterable<? extends Iterable<? extends IndexableField>> docs, Analyzer analyzer) throws IOException {
        Timer timer = delTerm == null ? this.addDocumentsMetric : this.updateDocumentsMetric;
        try (Ticker ignored = timer.start(new String[0]);){
            super.updateDocuments(delTerm, docs, analyzer);
        }
    }

    public void deleteDocuments(Term ... terms) throws IOException {
        try (Ticker ignored = this.deleteDocumentsMetric.start(new String[0]);){
            super.deleteDocuments(terms);
        }
    }

    public void deleteDocuments(Query query) throws IOException {
        try (Ticker ignored = this.deleteDocumentsMetric.start(new String[0]);){
            super.deleteDocuments(query);
        }
    }

    public void deleteDocuments(Query ... queries) throws IOException {
        try (Ticker ignored = this.deleteDocumentsMetric.start(new String[0]);){
            super.deleteDocuments(queries);
        }
    }

    public void updateDocument(Term term, Iterable<? extends IndexableField> doc, Analyzer analyzer) throws IOException {
        Timer timer = term == null ? this.addDocumentMetric : this.updateDocumentMetric;
        try (Ticker ignored = timer.start(new String[0]);){
            super.updateDocument(term, doc, analyzer);
        }
    }

    public void forceMerge(int maxNumSegments, boolean doWait) throws IOException {
        try (Ticker ignored = this.mergeDocumentsMetric.start(new String[0]);){
            super.forceMerge(maxNumSegments, doWait);
        }
    }

    public void forceMergeDeletes(boolean doWait) throws IOException {
        try (Ticker ignored = this.mergeDocumentsMetric.start(new String[0]);){
            super.forceMergeDeletes(doWait);
        }
    }

    public void deleteAll() throws IOException {
        try (Ticker ignored = this.deleteAllMetric.start(new String[0]);){
            super.deleteAll();
        }
    }

    public void merge(MergePolicy.OneMerge merge) throws IOException {
        try (Ticker ignored = this.mergeDocumentsMetric.start(new String[0]);){
            super.merge(merge);
        }
    }
}

