/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.index.BinaryDocValuesWriter;
import org.apache.lucene.index.DocValuesWriter;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.NumericDocValuesWriter;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.SortedDocValuesWriter;
import org.apache.lucene.index.SortedSetDocValuesWriter;
import org.apache.lucene.index.StoredFieldsConsumer;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.IOUtils;

final class DocValuesProcessor
extends StoredFieldsConsumer {
    private final Map<String, DocValuesWriter> writers = new HashMap<String, DocValuesWriter>();
    private final Counter bytesUsed;

    public DocValuesProcessor(Counter bytesUsed) {
        this.bytesUsed = bytesUsed;
    }

    @Override
    void startDocument() {
    }

    @Override
    void finishDocument() {
    }

    @Override
    public void addField(int docID, IndexableField field, FieldInfo fieldInfo) {
        FieldInfo.DocValuesType dvType = field.fieldType().docValueType();
        if (dvType != null) {
            fieldInfo.setDocValuesType(dvType);
            if (dvType == FieldInfo.DocValuesType.BINARY) {
                this.addBinaryField(fieldInfo, docID, field.binaryValue());
            } else if (dvType == FieldInfo.DocValuesType.SORTED) {
                this.addSortedField(fieldInfo, docID, field.binaryValue());
            } else if (dvType == FieldInfo.DocValuesType.SORTED_SET) {
                this.addSortedSetField(fieldInfo, docID, field.binaryValue());
            } else if (dvType == FieldInfo.DocValuesType.NUMERIC) {
                if (!(field.numericValue() instanceof Long)) {
                    throw new IllegalArgumentException("illegal type " + field.numericValue().getClass() + ": DocValues types must be Long");
                }
                this.addNumericField(fieldInfo, docID, field.numericValue().longValue());
            } else assert (false) : "unrecognized DocValues.Type: " + (Object)((Object)dvType);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void flush(SegmentWriteState state) throws IOException {
        block7: {
            if (!this.writers.isEmpty()) {
                DocValuesConsumer dvConsumer;
                block6: {
                    DocValuesFormat fmt = state.segmentInfo.getCodec().docValuesFormat();
                    dvConsumer = fmt.fieldsConsumer(state);
                    boolean success = false;
                    try {
                        for (DocValuesWriter writer : this.writers.values()) {
                            writer.finish(state.segmentInfo.getDocCount());
                            writer.flush(state, dvConsumer);
                        }
                        this.writers.clear();
                        success = true;
                        if (!success) break block6;
                    }
                    catch (Throwable throwable) {
                        if (success) {
                            IOUtils.close(dvConsumer);
                        } else {
                            IOUtils.closeWhileHandlingException(dvConsumer);
                        }
                        throw throwable;
                    }
                    IOUtils.close(dvConsumer);
                    break block7;
                }
                IOUtils.closeWhileHandlingException(dvConsumer);
            }
        }
    }

    void addBinaryField(FieldInfo fieldInfo, int docID, BytesRef value) {
        BinaryDocValuesWriter binaryWriter;
        DocValuesWriter writer = this.writers.get(fieldInfo.name);
        if (writer == null) {
            binaryWriter = new BinaryDocValuesWriter(fieldInfo, this.bytesUsed);
            this.writers.put(fieldInfo.name, binaryWriter);
        } else {
            if (!(writer instanceof BinaryDocValuesWriter)) {
                throw new IllegalArgumentException("Incompatible DocValues type: field \"" + fieldInfo.name + "\" changed from " + this.getTypeDesc(writer) + " to binary");
            }
            binaryWriter = (BinaryDocValuesWriter)writer;
        }
        binaryWriter.addValue(docID, value);
    }

    void addSortedField(FieldInfo fieldInfo, int docID, BytesRef value) {
        SortedDocValuesWriter sortedWriter;
        DocValuesWriter writer = this.writers.get(fieldInfo.name);
        if (writer == null) {
            sortedWriter = new SortedDocValuesWriter(fieldInfo, this.bytesUsed);
            this.writers.put(fieldInfo.name, sortedWriter);
        } else {
            if (!(writer instanceof SortedDocValuesWriter)) {
                throw new IllegalArgumentException("Incompatible DocValues type: field \"" + fieldInfo.name + "\" changed from " + this.getTypeDesc(writer) + " to sorted");
            }
            sortedWriter = (SortedDocValuesWriter)writer;
        }
        sortedWriter.addValue(docID, value);
    }

    void addSortedSetField(FieldInfo fieldInfo, int docID, BytesRef value) {
        SortedSetDocValuesWriter sortedSetWriter;
        DocValuesWriter writer = this.writers.get(fieldInfo.name);
        if (writer == null) {
            sortedSetWriter = new SortedSetDocValuesWriter(fieldInfo, this.bytesUsed);
            this.writers.put(fieldInfo.name, sortedSetWriter);
        } else {
            if (!(writer instanceof SortedSetDocValuesWriter)) {
                throw new IllegalArgumentException("Incompatible DocValues type: field \"" + fieldInfo.name + "\" changed from " + this.getTypeDesc(writer) + " to sorted");
            }
            sortedSetWriter = (SortedSetDocValuesWriter)writer;
        }
        sortedSetWriter.addValue(docID, value);
    }

    void addNumericField(FieldInfo fieldInfo, int docID, long value) {
        NumericDocValuesWriter numericWriter;
        DocValuesWriter writer = this.writers.get(fieldInfo.name);
        if (writer == null) {
            numericWriter = new NumericDocValuesWriter(fieldInfo, this.bytesUsed);
            this.writers.put(fieldInfo.name, numericWriter);
        } else {
            if (!(writer instanceof NumericDocValuesWriter)) {
                throw new IllegalArgumentException("Incompatible DocValues type: field \"" + fieldInfo.name + "\" changed from " + this.getTypeDesc(writer) + " to numeric");
            }
            numericWriter = (NumericDocValuesWriter)writer;
        }
        numericWriter.addValue(docID, value);
    }

    private String getTypeDesc(DocValuesWriter obj) {
        if (obj instanceof BinaryDocValuesWriter) {
            return "binary";
        }
        if (obj instanceof NumericDocValuesWriter) {
            return "numeric";
        }
        assert (obj instanceof SortedDocValuesWriter);
        return "sorted";
    }

    @Override
    public void abort() throws IOException {
        for (DocValuesWriter writer : this.writers.values()) {
            try {
                writer.abort();
            }
            catch (Throwable throwable) {}
        }
        this.writers.clear();
    }
}

