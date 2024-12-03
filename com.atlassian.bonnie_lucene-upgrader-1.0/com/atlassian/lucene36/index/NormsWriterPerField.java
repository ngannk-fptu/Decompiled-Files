/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocInverterPerField;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInvertState;
import com.atlassian.lucene36.index.InvertedDocEndConsumerPerField;
import com.atlassian.lucene36.index.NormsWriterPerThread;
import com.atlassian.lucene36.util.ArrayUtil;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class NormsWriterPerField
extends InvertedDocEndConsumerPerField
implements Comparable<NormsWriterPerField> {
    final NormsWriterPerThread perThread;
    final FieldInfo fieldInfo;
    final DocumentsWriter.DocState docState;
    int[] docIDs = new int[1];
    byte[] norms = new byte[1];
    int upto;
    final FieldInvertState fieldState;

    public void reset() {
        this.docIDs = ArrayUtil.shrink(this.docIDs, this.upto);
        this.norms = ArrayUtil.shrink(this.norms, this.upto);
        this.upto = 0;
    }

    public NormsWriterPerField(DocInverterPerField docInverterPerField, NormsWriterPerThread perThread, FieldInfo fieldInfo) {
        this.perThread = perThread;
        this.fieldInfo = fieldInfo;
        this.docState = perThread.docState;
        this.fieldState = docInverterPerField.fieldState;
    }

    @Override
    void abort() {
        this.upto = 0;
    }

    @Override
    public int compareTo(NormsWriterPerField other) {
        return this.fieldInfo.name.compareTo(other.fieldInfo.name);
    }

    @Override
    void finish() {
        if (this.fieldInfo.isIndexed && !this.fieldInfo.omitNorms) {
            if (this.docIDs.length <= this.upto) {
                assert (this.docIDs.length == this.upto);
                this.docIDs = ArrayUtil.grow(this.docIDs, 1 + this.upto);
            }
            if (this.norms.length <= this.upto) {
                assert (this.norms.length == this.upto);
                this.norms = ArrayUtil.grow(this.norms, 1 + this.upto);
            }
            float norm = this.docState.similarity.computeNorm(this.fieldInfo.name, this.fieldState);
            this.norms[this.upto] = this.docState.similarity.encodeNormValue(norm);
            this.docIDs[this.upto] = this.docState.docID;
            ++this.upto;
        }
    }
}

