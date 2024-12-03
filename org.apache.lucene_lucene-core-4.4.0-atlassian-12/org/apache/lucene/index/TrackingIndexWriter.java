/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;

public class TrackingIndexWriter {
    private final IndexWriter writer;
    private final AtomicLong indexingGen = new AtomicLong(1L);

    public TrackingIndexWriter(IndexWriter writer) {
        this.writer = writer;
    }

    public long updateDocument(Term t, Iterable<? extends IndexableField> d, Analyzer a) throws IOException {
        this.writer.updateDocument(t, d, a);
        return this.indexingGen.get();
    }

    public long updateDocument(Term t, Iterable<? extends IndexableField> d) throws IOException {
        this.writer.updateDocument(t, d);
        return this.indexingGen.get();
    }

    public long updateDocuments(Term t, Iterable<? extends Iterable<? extends IndexableField>> docs, Analyzer a) throws IOException {
        this.writer.updateDocuments(t, docs, a);
        return this.indexingGen.get();
    }

    public long updateDocuments(Term t, Iterable<? extends Iterable<? extends IndexableField>> docs) throws IOException {
        this.writer.updateDocuments(t, docs);
        return this.indexingGen.get();
    }

    public long deleteDocuments(Term t) throws IOException {
        this.writer.deleteDocuments(t);
        return this.indexingGen.get();
    }

    public long deleteDocuments(Term ... terms) throws IOException {
        this.writer.deleteDocuments(terms);
        return this.indexingGen.get();
    }

    public long deleteDocuments(Query q) throws IOException {
        this.writer.deleteDocuments(q);
        return this.indexingGen.get();
    }

    public long deleteDocuments(Query ... queries) throws IOException {
        this.writer.deleteDocuments(queries);
        return this.indexingGen.get();
    }

    public long deleteAll() throws IOException {
        this.writer.deleteAll();
        return this.indexingGen.get();
    }

    public long addDocument(Iterable<? extends IndexableField> d, Analyzer a) throws IOException {
        this.writer.addDocument(d, a);
        return this.indexingGen.get();
    }

    public long addDocuments(Iterable<? extends Iterable<? extends IndexableField>> docs, Analyzer a) throws IOException {
        this.writer.addDocuments(docs, a);
        return this.indexingGen.get();
    }

    public long addDocument(Iterable<? extends IndexableField> d) throws IOException {
        this.writer.addDocument(d);
        return this.indexingGen.get();
    }

    public long addDocuments(Iterable<? extends Iterable<? extends IndexableField>> docs) throws IOException {
        this.writer.addDocuments(docs);
        return this.indexingGen.get();
    }

    public long addIndexes(Directory ... dirs) throws IOException {
        this.writer.addIndexes(dirs);
        return this.indexingGen.get();
    }

    public long addIndexes(IndexReader ... readers) throws IOException {
        this.writer.addIndexes(readers);
        return this.indexingGen.get();
    }

    public long getGeneration() {
        return this.indexingGen.get();
    }

    public IndexWriter getIndexWriter() {
        return this.writer;
    }

    public long getAndIncrementGeneration() {
        return this.indexingGen.getAndIncrement();
    }

    public long tryDeleteDocument(IndexReader reader, int docID) throws IOException {
        if (this.writer.tryDeleteDocument(reader, docID)) {
            return this.indexingGen.get();
        }
        return -1L;
    }
}

