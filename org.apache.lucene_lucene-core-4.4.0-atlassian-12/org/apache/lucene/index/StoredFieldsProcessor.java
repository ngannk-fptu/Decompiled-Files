/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.StoredFieldsWriter;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.StoredFieldsConsumer;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.RamUsageEstimator;

final class StoredFieldsProcessor
extends StoredFieldsConsumer {
    StoredFieldsWriter fieldsWriter;
    final DocumentsWriterPerThread docWriter;
    int lastDocID;
    final DocumentsWriterPerThread.DocState docState;
    final Codec codec;
    private int numStoredFields;
    private IndexableField[] storedFields = new IndexableField[1];
    private FieldInfo[] fieldInfos = new FieldInfo[1];

    public StoredFieldsProcessor(DocumentsWriterPerThread docWriter) {
        this.docWriter = docWriter;
        this.docState = docWriter.docState;
        this.codec = docWriter.codec;
    }

    public void reset() {
        this.numStoredFields = 0;
        Arrays.fill(this.storedFields, null);
        Arrays.fill(this.fieldInfos, null);
    }

    @Override
    public void startDocument() {
        this.reset();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void flush(SegmentWriteState state) throws IOException {
        block7: {
            int numDocs = state.segmentInfo.getDocCount();
            if (numDocs > 0) {
                this.initFieldsWriter(state.context);
                this.fill(numDocs);
            }
            if (this.fieldsWriter != null) {
                block6: {
                    boolean success = false;
                    try {
                        this.fieldsWriter.finish(state.fieldInfos, numDocs);
                        success = true;
                        if (!success) break block6;
                    }
                    catch (Throwable throwable) {
                        if (success) {
                            IOUtils.close(this.fieldsWriter);
                        } else {
                            IOUtils.closeWhileHandlingException(this.fieldsWriter);
                        }
                        throw throwable;
                    }
                    IOUtils.close(this.fieldsWriter);
                    break block7;
                }
                IOUtils.closeWhileHandlingException(this.fieldsWriter);
            }
        }
    }

    private synchronized void initFieldsWriter(IOContext context) throws IOException {
        if (this.fieldsWriter == null) {
            this.fieldsWriter = this.codec.storedFieldsFormat().fieldsWriter(this.docWriter.directory, this.docWriter.getSegmentInfo(), context);
            this.lastDocID = 0;
        }
    }

    @Override
    void abort() {
        this.reset();
        if (this.fieldsWriter != null) {
            this.fieldsWriter.abort();
            this.fieldsWriter = null;
            this.lastDocID = 0;
        }
    }

    void fill(int docID) throws IOException {
        while (this.lastDocID < docID) {
            this.fieldsWriter.startDocument(0);
            ++this.lastDocID;
            this.fieldsWriter.finishDocument();
        }
    }

    @Override
    void finishDocument() throws IOException {
        assert (this.docWriter.testPoint("StoredFieldsWriter.finishDocument start"));
        this.initFieldsWriter(IOContext.DEFAULT);
        this.fill(this.docState.docID);
        if (this.fieldsWriter != null && this.numStoredFields > 0) {
            this.fieldsWriter.startDocument(this.numStoredFields);
            for (int i = 0; i < this.numStoredFields; ++i) {
                this.fieldsWriter.writeField(this.fieldInfos[i], this.storedFields[i]);
            }
            this.fieldsWriter.finishDocument();
            ++this.lastDocID;
        }
        this.reset();
        assert (this.docWriter.testPoint("StoredFieldsWriter.finishDocument end"));
    }

    @Override
    public void addField(int docID, IndexableField field, FieldInfo fieldInfo) {
        if (field.fieldType().stored()) {
            if (this.numStoredFields == this.storedFields.length) {
                int newSize = ArrayUtil.oversize(this.numStoredFields + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF);
                IndexableField[] newArray = new IndexableField[newSize];
                System.arraycopy(this.storedFields, 0, newArray, 0, this.numStoredFields);
                this.storedFields = newArray;
                FieldInfo[] newInfoArray = new FieldInfo[newSize];
                System.arraycopy(this.fieldInfos, 0, newInfoArray, 0, this.numStoredFields);
                this.fieldInfos = newInfoArray;
            }
            this.storedFields[this.numStoredFields] = field;
            this.fieldInfos[this.numStoredFields] = fieldInfo;
            ++this.numStoredFields;
            assert (this.docState.testPoint("StoredFieldsWriterPerThread.processFields.writeField"));
        }
    }
}

