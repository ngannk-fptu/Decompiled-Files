/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.index.CompositeReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.index.Term;

public abstract class BaseCompositeReader<R extends IndexReader>
extends CompositeReader {
    private final R[] subReaders;
    private final int[] starts;
    private final int maxDoc;
    private final int numDocs;
    private final List<R> subReadersList;

    protected BaseCompositeReader(R[] subReaders) {
        this.subReaders = subReaders;
        this.subReadersList = Collections.unmodifiableList(Arrays.asList(subReaders));
        this.starts = new int[subReaders.length + 1];
        int maxDoc = 0;
        int numDocs = 0;
        for (int i = 0; i < subReaders.length; ++i) {
            this.starts[i] = maxDoc;
            R r = subReaders[i];
            if ((maxDoc += ((IndexReader)r).maxDoc()) < 0) {
                throw new IllegalArgumentException("Too many documents, composite IndexReaders cannot exceed 2147483647");
            }
            numDocs += ((IndexReader)r).numDocs();
            ((IndexReader)r).registerParentReader(this);
        }
        this.starts[subReaders.length] = maxDoc;
        this.maxDoc = maxDoc;
        this.numDocs = numDocs;
    }

    @Override
    public final Fields getTermVectors(int docID) throws IOException {
        this.ensureOpen();
        int i = this.readerIndex(docID);
        return ((IndexReader)this.subReaders[i]).getTermVectors(docID - this.starts[i]);
    }

    @Override
    public final int numDocs() {
        return this.numDocs;
    }

    @Override
    public final int maxDoc() {
        return this.maxDoc;
    }

    @Override
    public final void document(int docID, StoredFieldVisitor visitor) throws IOException {
        this.ensureOpen();
        int i = this.readerIndex(docID);
        ((IndexReader)this.subReaders[i]).document(docID - this.starts[i], visitor);
    }

    @Override
    public final int docFreq(Term term) throws IOException {
        this.ensureOpen();
        int total = 0;
        for (int i = 0; i < this.subReaders.length; ++i) {
            total += ((IndexReader)this.subReaders[i]).docFreq(term);
        }
        return total;
    }

    @Override
    public final long totalTermFreq(Term term) throws IOException {
        this.ensureOpen();
        long total = 0L;
        for (int i = 0; i < this.subReaders.length; ++i) {
            long sub = ((IndexReader)this.subReaders[i]).totalTermFreq(term);
            if (sub == -1L) {
                return -1L;
            }
            total += sub;
        }
        return total;
    }

    @Override
    public final long getSumDocFreq(String field) throws IOException {
        this.ensureOpen();
        long total = 0L;
        for (R reader : this.subReaders) {
            long sub = ((IndexReader)reader).getSumDocFreq(field);
            if (sub == -1L) {
                return -1L;
            }
            total += sub;
        }
        return total;
    }

    @Override
    public final int getDocCount(String field) throws IOException {
        this.ensureOpen();
        int total = 0;
        for (R reader : this.subReaders) {
            int sub = ((IndexReader)reader).getDocCount(field);
            if (sub == -1) {
                return -1;
            }
            total += sub;
        }
        return total;
    }

    @Override
    public final long getSumTotalTermFreq(String field) throws IOException {
        this.ensureOpen();
        long total = 0L;
        for (R reader : this.subReaders) {
            long sub = ((IndexReader)reader).getSumTotalTermFreq(field);
            if (sub == -1L) {
                return -1L;
            }
            total += sub;
        }
        return total;
    }

    protected final int readerIndex(int docID) {
        if (docID < 0 || docID >= this.maxDoc) {
            throw new IllegalArgumentException("docID must be >= 0 and < maxDoc=" + this.maxDoc + " (got docID=" + docID + ")");
        }
        return ReaderUtil.subIndex(docID, this.starts);
    }

    protected final int readerBase(int readerIndex) {
        if (readerIndex < 0 || readerIndex >= this.subReaders.length) {
            throw new IllegalArgumentException("readerIndex must be >= 0 and < getSequentialSubReaders().size()");
        }
        return this.starts[readerIndex];
    }

    protected final List<? extends R> getSequentialSubReaders() {
        return this.subReadersList;
    }
}

