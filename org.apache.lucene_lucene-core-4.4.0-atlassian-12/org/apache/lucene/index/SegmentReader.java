/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SegmentCoreReaders;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.util.Bits;

public final class SegmentReader
extends AtomicReader {
    private final SegmentInfoPerCommit si;
    private final Bits liveDocs;
    private final int numDocs;
    final SegmentCoreReaders core;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SegmentReader(SegmentInfoPerCommit si, int termInfosIndexDivisor, IOContext context) throws IOException {
        this.si = si;
        this.core = new SegmentCoreReaders(this, si.info.dir, si, context, termInfosIndexDivisor);
        boolean success = false;
        try {
            if (si.hasDeletions()) {
                this.liveDocs = si.info.getCodec().liveDocsFormat().readLiveDocs(this.directory(), si, new IOContext(IOContext.READ, true));
            } else {
                assert (si.getDelCount() == 0);
                this.liveDocs = null;
            }
            this.numDocs = si.info.getDocCount() - si.getDelCount();
            success = true;
        }
        finally {
            if (!success) {
                this.core.decRef();
            }
        }
    }

    SegmentReader(SegmentInfoPerCommit si, SegmentCoreReaders core, IOContext context) throws IOException {
        this(si, core, si.info.getCodec().liveDocsFormat().readLiveDocs(si.info.dir, si, context), si.info.getDocCount() - si.getDelCount());
    }

    SegmentReader(SegmentInfoPerCommit si, SegmentCoreReaders core, Bits liveDocs, int numDocs) {
        this.si = si;
        this.core = core;
        core.incRef();
        assert (liveDocs != null);
        this.liveDocs = liveDocs;
        this.numDocs = numDocs;
    }

    @Override
    public Bits getLiveDocs() {
        this.ensureOpen();
        return this.liveDocs;
    }

    @Override
    protected void doClose() throws IOException {
        this.core.decRef();
    }

    @Override
    public FieldInfos getFieldInfos() {
        this.ensureOpen();
        return this.core.fieldInfos;
    }

    public StoredFieldsReader getFieldsReader() {
        this.ensureOpen();
        return this.core.fieldsReaderLocal.get();
    }

    @Override
    public void document(int docID, StoredFieldVisitor visitor) throws IOException {
        this.checkBounds(docID);
        this.getFieldsReader().visitDocument(docID, visitor);
    }

    @Override
    public Fields fields() {
        this.ensureOpen();
        return this.core.fields;
    }

    @Override
    public int numDocs() {
        return this.numDocs;
    }

    @Override
    public int maxDoc() {
        return this.si.info.getDocCount();
    }

    public TermVectorsReader getTermVectorsReader() {
        this.ensureOpen();
        return this.core.termVectorsLocal.get();
    }

    @Override
    public Fields getTermVectors(int docID) throws IOException {
        TermVectorsReader termVectorsReader = this.getTermVectorsReader();
        if (termVectorsReader == null) {
            return null;
        }
        this.checkBounds(docID);
        return termVectorsReader.get(docID);
    }

    private void checkBounds(int docID) {
        if (docID < 0 || docID >= this.maxDoc()) {
            throw new IndexOutOfBoundsException("docID must be >= 0 and < maxDoc=" + this.maxDoc() + " (got docID=" + docID + ")");
        }
    }

    public String toString() {
        return this.si.toString(this.si.info.dir, this.si.info.getDocCount() - this.numDocs - this.si.getDelCount());
    }

    public String getSegmentName() {
        return this.si.info.name;
    }

    public SegmentInfoPerCommit getSegmentInfo() {
        return this.si;
    }

    public Directory directory() {
        return this.si.info.dir;
    }

    @Override
    public Object getCoreCacheKey() {
        return this.core;
    }

    @Override
    public Object getCombinedCoreAndDeletesKey() {
        return this;
    }

    public int getTermInfosIndexDivisor() {
        return this.core.termsIndexDivisor;
    }

    @Override
    public NumericDocValues getNumericDocValues(String field) throws IOException {
        this.ensureOpen();
        return this.core.getNumericDocValues(field);
    }

    @Override
    public BinaryDocValues getBinaryDocValues(String field) throws IOException {
        this.ensureOpen();
        return this.core.getBinaryDocValues(field);
    }

    @Override
    public SortedDocValues getSortedDocValues(String field) throws IOException {
        this.ensureOpen();
        return this.core.getSortedDocValues(field);
    }

    @Override
    public SortedSetDocValues getSortedSetDocValues(String field) throws IOException {
        this.ensureOpen();
        return this.core.getSortedSetDocValues(field);
    }

    @Override
    public NumericDocValues getNormValues(String field) throws IOException {
        this.ensureOpen();
        return this.core.getNormValues(field);
    }

    public void addCoreClosedListener(CoreClosedListener listener) {
        this.ensureOpen();
        this.core.addCoreClosedListener(listener);
    }

    public void removeCoreClosedListener(CoreClosedListener listener) {
        this.ensureOpen();
        this.core.removeCoreClosedListener(listener);
    }

    public static interface CoreClosedListener {
        public void onClose(SegmentReader var1);
    }
}

