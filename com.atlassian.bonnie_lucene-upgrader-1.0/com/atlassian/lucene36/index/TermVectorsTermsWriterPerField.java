/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.analysis.tokenattributes.OffsetAttribute;
import com.atlassian.lucene36.document.Fieldable;
import com.atlassian.lucene36.index.ByteSliceReader;
import com.atlassian.lucene36.index.DocumentsWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInvertState;
import com.atlassian.lucene36.index.ParallelPostingsArray;
import com.atlassian.lucene36.index.TermVectorsTermsWriter;
import com.atlassian.lucene36.index.TermVectorsTermsWriterPerThread;
import com.atlassian.lucene36.index.TermsHashConsumerPerField;
import com.atlassian.lucene36.index.TermsHashPerField;
import com.atlassian.lucene36.store.DataOutput;
import com.atlassian.lucene36.store.RAMOutputStream;
import com.atlassian.lucene36.util.UnicodeUtil;
import java.io.IOException;

final class TermVectorsTermsWriterPerField
extends TermsHashConsumerPerField {
    final TermVectorsTermsWriterPerThread perThread;
    final TermsHashPerField termsHashPerField;
    final TermVectorsTermsWriter termsWriter;
    final FieldInfo fieldInfo;
    final DocumentsWriter.DocState docState;
    final FieldInvertState fieldState;
    boolean doVectors;
    boolean doVectorPositions;
    boolean doVectorOffsets;
    int maxNumPostings;
    OffsetAttribute offsetAttribute = null;

    public TermVectorsTermsWriterPerField(TermsHashPerField termsHashPerField, TermVectorsTermsWriterPerThread perThread, FieldInfo fieldInfo) {
        this.termsHashPerField = termsHashPerField;
        this.perThread = perThread;
        this.termsWriter = perThread.termsWriter;
        this.fieldInfo = fieldInfo;
        this.docState = termsHashPerField.docState;
        this.fieldState = termsHashPerField.fieldState;
    }

    int getStreamCount() {
        return 2;
    }

    boolean start(Fieldable[] fields, int count) {
        this.doVectors = false;
        this.doVectorPositions = false;
        this.doVectorOffsets = false;
        for (int i = 0; i < count; ++i) {
            Fieldable field = fields[i];
            if (!field.isIndexed() || !field.isTermVectorStored()) continue;
            this.doVectors = true;
            this.doVectorPositions |= field.isStorePositionWithTermVector();
            this.doVectorOffsets |= field.isStoreOffsetWithTermVector();
        }
        if (this.doVectors) {
            if (this.perThread.doc == null) {
                this.perThread.doc = this.termsWriter.getPerDoc();
                this.perThread.doc.docID = this.docState.docID;
                assert (this.perThread.doc.numVectorFields == 0);
                assert (0L == this.perThread.doc.perDocTvf.length());
                assert (0L == this.perThread.doc.perDocTvf.getFilePointer());
            }
            assert (this.perThread.doc.docID == this.docState.docID);
            if (this.termsHashPerField.numPostings != 0) {
                this.termsHashPerField.reset();
                this.perThread.termsHashPerThread.reset(false);
            }
        }
        return this.doVectors;
    }

    public void abort() {
    }

    void finish() throws IOException {
        assert (this.docState.testPoint("TermVectorsTermsWriterPerField.finish start"));
        int numPostings = this.termsHashPerField.numPostings;
        assert (numPostings >= 0);
        if (!this.doVectors || numPostings == 0) {
            return;
        }
        if (numPostings > this.maxNumPostings) {
            this.maxNumPostings = numPostings;
        }
        RAMOutputStream tvf = this.perThread.doc.perDocTvf;
        assert (this.fieldInfo.storeTermVector);
        assert (this.perThread.vectorFieldsInOrder(this.fieldInfo));
        this.perThread.doc.addField(this.termsHashPerField.fieldInfo.number);
        TermVectorsPostingsArray postings = (TermVectorsPostingsArray)this.termsHashPerField.postingsArray;
        int[] termIDs = this.termsHashPerField.sortPostings();
        tvf.writeVInt(numPostings);
        byte bits = 0;
        if (this.doVectorPositions) {
            bits = (byte)(bits | 1);
        }
        if (this.doVectorOffsets) {
            bits = (byte)(bits | 2);
        }
        ((DataOutput)tvf).writeByte(bits);
        int encoderUpto = 0;
        int lastTermBytesCount = 0;
        ByteSliceReader reader = this.perThread.vectorSliceReader;
        char[][] charBuffers = this.perThread.termsHashPerThread.charPool.buffers;
        for (int j = 0; j < numPostings; ++j) {
            int prefix;
            int termID = termIDs[j];
            int freq = postings.freqs[termID];
            char[] text2 = charBuffers[postings.textStarts[termID] >> 14];
            int start2 = postings.textStarts[termID] & 0x3FFF;
            UnicodeUtil.UTF8Result utf8Result = this.perThread.utf8Results[encoderUpto];
            UnicodeUtil.UTF16toUTF8(text2, start2, utf8Result);
            int termBytesCount = utf8Result.length;
            if (j > 0) {
                byte[] lastTermBytes = this.perThread.utf8Results[1 - encoderUpto].result;
                byte[] termBytes = this.perThread.utf8Results[encoderUpto].result;
                for (prefix = 0; prefix < lastTermBytesCount && prefix < termBytesCount && lastTermBytes[prefix] == termBytes[prefix]; ++prefix) {
                }
            }
            encoderUpto = 1 - encoderUpto;
            lastTermBytesCount = termBytesCount;
            int suffix = termBytesCount - prefix;
            tvf.writeVInt(prefix);
            tvf.writeVInt(suffix);
            ((DataOutput)tvf).writeBytes(utf8Result.result, prefix, suffix);
            tvf.writeVInt(freq);
            if (this.doVectorPositions) {
                this.termsHashPerField.initReader(reader, termID, 0);
                reader.writeTo(tvf);
            }
            if (!this.doVectorOffsets) continue;
            this.termsHashPerField.initReader(reader, termID, 1);
            reader.writeTo(tvf);
        }
        this.termsHashPerField.reset();
        this.perThread.termsHashPerThread.reset(false);
    }

    void shrinkHash() {
        this.termsHashPerField.shrinkHash(this.maxNumPostings);
        this.maxNumPostings = 0;
    }

    void start(Fieldable f) {
        this.offsetAttribute = this.doVectorOffsets ? this.fieldState.attributeSource.addAttribute(OffsetAttribute.class) : null;
    }

    void newTerm(int termID) {
        assert (this.docState.testPoint("TermVectorsTermsWriterPerField.newTerm start"));
        TermVectorsPostingsArray postings = (TermVectorsPostingsArray)this.termsHashPerField.postingsArray;
        postings.freqs[termID] = 1;
        if (this.doVectorOffsets) {
            int startOffset = this.fieldState.offset + this.offsetAttribute.startOffset();
            int endOffset = this.fieldState.offset + this.offsetAttribute.endOffset();
            this.termsHashPerField.writeVInt(1, startOffset);
            this.termsHashPerField.writeVInt(1, endOffset - startOffset);
            postings.lastOffsets[termID] = endOffset;
        }
        if (this.doVectorPositions) {
            this.termsHashPerField.writeVInt(0, this.fieldState.position);
            postings.lastPositions[termID] = this.fieldState.position;
        }
    }

    void addTerm(int termID) {
        assert (this.docState.testPoint("TermVectorsTermsWriterPerField.addTerm start"));
        TermVectorsPostingsArray postings = (TermVectorsPostingsArray)this.termsHashPerField.postingsArray;
        int n = termID;
        postings.freqs[n] = postings.freqs[n] + 1;
        if (this.doVectorOffsets) {
            int startOffset = this.fieldState.offset + this.offsetAttribute.startOffset();
            int endOffset = this.fieldState.offset + this.offsetAttribute.endOffset();
            this.termsHashPerField.writeVInt(1, startOffset - postings.lastOffsets[termID]);
            this.termsHashPerField.writeVInt(1, endOffset - startOffset);
            postings.lastOffsets[termID] = endOffset;
        }
        if (this.doVectorPositions) {
            this.termsHashPerField.writeVInt(0, this.fieldState.position - postings.lastPositions[termID]);
            postings.lastPositions[termID] = this.fieldState.position;
        }
    }

    void skippingLongTerm() {
    }

    ParallelPostingsArray createPostingsArray(int size) {
        return new TermVectorsPostingsArray(size);
    }

    static final class TermVectorsPostingsArray
    extends ParallelPostingsArray {
        int[] freqs;
        int[] lastOffsets;
        int[] lastPositions;

        public TermVectorsPostingsArray(int size) {
            super(size);
            this.freqs = new int[size];
            this.lastOffsets = new int[size];
            this.lastPositions = new int[size];
        }

        ParallelPostingsArray newInstance(int size) {
            return new TermVectorsPostingsArray(size);
        }

        void copyTo(ParallelPostingsArray toArray, int numToCopy) {
            assert (toArray instanceof TermVectorsPostingsArray);
            TermVectorsPostingsArray to = (TermVectorsPostingsArray)toArray;
            super.copyTo(toArray, numToCopy);
            System.arraycopy(this.freqs, 0, to.freqs, 0, this.size);
            System.arraycopy(this.lastOffsets, 0, to.lastOffsets, 0, this.size);
            System.arraycopy(this.lastPositions, 0, to.lastPositions, 0, this.size);
        }

        int bytesPerPosting() {
            return super.bytesPerPosting() + 12;
        }
    }
}

