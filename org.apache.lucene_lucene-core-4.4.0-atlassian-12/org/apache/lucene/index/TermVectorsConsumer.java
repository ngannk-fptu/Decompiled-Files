/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import org.apache.lucene.codecs.TermVectorsWriter;
import org.apache.lucene.index.ByteSliceReader;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.TermVectorsConsumerPerField;
import org.apache.lucene.index.TermsHash;
import org.apache.lucene.index.TermsHashConsumer;
import org.apache.lucene.index.TermsHashConsumerPerField;
import org.apache.lucene.index.TermsHashPerField;
import org.apache.lucene.store.FlushInfo;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.RamUsageEstimator;

final class TermVectorsConsumer
extends TermsHashConsumer {
    TermVectorsWriter writer;
    final DocumentsWriterPerThread docWriter;
    final DocumentsWriterPerThread.DocState docState;
    final BytesRef flushTerm = new BytesRef();
    final ByteSliceReader vectorSliceReaderPos = new ByteSliceReader();
    final ByteSliceReader vectorSliceReaderOff = new ByteSliceReader();
    boolean hasVectors;
    int numVectorFields;
    int lastDocID;
    private TermVectorsConsumerPerField[] perFields = new TermVectorsConsumerPerField[1];
    String lastVectorFieldName;

    public TermVectorsConsumer(DocumentsWriterPerThread docWriter) {
        this.docWriter = docWriter;
        this.docState = docWriter.docState;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void flush(Map<String, TermsHashConsumerPerField> fieldsToFlush, SegmentWriteState state) throws IOException {
        if (this.writer != null) {
            int numDocs = state.segmentInfo.getDocCount();
            assert (numDocs > 0);
            try {
                this.fill(numDocs);
                assert (state.segmentInfo != null);
                this.writer.finish(state.fieldInfos, numDocs);
            }
            catch (Throwable throwable) {
                IOUtils.close(this.writer);
                this.writer = null;
                this.lastDocID = 0;
                this.hasVectors = false;
                throw throwable;
            }
            IOUtils.close(this.writer);
            this.writer = null;
            this.lastDocID = 0;
            this.hasVectors = false;
        }
        for (TermsHashConsumerPerField field : fieldsToFlush.values()) {
            TermVectorsConsumerPerField perField = (TermVectorsConsumerPerField)field;
            perField.termsHashPerField.reset();
            perField.shrinkHash();
        }
    }

    void fill(int docID) throws IOException {
        while (this.lastDocID < docID) {
            this.writer.startDocument(0);
            this.writer.finishDocument();
            ++this.lastDocID;
        }
    }

    private final void initTermVectorsWriter() throws IOException {
        if (this.writer == null) {
            IOContext context = new IOContext(new FlushInfo(this.docWriter.getNumDocsInRAM(), this.docWriter.bytesUsed()));
            this.writer = this.docWriter.codec.termVectorsFormat().vectorsWriter(this.docWriter.directory, this.docWriter.getSegmentInfo(), context);
            this.lastDocID = 0;
        }
    }

    @Override
    void finishDocument(TermsHash termsHash) throws IOException {
        assert (this.docWriter.testPoint("TermVectorsTermsWriter.finishDocument start"));
        if (!this.hasVectors) {
            return;
        }
        this.initTermVectorsWriter();
        this.fill(this.docState.docID);
        this.writer.startDocument(this.numVectorFields);
        for (int i = 0; i < this.numVectorFields; ++i) {
            this.perFields[i].finishDocument();
        }
        this.writer.finishDocument();
        assert (this.lastDocID == this.docState.docID) : "lastDocID=" + this.lastDocID + " docState.docID=" + this.docState.docID;
        ++this.lastDocID;
        termsHash.reset();
        this.reset();
        assert (this.docWriter.testPoint("TermVectorsTermsWriter.finishDocument end"));
    }

    @Override
    public void abort() {
        this.hasVectors = false;
        if (this.writer != null) {
            this.writer.abort();
            this.writer = null;
        }
        this.lastDocID = 0;
        this.reset();
    }

    void reset() {
        Arrays.fill(this.perFields, null);
        this.numVectorFields = 0;
    }

    @Override
    public TermsHashConsumerPerField addField(TermsHashPerField termsHashPerField, FieldInfo fieldInfo) {
        return new TermVectorsConsumerPerField(termsHashPerField, this, fieldInfo);
    }

    void addFieldToFlush(TermVectorsConsumerPerField fieldToFlush) {
        if (this.numVectorFields == this.perFields.length) {
            int newSize = ArrayUtil.oversize(this.numVectorFields + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF);
            TermVectorsConsumerPerField[] newArray = new TermVectorsConsumerPerField[newSize];
            System.arraycopy(this.perFields, 0, newArray, 0, this.numVectorFields);
            this.perFields = newArray;
        }
        this.perFields[this.numVectorFields++] = fieldToFlush;
    }

    @Override
    void startDocument() {
        assert (this.clearLastVectorFieldName());
        this.reset();
    }

    final boolean clearLastVectorFieldName() {
        this.lastVectorFieldName = null;
        return true;
    }

    final boolean vectorFieldsInOrder(FieldInfo fi) {
        try {
            boolean bl = this.lastVectorFieldName != null ? this.lastVectorFieldName.compareTo(fi.name) < 0 : true;
            return bl;
        }
        finally {
            this.lastVectorFieldName = fi.name;
        }
    }
}

