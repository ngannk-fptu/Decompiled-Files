/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.FieldsWriter;
import com.atlassian.lucene36.index.SegmentWriteState;
import com.atlassian.lucene36.index.StoredFieldsWriterPerThread;
import com.atlassian.lucene36.store.RAMOutputStream;
import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.RamUsageEstimator;
import java.io.IOException;

final class StoredFieldsWriter {
    FieldsWriter fieldsWriter;
    final DocumentsWriter docWriter;
    final FieldInfos fieldInfos;
    int lastDocID;
    PerDoc[] docFreeList = new PerDoc[1];
    int freeCount;
    int allocCount;

    public StoredFieldsWriter(DocumentsWriter docWriter, FieldInfos fieldInfos) {
        this.docWriter = docWriter;
        this.fieldInfos = fieldInfos;
    }

    public StoredFieldsWriterPerThread addThread(DocumentsWriter.DocState docState) throws IOException {
        return new StoredFieldsWriterPerThread(docState, this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void flush(SegmentWriteState state) throws IOException {
        if (state.numDocs > this.lastDocID) {
            this.initFieldsWriter();
            this.fill(state.numDocs);
        }
        if (this.fieldsWriter != null) {
            try {
                this.fieldsWriter.finish(state.numDocs);
                Object var3_2 = null;
            }
            catch (Throwable throwable) {
                Object var3_3 = null;
                this.fieldsWriter.close();
                this.fieldsWriter = null;
                this.lastDocID = 0;
                throw throwable;
            }
            this.fieldsWriter.close();
            this.fieldsWriter = null;
            this.lastDocID = 0;
            {
            }
        }
    }

    private synchronized void initFieldsWriter() throws IOException {
        if (this.fieldsWriter == null) {
            this.fieldsWriter = new FieldsWriter(this.docWriter.directory, this.docWriter.getSegment(), this.fieldInfos);
            this.lastDocID = 0;
        }
    }

    synchronized PerDoc getPerDoc() {
        if (this.freeCount == 0) {
            ++this.allocCount;
            if (this.allocCount > this.docFreeList.length) {
                assert (this.allocCount == 1 + this.docFreeList.length);
                this.docFreeList = new PerDoc[ArrayUtil.oversize(this.allocCount, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            }
            return new PerDoc();
        }
        return this.docFreeList[--this.freeCount];
    }

    synchronized void abort() {
        if (this.fieldsWriter != null) {
            this.fieldsWriter.abort();
            this.fieldsWriter = null;
            this.lastDocID = 0;
        }
    }

    void fill(int docID) throws IOException {
        while (this.lastDocID < docID) {
            this.fieldsWriter.skipDocument();
            ++this.lastDocID;
        }
    }

    synchronized void finishDocument(PerDoc perDoc) throws IOException {
        assert (this.docWriter.writer.testPoint("StoredFieldsWriter.finishDocument start"));
        this.initFieldsWriter();
        this.fill(perDoc.docID);
        this.fieldsWriter.flushDocument(perDoc.numStoredFields, perDoc.fdt);
        ++this.lastDocID;
        perDoc.reset();
        this.free(perDoc);
        assert (this.docWriter.writer.testPoint("StoredFieldsWriter.finishDocument end"));
    }

    synchronized void free(PerDoc perDoc) {
        assert (this.freeCount < this.docFreeList.length);
        assert (0 == perDoc.numStoredFields);
        assert (0L == perDoc.fdt.length());
        assert (0L == perDoc.fdt.getFilePointer());
        this.docFreeList[this.freeCount++] = perDoc;
    }

    class PerDoc
    extends DocumentsWriter.DocWriter {
        final DocumentsWriter.PerDocBuffer buffer;
        RAMOutputStream fdt;
        int numStoredFields;

        PerDoc() {
            this.buffer = StoredFieldsWriter.this.docWriter.newPerDocBuffer();
            this.fdt = new RAMOutputStream(this.buffer);
        }

        void reset() {
            this.fdt.reset();
            this.buffer.recycle();
            this.numStoredFields = 0;
        }

        void abort() {
            this.reset();
            StoredFieldsWriter.this.free(this);
        }

        public long sizeInBytes() {
            return this.buffer.getSizeInBytes();
        }

        public void finish() throws IOException {
            StoredFieldsWriter.this.finishDocument(this);
        }
    }
}

