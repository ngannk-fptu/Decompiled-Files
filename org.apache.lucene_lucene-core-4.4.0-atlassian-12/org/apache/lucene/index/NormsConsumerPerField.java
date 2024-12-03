/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.index.DocInverterPerField;
import org.apache.lucene.index.DocumentsWriterPerThread;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.InvertedDocEndConsumerPerField;
import org.apache.lucene.index.NormsConsumer;
import org.apache.lucene.index.NumericDocValuesWriter;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.search.similarities.Similarity;

final class NormsConsumerPerField
extends InvertedDocEndConsumerPerField
implements Comparable<NormsConsumerPerField> {
    private final FieldInfo fieldInfo;
    private final DocumentsWriterPerThread.DocState docState;
    private final Similarity similarity;
    private final FieldInvertState fieldState;
    private NumericDocValuesWriter consumer;

    public NormsConsumerPerField(DocInverterPerField docInverterPerField, FieldInfo fieldInfo, NormsConsumer parent) {
        this.fieldInfo = fieldInfo;
        this.docState = docInverterPerField.docState;
        this.fieldState = docInverterPerField.fieldState;
        this.similarity = this.docState.similarity;
    }

    @Override
    public int compareTo(NormsConsumerPerField other) {
        return this.fieldInfo.name.compareTo(other.fieldInfo.name);
    }

    @Override
    void finish() throws IOException {
        if (this.fieldInfo.isIndexed() && !this.fieldInfo.omitsNorms()) {
            if (this.consumer == null) {
                this.fieldInfo.setNormValueType(FieldInfo.DocValuesType.NUMERIC);
                this.consumer = new NumericDocValuesWriter(this.fieldInfo, this.docState.docWriter.bytesUsed);
            }
            this.consumer.addValue(this.docState.docID, this.similarity.computeNorm(this.fieldState));
        }
    }

    void flush(SegmentWriteState state, DocValuesConsumer normsWriter) throws IOException {
        int docCount = state.segmentInfo.getDocCount();
        if (this.consumer == null) {
            return;
        }
        this.consumer.finish(docCount);
        this.consumer.flush(state, normsWriter);
    }

    boolean isEmpty() {
        return this.consumer == null;
    }

    @Override
    void abort() {
    }
}

